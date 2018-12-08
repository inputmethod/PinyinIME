package com.android.inputmethod.opengl;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.android.inputmethod.pinyin.R;

public class Sample3_1Activity extends Activity
{
	MyTDView mview;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        mview =new MyTDView(this);
//        mview.requestFocus();
//        mview.setFocusableInTouchMode(true);
//        setContentView(mview);
        setContentView(R.layout.sample_activity);
        mview = findViewById(R.id.m_view);
        mview.requestFocus();
        mview.setFocusableInTouchMode(true);
    }
    @Override
    public void onResume()
    {
    	super.onResume();
        mview.onResume();
    }
    @Override
    public void onPause()
    {
    	super.onPause();
        mview.onPause();
    }
}