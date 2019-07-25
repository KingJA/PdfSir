package sample.kingja.pdfsir;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingja.pdfsir.NewRemotePDFViewPager;
import com.kingja.pdfsir.remote.DownloadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.voghdev.pdfviewpager.R;

/**
 * Description:TODO
 * Create Time:2019/7/25 0025 上午 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ViewPagerRemotePDFActivity extends AppCompatActivity {

    private static final String TAG = "RemotePDFActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vp_pdf);
        ViewPager vp = findViewById(R.id.vp);
        TextView tv_vp_page = findViewById(R.id.tv_vp_page);
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            urlList.add("http://app-h5.yalangke.vip/aaa.pdf");
        }
        PdfPagerAdapter pdfPagerAdapter = new PdfPagerAdapter(this, urlList);
        vp.setAdapter(pdfPagerAdapter);
        int totalPages = pdfPagerAdapter.getCount();
        tv_vp_page.setText(String.format("%d/%d",1,totalPages));
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                tv_vp_page.setText(String.format("%d/%d",position+1,totalPages));
            }
        });
    }

    class PdfPagerAdapter extends PagerAdapter {

        private Map<Integer, View> previewViewMap = new HashMap();

        public PdfPagerAdapter(Context context, List<String> urlList) {
            for (int i = 0; i < urlList.size(); i++) {
                previewViewMap.put(i, getPdfRootView(context,urlList.get(i)));
            }
        }

        private View getPdfRootView(Context context, String url) {
            View rootView = View.inflate(context, R.layout.layout_pdf, null);
            TextView tv_tip = rootView.findViewById(R.id.tv_tip);
            TextView tvPage = rootView.findViewById(R.id.tv_page);
            LinearLayout ssll_download = rootView.findViewById(R.id.ssll_download);
            ssll_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewRemotePDFViewPager remotePDFViewPager = rootView.findViewById(R.id.pdfView);
                    remotePDFViewPager.setUrl(url);
                    remotePDFViewPager.setDownloadFileListener(new DownloadFile.Listener() {
                        @Override
                        public void onSuccess(String url, String destinationPath, int totalPage) {
                            ssll_download.setVisibility(View.GONE);
                            tvPage.setVisibility(View.VISIBLE);
                            tvPage.setText(String.format("%d/%d", 1, totalPage));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            tv_tip.setText("文件下载失败");
                        }

                        @Override
                        public void onProgressUpdate(int progress, int total) {
                            tv_tip.setText(String.format("%d/%d", progress, total));
                        }
                    });
                    remotePDFViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
//                            tvPage.setText(String.format("%d/%d", position + 1, totalPage));
                            Log.e(TAG, "onPageSelected: "+position );
                        }
                    });
                    remotePDFViewPager.start();
                }
            });

            return rootView;
        }

        @Override
        public int getCount() {
            return previewViewMap.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View previewView = previewViewMap.get(position);
            ViewParent parent = previewView.getParent();
            if (parent != null) {
                ((ViewPager) previewView.getParent()).removeView(previewView);
                if (((ViewPager) parent).getChildCount() < previewViewMap.size()) {
                    container.addView(previewView);
                }
            } else {
                container.addView(previewView);
            }
            return previewView;
        }
    }

    public static void open(Context context) {
        Intent i = new Intent(context, ViewPagerRemotePDFActivity.class);
        context.startActivity(i);
    }
}
