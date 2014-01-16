/*
 * Copyright (C) 2012 Android Open Kang Project
 * This code has been modified.  Portions copyright (C) 2013 Carbon Development
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


package com.carbon.about.widget;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Point;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.carbon.about.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeveloperPreference extends Preference implements OnMenuItemClickListener {
    private static final String TAG = "DeveloperPreference";

    private ImageView popupMenuButton;
    private ImageView gplusButton;
    private ImageView donateButton;
    private ImageView githubButton;
    private ImageView photoView;

    private PopupMenu popupMenu;

    private Drawable devIcon;
    private TextView devName;
    private TextView devStatus;

    private String nameDev;
    private String statusDev;
    private String emailDev;
    private String googleNumber;
    private String donateLink;
    private String githubLink;
    private String twitterLink;
    private String devUrl;
    private final Display mDisplay;

    private static final int MENU_GITHUB = 0;
    private static final int MENU_TWITTER = 1;
    private static final int MENU_DONATE = 2;
    private static final int MENU_EMAIL = 3;

    public DeveloperPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeveloperPreference);
            nameDev = typedArray.getString(R.styleable.DeveloperPreference_nameDev);
            statusDev = typedArray.getString(R.styleable.DeveloperPreference_statusDev);
            googleNumber = typedArray.getString(R.styleable.DeveloperPreference_googleHandle);
            twitterLink = typedArray.getString(R.styleable.DeveloperPreference_twitterLink);
            donateLink = typedArray.getString(R.styleable.DeveloperPreference_donateLink);
            githubLink = typedArray.getString(R.styleable.DeveloperPreference_githubLink);
            devIcon = typedArray.getDrawable(R.styleable.DeveloperPreference_devIcon);
            devUrl = typedArray.getString(R.styleable.DeveloperPreference_devUrl);
            emailDev = typedArray.getString(R.styleable.DeveloperPreference_emailDev);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        View layout = View.inflate(getContext(), R.layout.dev_card, null);
        gplusButton = (ImageView) layout.findViewById(R.id.gplus_button);
        devName = (TextView) layout.findViewById(R.id.name);
        devStatus = (TextView) layout.findViewById(R.id.status);
        photoView = (ImageView) layout.findViewById(R.id.photo);
        popupMenuButton = (ImageView) layout.findViewById(R.id.anchor);
        popupMenu = new PopupMenu(getContext(), popupMenuButton);
        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (devIcon != null) {
            photoView.setImageDrawable(devIcon);
        } else if (devUrl != null) {
            final String url = "https://plus.google.com/s2/photos/profile/" + devUrl;
            UrlImageViewHelper.setUrlDrawable(this.photoView, url, R.drawable.icon,
                    UrlImageViewHelper.CACHE_DURATION_TWO_DAYS);
        }

        final OnClickListener openPopupMenu = new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        };
        popupMenuButton.setOnClickListener(openPopupMenu);

        if (donateLink != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_DONATE, 0, R.string.donate);
        }
        if (githubLink != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_GITHUB, 0, R.string.github);
        }
        if (twitterLink != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_TWITTER, 0, R.string.twitter);
        }
        if (emailDev != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_EMAIL, 0, R.string.email);
        }

        popupMenu.setOnMenuItemClickListener(this);

        if (googleNumber != null) {
            final OnPreferenceClickListener openGplus = new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri gplusURL = Uri.parse("https://plus.google.com/u/0/" + googleNumber);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, gplusURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    return true;
                }
            };

            // changed to clicking the preference to open twitter
            // it was a hit or miss to click the twitter bird
            this.setOnPreferenceClickListener(openGplus);
        } else {
            gplusButton.setVisibility(View.INVISIBLE);
        }
        devName.setText(nameDev);
        devStatus.setText(statusDev);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_GITHUB:
                Uri githubURL = Uri.parse(githubLink);
                final Intent ghintent = new Intent(Intent.ACTION_VIEW, githubURL);
                ghintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(ghintent);
                break;
            case MENU_TWITTER:
                Uri twitterURL = Uri.parse("http://twitter.com/" + twitterLink);
                final Intent twintent = new Intent(Intent.ACTION_VIEW, twitterURL);
                twintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(twintent);
                break;
            case MENU_DONATE:
                Uri donateURL = Uri.parse(donateLink);
                final Intent donintent = new Intent(Intent.ACTION_VIEW, donateURL);
                donintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(donintent);
                break;
            case MENU_EMAIL:
                final Intent emaintent = new Intent(Intent.ACTION_SEND);
                emaintent.setType("message/rfc822");
                emaintent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailDev});
                emaintent.putExtra(Intent.EXTRA_SUBJECT, "");
                emaintent.putExtra(Intent.EXTRA_TEXT, "");
                emaintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(emaintent);
                break;
        }
        return false;
    }
}
