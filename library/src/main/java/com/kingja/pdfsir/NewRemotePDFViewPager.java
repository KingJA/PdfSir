/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kingja.pdfsir;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kingja.pdfsir.adapter.PDFPagerAdapter;
import com.kingja.pdfsir.remote.DownloadFile;
import com.kingja.pdfsir.remote.DownloadFileUrlConnectionImpl;
import com.kingja.pdfsir.util.FileUtil;

import java.io.File;

import es.voghdev.pdfviewpager.library.R;


public class NewRemotePDFViewPager extends ViewPager implements DownloadFile.Listener {
    protected Context context;
    protected DownloadFile downloadFile;
    protected DownloadFile.Listener listener;
    private String pdfUrl;
    private PDFPagerAdapter adapter;

    public NewRemotePDFViewPager(Context context, String pdfUrl, DownloadFile.Listener listener) {
        super(context);
        this.context = context;
        this.listener = listener;

        init(new DownloadFileUrlConnectionImpl(context, new Handler(), this), pdfUrl);
    }

    public NewRemotePDFViewPager(@NonNull Context context) {
        this(context, null);
    }

    public NewRemotePDFViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNewRemotePDFViewPager();
    }

    private void initNewRemotePDFViewPager() {
        setPageTransformer(true, new DefaultTransformer());
        initDownloader(new DownloadFileUrlConnectionImpl(context, new Handler(), this));
    }

    private void initDownloader(DownloadFileUrlConnectionImpl downloadFile) {
        this.downloadFile = downloadFile;
    }

    public void setUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public void setDownloadFileListener(DownloadFile.Listener listener) {
        this.listener = listener;
    }

    public void start() {
        downloadFile.download(pdfUrl, new File(getContext().getCacheDir(), FileUtil.extractFileNameFromURL(pdfUrl))
                .getAbsolutePath());
    }


    public NewRemotePDFViewPager(Context context,
                                 DownloadFile downloadFile,
                                 String pdfUrl,
                                 DownloadFile.Listener listener) {
        super(context);
        this.context = context;
        this.listener = listener;

        init(downloadFile, pdfUrl);
    }


    private void init(DownloadFile downloadFile, String pdfUrl) {
        setPageTransformer(true, new DefaultTransformer());
        setDownloader(downloadFile);
        downloadFile.download(pdfUrl,
                new File(context.getCacheDir(), FileUtil.extractFileNameFromURL(pdfUrl)).getAbsolutePath());
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a;

            a = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager);
            String pdfUrl = a.getString(R.styleable.PDFViewPager_pdfUrl);

            if (pdfUrl != null && pdfUrl.length() > 0) {
                init(new DownloadFileUrlConnectionImpl(context, new Handler(), this), pdfUrl);
            }

            a.recycle();
        }
    }

    private void setDownloader(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapter = new PDFPagerAdapter(getContext(), FileUtil.extractFileNameFromURL(url));
        setAdapter(adapter);
        listener.onSuccess(url, destinationPath, adapter.getCount());
    }

    @Override
    public void onFailure(Exception e) {
        listener.onFailure(e);
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        listener.onProgressUpdate(progress, total);
    }

    /**
     * PDFViewPager uses PhotoView, so this bugfix should be added
     * Issue explained in https://github.com/chrisbanes/PhotoView
     */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        try {
//            return super.onInterceptTouchEvent(ev);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(swapEvent(ev));
        swapEvent(ev);
        return intercept;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapEvent(ev));
    }

    private MotionEvent swapEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();
        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;
        event.setLocation(swappedX, swappedY);
        return event;
    }


    public class DefaultTransformer implements PageTransformer {
        public static final String TAG = "simple";

        @Override
        public void transformPage(View view, float position) {
            float alpha = 0;
            if (0 <= position && position <= 1) {
                alpha = 1 - position;
            } else if (-1 < position && position < 0) {
                alpha = position + 1;
            }
            view.setAlpha(alpha);
            float transX = view.getWidth() * -position;
            view.setTranslationX(transX);
            float transY = position * view.getHeight();
            view.setTranslationY(transY);
        }
    }


    public class NullListener implements DownloadFile.Listener {
        public void onSuccess(String url, String destinationPath) {
            /* Empty */
        }

        public void onFailure(Exception e) {
            /* Empty */
        }

        public void onProgressUpdate(int progress, int total) {
            /* Empty */
        }
    }

    public void release() {
        if (adapter != null) {
            adapter.close();
        }
    }
}
