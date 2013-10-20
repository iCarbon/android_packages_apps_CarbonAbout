/*
 * Copyright (C) 2013 Carbon Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.carbon.about.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.carbon.about.R;
import com.carbon.about.fragments.*;

public class AboutActivity extends FragmentActivity {

    public static Context appContext;

    protected PagerTabStrip mPagerTabStrip;
    protected ViewPager mViewPager;

    String titleString[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pager_tab);

        appContext = getApplicationContext();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);

        TabsAdapter TabsAdapter = new TabsAdapter(getFragmentManager());
        mViewPager.setAdapter(TabsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    class TabsAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public TabsAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new AboutFragment();
            frags[1] = new AboutDevelopers();
            frags[2] = new AboutMaintainers();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    private String[] getTitles() {
        titleString = new String[]{
                getString(R.string.about_title),
                getString(R.string.carbon_devs),
                getString(R.string.carbon_maintainers)};
        return titleString;
    }
}
