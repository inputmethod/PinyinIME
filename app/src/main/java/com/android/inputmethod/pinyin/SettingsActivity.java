/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.inputmethod.pinyin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.android.inputmethod.utils.ImmUtils;
import com.typany.keyboard.sound.SoundPickerUtils;
import com.typany.resource.ResourceManager;
import com.typany.resource.SoundHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Setting activity of Pinyin IME.
 */
public class SettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static String TAG = "SettingsActivity";

    private CheckBoxPreference mKeySoundPref;
    private CheckBoxPreference mVibratePref;
    private CheckBoxPreference mPredictionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        ResourceManager.getInstance().onCreate(getApplicationContext());
        soundHolder = ResourceManager.getInstance().sound;

        PreferenceScreen prefSet = getPreferenceScreen();

        mKeySoundPref = (CheckBoxPreference) prefSet
                .findPreference(getString(R.string.setting_sound_key));
        mVibratePref = (CheckBoxPreference) prefSet
                .findPreference(getString(R.string.setting_vibrate_key));
        mPredictionPref = (CheckBoxPreference) prefSet
                .findPreference(getString(R.string.setting_prediction_key));

        mKeySoundPref.setOnPreferenceChangeListener(this);
        
        prefSet.setOnPreferenceChangeListener(this);
        
        Settings.getInstance(PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext()));

        updatePreference(prefSet, getString(R.string.setting_advanced_key));
        
        updateWidgets();

        loadSoundAssets();

        Context appContext = getApplicationContext();
        if (ImmUtils.isImeEnabled(appContext, PinyinIME.class)) {
            if (ImmUtils.isImeDefault(appContext, PinyinIME.class)) {
                // do nothing now.
            } else {
                ImmUtils.showInputmethodPicker(appContext);
            }
        } else {
            ImmUtils.showInputmethodSetting(appContext);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWidgets();
        showSoundInfo();
    }

    @Override
    protected void onDestroy() {
        Settings.releaseInstance();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Settings.setKeySound(mKeySoundPref.isChecked());
        Settings.setVibrate(mVibratePref.isChecked());
        Settings.setPrediction(mPredictionPref.isChecked());

        Settings.writeBack();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        soundHolder.playKeyTone(1.0f);
        applySound();

        if ("setting_sound_key".equals(preference.getKey())) {
            ImmUtils.showIme(getApplicationContext());
        }

        return true;
    }

    private void updateWidgets() {
        mKeySoundPref.setChecked(Settings.getKeySound());
        mVibratePref.setChecked(Settings.getVibrate());
        mPredictionPref.setChecked(Settings.getPrediction());
    }

    public void updatePreference(PreferenceGroup parentPref, String prefKey) {
        Preference preference = parentPref.findPreference(prefKey);
        if (preference == null) {
            return;
        }
        Intent intent = preference.getIntent();
        if (intent != null) {
            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            int listSize = list.size();
            if (listSize == 0)
                parentPref.removePreference(preference);
        }
    }

    private SoundHolder soundHolder;
    private int currentIndex = 0;
    private final List<String> soundList = new ArrayList<>();
    private void loadSoundAssets() {
        if (soundList.isEmpty()) {
            List<String> loadList = SoundPickerUtils.getShortSoundList(getApplicationContext(), 11);
            if (null != loadList) {
                soundList.addAll(loadList);
            }
        }
    }

    private void showSoundInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : soundList) {
            stringBuilder.append(s).append("\n");
        }
        stringBuilder.append("current index:").append(currentIndex);
        Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();

        applySound();
    }

    private void applySound() {
        soundHolder.reloadAssert(getApplicationContext(), soundList.get(currentIndex));
        soundHolder.playCurrentPreview(1.0f);
        currentIndex = (++currentIndex) % soundList.size();
    }

}
