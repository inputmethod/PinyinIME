package com.android.inputmethod.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

/**
 * Created by yangfeng on 2017/9/14.
 */

public class ImmUtils {
    public static void showIme(Context appContext) {
        InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showInputmethodPicker(Context appContext) {
        InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();
    }

    public static void showInputmethodSetting(Context appContext) {
        try {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
            appContext.startActivity(intent);
        } catch (Exception e) {

        }
    }

    public static boolean isImeEnabled(Context appContext, Class cls) {
        InputMethodManager imm = (InputMethodManager) appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        ComponentName myInputMethod = new ComponentName(appContext, cls);
        List<InputMethodInfo> enabledInputMethodInfos = imm.getEnabledInputMethodList();
        for (InputMethodInfo im : enabledInputMethodInfos) {
            if (im.getComponent().toShortString().equals(myInputMethod.toShortString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImeDefault(Context appContext, Class cls) {
        ComponentName defaultInputMethod = null;
        try {
            defaultInputMethod = ComponentName.unflattenFromString(Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));
        } catch (Exception e) {
        }
        ComponentName myInputMethod = new ComponentName(appContext, cls);

        if (defaultInputMethod == null) return false;

        return defaultInputMethod.toShortString().equals(myInputMethod.toShortString());
    }
}
