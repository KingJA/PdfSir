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



import es.voghdev.pdfviewpager.R;

import com.kingja.pdfsir.PDFViewPager;
import com.kingja.pdfsir.adapter.BasePDFPagerAdapter;

public class AssetOnXMLActivity extends BaseSampleActivity {
    PDFViewPager pdfViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.asset_on_xml);
        setContentView(R.layout.activity_asset_on_xml);

        pdfViewPager = (PDFViewPager) findViewById(R.id.pdfViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((BasePDFPagerAdapter) pdfViewPager.getAdapter()).close();
    }

    public static void open(Context context) {
        Intent i = new Intent(context, AssetOnXMLActivity.class);
        context.startActivity(i);
    }
}