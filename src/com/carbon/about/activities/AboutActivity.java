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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.carbon.about.R;
import com.carbon.about.fragments.*;
import com.carbon.about.widget.CustomDrawerLayout;

public class AboutActivity extends Fragment {

    public static Context appContext;

    //==================================
    // Drawer
    //==================================
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private ActionBarDrawerToggle mDrawerToggle;
    private CustomDrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    private static int DRAWER_MODE = 0;
    private SharedPreferences mPreferences;

    private static final int MENU_DRAWER = Menu.FIRST;

    String titleString[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //appContext = getApplicationContext();

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView;

        rootView = (ViewGroup) inflater.inflate(R.layout.drawer_main, container, false);

        mDrawerListView = (ListView) rootView.findViewById(R.id.dw_navigation_drawer);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                getTitles()));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        setUpNavigationDrawer(
                rootView.findViewById(R.id.dw_navigation_drawer),
                (CustomDrawerLayout) rootView.findViewById(R.id.dw_drawer_layout));

        if (container instanceof PreferenceFrameLayout) {
            ((PreferenceFrameLayout.LayoutParams) rootView.getLayoutParams()).removeBorders = true;
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_DRAWER, 0, R.string.navigation_drawer_toggle)
                .setIcon(R.drawable.ic_drawer)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        restoreActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent();
                homeIntent.setClassName("com.carbon.fibers", "com.carbon.fibers.SettingsActivity");
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            case MENU_DRAWER:
                if (isDrawerOpen())
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                else
                    mDrawerLayout.openDrawer(mFragmentContainerView);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //==================================
    // Methods
    //==================================

    /**
     * Users of this fragment must call this method to set up the
     * navigation menu_drawer interactions.
     *
     * @param fragmentContainerView The view of this fragment in its activity's layout.
     * @param drawerLayout          The DrawerLayout containing this fragment's UI.
     */
    public void setUpNavigationDrawer(View fragmentContainerView, CustomDrawerLayout drawerLayout) {
        mFragmentContainerView = fragmentContainerView;
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Remove or set it to true, if you want to use home to toggle the menu_drawer
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        if (!mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        selectItem(mCurrentSelectedPosition);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Restores the action bar after closing the menu_drawer
     */
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getActivity().getTitle());
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.dw_container, PlaceholderFragment.newInstance(getPosition(position)))
                .commit();
    }

    /**
     * Depending on if the item is shown or not, it increases
     * the position to make the activity load the right fragment.
     *
     * @param pos The selected position
     * @return the modified position
     */
    public int getPosition(int pos) {
        int position = pos;
        switch (DRAWER_MODE) {
            default:
            case 0:
                position = pos;
                break;
            case 1:
                if (pos > 0) position = pos + 1;
                break;
            case 2:
                if (pos > 3) position = pos + 1;
                break;
            case 3:
                if (pos > 0) position = pos + 1;
                if (pos > 3) position = pos + 2;
                break;
        }
        return position;
    }

    /**
     * Get a list of titles for the tabstrip to display depending on if the
     * voltage control fragment and battery fragment will be displayed. (Depends on the result of
     * Helpers.voltageTableExists() & Helpers.showBattery()
     *
     * @return String[] containing titles
     */
    private String[] getTitles() {
        String titleString[];
        DRAWER_MODE = 0;
        titleString = new String[]{
                getString(R.string.about_title),
                getString(R.string.carbon_main_members),
                getString(R.string.carbon_maintainers)};
        return titleString;
    }

    //==================================
    // Internal Classes
    //==================================

    /**
     * Loads our Fragments.
     */

    public static final int FRAGMENT_ID_ABOUT = 0;
    public static final int FRAGMENT_ID_CORE_TEAM = 1;
    public static final int FRAGMENT_ID_MAINTAINERS = 2;
    // public static final int FRAGMENT_ID_CREDITS = 3;
    // public static final int FRAGMENT_ID_CHANGELOG = 4;

    public static class PlaceholderFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int fragmentId) {
            Fragment fragment;
            switch (fragmentId) {
                default:
                case FRAGMENT_ID_ABOUT:
                    fragment = new AboutFragments();
                    break;
                case FRAGMENT_ID_CORE_TEAM:
                    fragment = new AboutCoreMembers();
                    break;
                case FRAGMENT_ID_MAINTAINERS:
                    fragment = new AboutMaintainers();
                    break;
            }

            return fragment;
        }

        public PlaceholderFragment() {
            // intentionally left blank
        }
    }
}
