package com.typany.keyboard.sound;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by yangfeng on 2017/9/14.
 */

public class SoundPackageConf {
    private static final String TAG = SoundPackageConf.class.getSimpleName();

    // file name
    public String defaultFileName;
    public String keyFileName;
    public String funcFileName;
    public String toolFileName;
    public String candidateFileName;
    public String previewFileName;

    // sound track id list ready for play
    public final List<Integer> trackIds = new ArrayList<>();

    // map file name to its track id
    public final Map<String, Integer> nameToTracks = new HashMap<>();

    // map key code to sound file name so any key could be configured to a specific sound, if
    // a key is not configured, then it will be map to either the category sound file name or
    // default file name. if none of these file name existing, no sound play for that key.
    public final Map<Integer, String> keyToFileName = new HashMap<>();

    public int getPreviewTrack() {
        int trackId = -1;
        if (nameToTracks.containsKey(previewFileName)) {
            trackId = nameToTracks.get(nameToTracks);
        }
        Log.d(TAG, "getPreviewTrack " + trackId + " for " + previewFileName);
        return trackId;
    }

    public List<String> getAllFileNameList() {
        ArrayList<String> fileNameList = new ArrayList<>();
        int i = 0;
        Collection<String> values = keyToFileName.values();
        if (null != values) {
            Iterator<String> it = values.iterator();
            while (it.hasNext()) {
                String s = it.next();
                i = tryAddNameToTrack(s, i);
            }
        }
        i = tryAddNameToTrack(defaultFileName, i);
        i = tryAddNameToTrack(keyFileName, i);
        i = tryAddNameToTrack(funcFileName, i);
        i = tryAddNameToTrack(toolFileName, i);
        i = tryAddNameToTrack(candidateFileName, i);
        tryAddNameToTrack(previewFileName, i);

        Log.d(TAG, "unique file list size " + fileNameList.size() + ", map size " + nameToTracks.size()
                + ", key map size " + keyToFileName.size());
        return fileNameList;
    }

    private int tryAddNameToTrack(String s, int i) {
        if (!TextUtils.isEmpty(s) && !nameToTracks.containsKey(s)) {
            nameToTracks.put(s, i);
            i++;
        }
        return i;
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
        trackIds.add(trackId);
        nameToTracks.put(fileName, trackId);
    }
}
