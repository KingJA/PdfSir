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
package sample.kingja.pdfsir;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingja.pdfsir.NewRemotePDFViewPager;
import com.kingja.pdfsir.remote.DownloadFile;

import es.voghdev.pdfviewpager.R;

public class NewRemotePDFActivity extends BaseSampleActivity {
    private static final String TAG = "RemotePDFActivity";
    LinearLayout root;
    EditText etPdfUrl;
    Button btnDownload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.remote_pdf_example);
        setContentView(R.layout.activity_remote_pdf);

        root = (LinearLayout) findViewById(R.id.remote_pdf_root);
        etPdfUrl = (EditText) findViewById(R.id.et_pdfUrl);
        btnDownload = (Button) findViewById(R.id.btn_download);

        setDownloadButtonListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//
//        if (adapter != null) {
//            adapter.close();
//        }
    }

    protected void setDownloadButtonListener() {
        final Context ctx = this;
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tvPage = findViewById(R.id.tvPage);
                NewRemotePDFViewPager    remotePDFViewPager = new NewRemotePDFViewPager(ctx, getUrlFromEditText(), new DownloadFile.Listener() {



                    @Override
                    public void onSuccess(String url, String destinationPath,int totalPage) {
                        Log.e(TAG, "onSuccess: " );
                        showDownloadButton();
                        tvPage.setVisibility(View.VISIBLE);
                        tvPage.setText(String.format("%d/%d",1,totalPage));

                    }

                    @Override
                    public void onFailure(Exception e) {
                        showDownloadButton();
                    }

                    @Override
                    public void onProgressUpdate(int progress, int total) {
                        Log.e(TAG, "progress: " +progress+" total: " +total);
                    }
                });
                remotePDFViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageSelected(int position) {
                    }
                });
                root.addView(remotePDFViewPager,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    protected String getUrlFromEditText() {
        return etPdfUrl.getText().toString().trim();
    }

    public static void open(Context context) {
        Intent i = new Intent(context, NewRemotePDFActivity.class);
        context.startActivity(i);
    }

    public void showDownloadButton() {
        btnDownload.setVisibility(View.VISIBLE);
    }


}

