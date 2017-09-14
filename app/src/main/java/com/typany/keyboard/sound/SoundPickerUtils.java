package com.typany.keyboard.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangfeng on 2017/9/13.
 */

public class SoundPickerUtils {
    private static final String TAG = SoundPickerUtils.class.getSimpleName();

    public static List<String> getShortSoundList(Context appContext, int count) {
        String soundFolder = "sound/suite";
        AssetFileDescriptor descriptor = null;
        try {
            AssetManager am = appContext.getAssets();
            String[] subList = am.list(soundFolder);
            if (null == subList) {
                Log.w(TAG, "getShortSoundList, no file in " + soundFolder);
            } else {
                ArrayList<String> result = new ArrayList<>();
                for (String s : subList) {
                    result.add(s);
                }
                Log.d(TAG, "getShortSoundList, sub file no: " + subList.length);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return Collections.emptyList();
    }
}
