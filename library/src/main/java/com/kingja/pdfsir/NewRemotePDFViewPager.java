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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.kingja.pdfsir.adapter.PDFPagerAdapter;
import com.kingja.pdfsir.remote.DownloadFile;
import com.kingja.pdfsir.remote.DownloadFileUrlConnectionImpl;
import com.kingja.pdfsir.util.FileUtil;

import java.io.File;

import es.voghdev.pdfviewpager.library.R;


public class NewRemotePDFViewPager extends ViewPager implements DownloadFile.Listener {
    private static final String TAG = "NewRemotePDFViewPager";
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

    private GestureDetector mGestureDetector;

    private void initNewRemotePDFViewPager() {
        setPageTransformer(true, new DefaultTransformer());
        initDownloader(new DownloadFileUrlConnectionImpl(context, new Handler(), this));
        mGestureDetector = new GestureDetector(getContext(), new YScrollDetector());
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

    private void init(DownloadFile downloadFile, String pdfUrl) {
        setPageTransformer(true, new DefaultTransformer());
        setDownloader(downloadFile);
        downloadFile.download(pdfUrl,
                new File(context.getCacheDir(), FileUtil.extractFileNameFromURL(pdfUrl)).getAbsolutePath());
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {
            getParent().requestDisallowInterceptTouchEvent(false);
            Log.e(TAG, "捕捉水平滑动: " );
            return false;
        }
        boolean intercept = super.onInterceptTouchEvent(swapEvent(ev));
        swapEvent(ev);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {
            Log.e(TAG, "捕捉水平滑动: " );
            getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        }
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

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) < Math.abs(distanceX)) {
                Log.e(TAG, "水平滑动: ");
            } else {
                Log.e(TAG, "垂直滑动: ");
            }
            return Math.abs(distanceY) < Math.abs(distanceX);
        }
    }
}
