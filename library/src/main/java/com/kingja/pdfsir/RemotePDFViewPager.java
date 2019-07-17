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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

import es.voghdev.pdfviewpager.library.R;
import com.kingja.pdfsir.remote.DownloadFile;
import com.kingja.pdfsir.remote.DownloadFileUrlConnectionImpl;
import com.kingja.pdfsir.util.FileUtil;


public class RemotePDFViewPager extends ViewPager implements DownloadFile.Listener {
    protected Context context;
    protected DownloadFile downloadFile;
    protected DownloadFile.Listener listener;

    public RemotePDFViewPager(Context context, String pdfUrl, DownloadFile.Listener listener) {
        super(context);
        this.context = context;
        this.listener = listener;

        init(new DownloadFileUrlConnectionImpl(context, new Handler(), this), pdfUrl);
    }

    public RemotePDFViewPager(Context context,
                              DownloadFile downloadFile,
                              String pdfUrl,
                              DownloadFile.Listener listener) {
        super(context);
        this.context = context;
        this.listener = listener;

        init(downloadFile, pdfUrl);
    }

    public RemotePDFViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init(attrs);
    }

    private void init(DownloadFile downloadFile, String pdfUrl) {
        //设置viewpage的切换动画,这里设置才能真正实现垂直滑动的viewpager
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

    public void setDownloader(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        listener.onSuccess(url, destinationPath);
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
    /**
     * 拦截touch事件
     * @param ev 获取事件类型的封装类MotionEvent
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(swapEvent(ev));
        swapEvent(ev);
        return intercept;
    }


    /**
     * 触摸点击触发该方法
     * @param ev 获取事件类型的封装类MotionEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapEvent(ev));
    }

    /**
     * 交换x轴和y轴的移动距离
     * @param event 获取事件类型的封装类MotionEvent
     */
    private MotionEvent swapEvent(MotionEvent event) {
        //获取宽高
        float width = getWidth();
        float height = getHeight();
        //将Y轴的移动距离转变成X轴的移动距离
        float swappedX = (event.getY() / height) * width;
        //将X轴的移动距离转变成Y轴的移动距离
        float swappedY = (event.getX() / width) * height;
        //重设event的位置
        event.setLocation(swappedX, swappedY);
        return event;
    }


    /**
     * 自定义 ViewPager 切换动画
     * 如果不设置切换动画，还会是水平方向的动画
     */
    public class DefaultTransformer implements ViewPager.PageTransformer {
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
}
