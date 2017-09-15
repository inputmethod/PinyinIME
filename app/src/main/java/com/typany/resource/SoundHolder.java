package com.typany.resource;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.Log;

import com.typany.keyboard.sound.SoundPackageConf;
import com.typany.keyboard.sound.SoundPickerUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SoundHolder implements IResourceHolder {
    private static final String SOUND_TRACK_FILENAME = "tap_key.ogg";
    private static final String TAG = SoundHolder.class.getSimpleName();

    private SoundPool mSoundPool;
    private int mSoundTrackId = -1;
    private int mPlayTrackId = -1;
    private Thread releaseThread;

    SoundHolder() {
    }

    @Override
    public void onCreate(Context appContext) {
        // modified by sunhang : change stream type from FX_KEYPRESS_STANDARD to STREAM_MUSIC.
        // this can fix a issue(http://10.134.74.226:880/browse/GIME-1429)
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        AssetFileDescriptor descriptor = SoundPickerUtils.openFd(appContext, "sound", SOUND_TRACK_FILENAME);
        mSoundTrackId = tryLoadAsset(descriptor);

        releaseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSoundPool != null) {
                    try {
                        if (mSoundTrackId != -1) {
                            mSoundPool.stop(mSoundTrackId);
                        }
                        mSoundPool.release();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        mSoundPool = null;
                    }
                }
            }
        });
    }

    public void stopCurrentSound() {
        if (mSoundPool != null && mPlayTrackId != -1) {
            try {
                //these two methods may lead to unexpcted error.
                mSoundPool.stop(mPlayTrackId);

                if (null != conf) {
                    List<Integer> trackIds = conf.getAllTrackIds();
                    Log.d(TAG, "stopCurrentSound, try to stop track id size: " + trackIds.size());
                    for (int id : trackIds) {
                        mSoundPool.stop(id);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void reload(Context appContext, String folder, Map<String, String> suite) {
        stopCurrentSound();

        // todo: load and model key tone suite
    }

    private SoundPackageConf conf = null;
    private String confName = null;
    public void reloadAssert(Context context, String assertFolderName) {
        stopCurrentSound();
        confName = assertFolderName;
        conf = SoundPickerUtils.loadSound(context, assertFolderName);
        if (null == conf) {
            Log.e(TAG, "reloadAssert failed for " + assertFolderName);
        } else {
            String folderName = "sound/suite/" + assertFolderName;
            List<String> fileNameList = conf.getAllFileNameList();
            for (String fileName : fileNameList) {
                AssetFileDescriptor fd = SoundPickerUtils.openFd(context, folderName, fileName);
                conf.addTrack(fileName, tryLoadAsset(fd));
            }
            Log.d(TAG, "reloadAssert " + confName + ", add track " + conf.trackFileNames.size() +
                    ", name2track size " + conf.nameToTrackId.size());
        }
    }

    public void playCurrentPreview(float vol) {
        if (null != conf) {
            int previewId = conf.getPreviewTrack();
            playTone(previewId, vol);
        }
    }

    public void reload(Context appContext, String filePath) {
        stopCurrentSound();

        try {
            File newKeyToneFile = new File(filePath);
            if (TextUtils.isEmpty(filePath) || !new File(filePath).exists() || newKeyToneFile.length() > 102400) {
                AssetFileDescriptor descriptor = SoundPickerUtils.openFd(appContext, "sound", SOUND_TRACK_FILENAME);
                mSoundTrackId = tryLoadAsset(descriptor);
            } else if (mSoundPool != null) {
                mSoundTrackId = mSoundPool.load(filePath, 1);
//                SettingMgr.getInstance(appContext).setValue(SettingField.TYPING_SOUND_ENABLE, "true");
                // todo: save enable flag
            } else {
                Log.e(TAG, "reload while sound pool is null.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mSoundTrackId = -1;
        }
    }

    private int tryLoadAsset(AssetFileDescriptor descriptor) {
        try {
            return mSoundPool.load(descriptor, 1);
        } catch (Exception e) {
            return  -1;
        } finally {
            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if(mSoundPool != null && releaseThread!=null){
            releaseThread.start();
        }
    }

    public void playKeyTone(float vol) {
        playTone(mSoundTrackId, vol);
    }

    public void playKeyTone(int keyCode, float vol) {
        int trackId = null == conf ? mSoundTrackId : conf.getTrackIdByCode(keyCode);
        playTone(trackId, vol);
    }

    public void playTone(int trackId, float vol) {
        Log.d(TAG, "playTone trackId " + trackId + ", volume " + vol);
        if (mSoundPool != null) {
            if (trackId != -1) {
                mPlayTrackId = mSoundPool.play(trackId, vol, vol, 1, 0, 1);
            }
        } else {
            throw new RuntimeException("Use IME resource without IME service instance existing.");
        }
    }
}