package com.typany.resource;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SoundHolder implements IResourceHolder {
    private static final String SOUND_TRACK_FILENAME = "tap_key.ogg";

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
        String soundFile = "sound/" + SOUND_TRACK_FILENAME;
        AssetFileDescriptor descriptor = null;
        AssetManager am = appContext.getAssets();
        try {
            descriptor = am.openFd(soundFile);
            mSoundTrackId = mSoundPool.load(descriptor, 1);
//            SLog.i(CommonUtils.DEFAULT_TAG, "sound pool >> " + "onCreate" + mSoundTrackId);
        } catch (IOException e) {
            e.printStackTrace();
            mSoundTrackId = -1;
        } finally {
            if (descriptor != null) {
                try {
                    descriptor.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void reload(Context appContext, String folder, Map<String, String> suite) {
        stopCurrentSound();

        // todo: load and model key tone suite
    }

    public void reload(Context appContext, String filePath) {
        stopCurrentSound();

        try {
            File newKeyToneFile = new File(filePath);
            if (TextUtils.isEmpty(filePath) || !new File(filePath).exists() || newKeyToneFile.length() > 102400) {
                String soundFile = "sound/" + SOUND_TRACK_FILENAME;
                AssetFileDescriptor descriptor = null;
                try {
                    AssetManager am = appContext.getAssets();
                    descriptor = am.openFd(soundFile);
                    mSoundTrackId = mSoundPool.load(descriptor, 1);
                } finally {
                    if (descriptor != null) {
                        try {
                            descriptor.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else if (mSoundPool != null) {
                mSoundTrackId = mSoundPool.load(filePath, 1);
//                SettingMgr.getInstance(appContext).setValue(SettingField.TYPING_SOUND_ENABLE, "true");
                // todo: save enable flag
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mSoundTrackId = -1;
        }
    }

    @Override
    public void onDestroy() {
        if(mSoundPool != null && releaseThread!=null){
            releaseThread.start();
        }
    }

    public void playKeyTone(float vol) {
        if (mSoundPool != null) {
            if (mSoundTrackId != -1) {
                mPlayTrackId = mSoundPool.play(mSoundTrackId, vol, vol, 1, 0, 1);
            }
        } else {
            throw new RuntimeException("Use IME resource without IME service instance existing.");
        }
    }
}