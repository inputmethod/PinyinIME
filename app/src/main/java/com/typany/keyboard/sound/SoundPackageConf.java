package com.typany.keyboard.sound;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by yangfeng on 2017/9/14.
 */

public class SoundPackageConf {
    private static final String TAG = SoundPackageConf.class.getSimpleName();

    public String folder;
    public String name;

    // file name
    public String defaultFileName;
    public String keyFileName;
    public String funcFileName;
    public String toolFileName;
    public String candidateFileName;
    public String previewFileName;

    // sound track file name list ready for play, in the order of put into map.
    public final List<String> trackFileNames = new ArrayList<>();

    // map file name to its track index
    public final Map<String, Integer> nameToTrackId = new HashMap<>();

    // map key code to sound file name so any key could be configured to a specific sound, if
    // a key is not configured, then it will be map to either the category sound file name or
    // default file name. if none of these file name existing, no sound play for that key.
    public final Map<Integer, String> keyToFileName = new HashMap<>();

    public int getPreviewTrack() {
        int trackId = -1;
        if (nameToTrackId.containsKey(previewFileName)) {
            trackId = nameToTrackId.get(previewFileName);
        }
        Log.d(TAG, "getPreviewTrack " + trackId + " for " + previewFileName);
        return trackId;
    }

    public List<String> getAllFileNameList() {
        Log.d(TAG, "unique file list size " + trackFileNames.size() + ", map size " + nameToTrackId.size()
                + ", key map size " + keyToFileName.size());
        return trackFileNames;
    }

    private void tryAddUniqueFileName(String s) {
        if (!TextUtils.isEmpty(s)) {
            if (!trackFileNames.contains(s)) {
                trackFileNames.add(s);
            }
        }
    }

    public void checkFileExisting(ArrayList<String> fileList) {
        checkFileExisting(fileList, defaultFileName);
        checkFileExisting(fileList, keyFileName);
        checkFileExisting(fileList, funcFileName);
        checkFileExisting(fileList, toolFileName);
        checkFileExisting(fileList, candidateFileName);
        checkFileExisting(fileList, previewFileName);
        Collection<String> values = keyToFileName.values();
        if (null != values) {
            Iterator<String> it = values.iterator();
            while (it.hasNext()) {
                String s = it.next();
                checkFileExisting(fileList, s);
            }
        }
    }

    private void checkFileExisting(ArrayList<String> fileList, String fileName) {
        if (!TextUtils.isEmpty(fileName) && !fileList.contains(fileName)) {
            Log.e(TAG, "checkFileExisting fail, could not found config file: " + fileName);
        }
    }

    public void addTrack(String fileName, int trackId) {
        nameToTrackId.put(fileName, trackId);
        Log.d(TAG, "addTrack " + fileName + " with id " + trackId + " -> map size " + nameToTrackId.size());
    }

    public void getReady() {
        Collection<String> values = keyToFileName.values();
        if (null != values) {
            Iterator<String> it = values.iterator();
            while (it.hasNext()) {
                String s = it.next();
                tryAddUniqueFileName(s);
            }
        }

        tryAddUniqueFileName(defaultFileName);
        tryAddUniqueFileName(keyFileName);
        tryAddUniqueFileName(funcFileName);
        tryAddUniqueFileName(toolFileName);
        tryAddUniqueFileName(candidateFileName);
        tryAddUniqueFileName(previewFileName);
    }

    public List<Integer> getAllTrackIds() {
        if (null == nameToTrackId || nameToTrackId.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> trackIds = new ArrayList<>();
        for (String key : nameToTrackId.keySet()) {
            trackIds.add(nameToTrackId.get(key));
        }

        return trackIds;
    }

    public int getTrackIdByCode(int keyCode) {
        if (KeyEvent.KEYCODE_0 < keyCode && keyCode < KeyEvent.KEYCODE_9 ||
                KeyEvent.KEYCODE_A < keyCode && keyCode < KeyEvent.KEYCODE_Z ) {
            return nameToTrackId.get(keyFileName);
        } else if (KeyEvent.KEYCODE_DEL == keyCode || KeyEvent.KEYCODE_ENTER == keyCode || KeyEvent.KEYCODE_SPACE == keyCode) {
            return nameToTrackId.get(funcFileName);
        } else if (KeyEvent.KEYCODE_SHIFT_LEFT == keyCode || KeyEvent.KEYCODE_SHIFT_RIGHT == keyCode) {
            return nameToTrackId.get(toolFileName);
        } else if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_DEL == keyCode) {
            return nameToTrackId.get(candidateFileName);
        } else {
            return nameToTrackId.get(defaultFileName);
        }
    }
}
