package com.typany.resource;

import android.content.Context;

public final class ResourceManager {
    private static ResourceManager sInstance = null;

    public static ResourceManager getInstance() {
        if (sInstance == null) {
            sInstance = getSync();
        }
        return sInstance;
    }

    private static synchronized ResourceManager getSync() {
        if (sInstance == null) {
            sInstance = new ResourceManager();
        }
        return sInstance;
    }

    public final SoundHolder sound = new SoundHolder();

    private ResourceManager() {
    }

    public void onCreate(Context appContext) {
        if (appContext != null) {
            sound.onCreate(appContext);
//            emojiMaker.onCreate(appContext);
        } else {
            throw new AssertionError("Can not get the context of service.");
        }
    }

    public void onDestroy() {
        sound.onDestroy();
    }
}
