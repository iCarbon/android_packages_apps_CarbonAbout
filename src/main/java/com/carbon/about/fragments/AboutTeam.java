package com.carbon.about.fragments;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.Collections;

import com.carbon.about.R;

public class AboutTeam extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_team);

        PreferenceGroup devsGroup = (PreferenceGroup) findPreference("devs");
        ArrayList<Preference> devs = new ArrayList<Preference>();
        for (int i = 0; i < devsGroup.getPreferenceCount(); i++) {
            devs.add(devsGroup.getPreference(i));
        }
        devsGroup.removeAll();
        devsGroup.setOrderingAsAdded(false);
        Collections.shuffle(devs);
        for (int i = 0; i < devs.size(); i++) {
            Preference p = devs.get(i);
            p.setOrder(i);
            devsGroup.addPreference(p);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void launchActivity(String packageName, String activity)
            throws ActivityNotFoundException {
       Intent launch = new Intent();
       launch.setComponent(new ComponentName(packageName, packageName + activity));
       launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       getActivity().startActivity(launch);
   }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent website = new Intent(Intent.ACTION_VIEW, uriUrl);
        getActivity().startActivity(website);
    }
}
