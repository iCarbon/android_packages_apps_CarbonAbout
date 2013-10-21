package com.carbon.about.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.carbon.about.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeveloperPreference extends Preference implements OnClickListener, OnMenuItemClickListener {
    private static final String TAG = "DeveloperPreference";
    public static final String GRAVATAR_API = "http://www.gravatar.com/avatar/";
    public static int mDefaultAvatarSize = 400;
    private ImageView twitterButton;
    private ImageView donateButton;
    private ImageView githubButton;
    private ImageView photoView;
    private PopupMenu popupMenu;

    private TextView devName;

    private String nameDev;
    private String twitterName;
    private String donateLink;
    private String githubLink;
    private String devEmail;
    private final Display mDisplay;

    private static final int MENU_GOOGLE = 0;
    private static final int MENU_GITHUB = 1;
    private static final int MENU_TWITTER = 2;

    public DeveloperPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeveloperPreference);
            nameDev = typedArray.getString(R.styleable.DeveloperPreference_nameDev);
            twitterName = typedArray.getString(R.styleable.DeveloperPreference_twitterHandle);
            donateLink = typedArray.getString(R.styleable.DeveloperPreference_donateLink);
            githubLink = typedArray.getString(R.styleable.DeveloperPreference_githubLink);
            devEmail = typedArray.getString(R.styleable.DeveloperPreference_emailDev);
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
        twitterButton = (ImageView) layout.findViewById(R.id.twitter_button);
        //donateButton = (ImageView) layout.findViewById(R.id.donate_button);
        //githubButton = (ImageView) layout.findViewById(R.id.github_button);
        devName = (TextView) layout.findViewById(R.id.name);
        photoView = (ImageView) layout.findViewById(R.id.photo);
        popupMenu = new PopupMenu(getContext(), layout.findViewById(R.id.anchor));
        layout.findViewById(R.id.anchor).setOnClickListener(this);
        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        popupMenu.setOnMenuItemClickListener(this);
        if (donateLink != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_GOOGLE, Menu.NONE, "Item 1");
        } else {
            donateButton.setVisibility(View.GONE);
        }

        if (githubLink != null) {
            popupMenu.getMenu().add(Menu.NONE, MENU_GITHUB, Menu.NONE, "Item 2");
        } else {
            githubButton.setVisibility(View.GONE);
        }

        if (twitterName != null) {
            final OnPreferenceClickListener openTwitter = new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri twitterURL = Uri.parse("http://twitter.com/#!/" + twitterName);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, twitterURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    return true;
                }
            };

            // changed to clicking the preference to open twitter
            // it was a hit or miss to click the twitter bird
            this.setOnPreferenceClickListener(openTwitter);
            UrlImageViewHelper.setUrlDrawable(this.photoView,
                    getGravatarUrl(devEmail),
                    R.drawable.icon,
                    UrlImageViewHelper.CACHE_DURATION_ONE_WEEK);
        } else {
            twitterButton.setVisibility(View.INVISIBLE);
            photoView.setVisibility(View.GONE);
        }
        devName.setText(nameDev);
    }

    @Override
    public void onClick(View v) {
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_GOOGLE:
                Uri donateURL = Uri.parse(donateLink);
                final Intent donintent = new Intent(Intent.ACTION_VIEW, donateURL);
                donintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(donintent);
                break;
            case MENU_GITHUB:
                Uri githubURL = Uri.parse(githubLink);
                final Intent ghintent = new Intent(Intent.ACTION_VIEW, githubURL);
                ghintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(ghintent);
                break;
        }
        return false;
    }


    public String getGravatarUrl(String email) {
        try {
            String emailMd5 = getMd5(email.trim().toLowerCase());
            return String.format("%s%s?s=%d&d=mm",
                    GRAVATAR_API,
                    emailMd5,
                    mDefaultAvatarSize);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String getMd5(String devEmail) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(devEmail.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++)
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }
}
