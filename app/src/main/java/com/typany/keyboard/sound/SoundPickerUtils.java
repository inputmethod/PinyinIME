package com.typany.keyboard.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangfeng on 2017/9/13.
 */

public class SoundPickerUtils {
    private static final String soundFolder = "sound/suite";
    private static final String configFileName = "sound.ini";
    private static final String configJsonName = "sound.json";

    private static final String TAG = SoundPickerUtils.class.getSimpleName();

    public static List<String> getShortSoundList(Context appContext, int count) {
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

    // todo: load sound asset under soundFolder with sub folder assertFolderName
    public static SoundPackageConf loadSound(Context context, String assertFolderName) {
        String subFolder = soundFolder + File.separator + assertFolderName;
        SoundPackageConf conf = null;
        try {
            AssetManager am = context.getAssets();
            String[] subFileNames = am.list(subFolder);
            if (null != subFileNames) {
                ArrayList<String> fileList = new ArrayList<>();
                for (String name : subFileNames) {
                    if (configJsonName.equalsIgnoreCase(name)) {
                        conf = parseJsonConfig(am, assertFolderName, name);
                    } else if (configFileName.equalsIgnoreCase(name)) {
                        if (null == conf) {
                            conf = parseIniConfig(am, assertFolderName, name);
                        }
                    } else {
                        fileList.add(name);
                    }
                }
                ensureConfigAndFile(conf, fileList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            conf = null;
        }
        return conf;
    }

    // todo: check conf item and all file name it reference to existing in fileList.
    private static void ensureConfigAndFile(SoundPackageConf conf, ArrayList<String> fileList) {
        if (null != conf) {
            conf.getReady();
            conf.checkFileExisting(fileList);
        }
    }

    private static SoundPackageConf parseIniConfig(AssetManager am, String folderName, String iniFileName) {
        return null;
    }

    private static SoundPackageConf parseJsonConfig(AssetManager am, String folderName, String jsonFileName) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            InputStream inputStream = am.open(soundFolder + File.separator + folderName + File.separator + jsonFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(laststr);
            SoundPackageConf conf = new SoundPackageConf();
            conf.folder = folderName;
            conf.name = jsonObject.optString("KEY_TONE_SUITE");
            conf.defaultFileName = jsonObject.optString("KEY_TONE_DEFAULT");
            conf.keyFileName = jsonObject.optString("KEY_TONE_NORMAL");
            conf.funcFileName = jsonObject.optString("KEY_TONE_FUNCTION");
            conf.toolFileName = jsonObject.optString("KEY_TONE_TOOL");
            conf.candidateFileName = jsonObject.optString("KEY_TONE_CANDIDATE");
            conf.previewFileName = jsonObject.optString("Preview");

            // todo: load extra key sound
            //
            return conf;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // this method open an file description in asserts, and the caller should ensure to close it after
    // used or it will be leak.
    public static AssetFileDescriptor openFd(Context appContext, String folderName, String fileName) {
        String soundFile = folderName + File.separator + fileName;
        AssetFileDescriptor descriptor = null;
        try {
            AssetManager am = appContext.getAssets();
            descriptor = am.openFd(soundFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return descriptor;
    }
}
