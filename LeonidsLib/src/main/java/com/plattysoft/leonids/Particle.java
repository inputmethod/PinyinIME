package com.plattysoft.leonids;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.plattysoft.leonids.modifiers.ParticleModifier;

import java.util.List;
import java.util.Random;

public class Particle {
    private final Camera mCamera = new Camera();

	protected Bitmap mImage;
	
	public float mCurrentX;
	public float mCurrentY;

	public float initialScale = 1f;
	public float mScale = 1f;
	public int mAlpha = 255;
	
	public float mInitialRotation = 0f;
	
	public float mRotationSpeed = 0f;
	
	public float mSpeedX = 0f;
	public float mSpeedY = 0f;

	public float mAccelerationX;
	public float mAccelerationY;

	public float mAccumulateX = 0f;
	public float mRandom = 0f;
	public long timeLast = 0;
	public int nCount = 0;
	public boolean bFirstTime = true;

	public float xOldPercent = 0f;
	public float yOldPercent = 0f;
	public float xNewPercent = 0f;
	public float yNewPercent = 0f;

	private Matrix mMatrix;
	private Paint mPaint;

	private float mInitialX;
	private float mInitialY;

	private float mRotation;

	private long mTimeToLive;

	protected long mStartingMilisecond;

	private int mBitmapHalfWidth;
	private int mBitmapHalfHeight;

	private List<ParticleModifier> mModifiers;


	protected Particle() {		
		mMatrix = new Matrix();
		mPaint = new Paint();
	}
	
	public Particle (Bitmap bitmap) {
		this();
		mImage = bitmap;
	}

	public void init() {
		initialScale = 1;
		mScale = 1;
		mAlpha = 255;
		Random random = new Random();
		mRandom = 1 - random.nextFloat() / 2;
	}
	
	public void configure(long timeToLive, float emiterX, float emiterY) {
		mBitmapHalfWidth = mImage.getWidth()/2;
		mBitmapHalfHeight = mImage.getHeight()/2;
		
		mInitialX = emiterX - mBitmapHalfWidth;
		mInitialY = emiterY - mBitmapHalfHeight;
		mCurrentX = mInitialX;
		mCurrentY = mInitialY;
		
		mTimeToLive = timeToLive;
	}

	public boolean update (long miliseconds) {
		long realMiliseconds = miliseconds - mStartingMilisecond;
		if (realMiliseconds > mTimeToLive) {
			return false;
		}
		// 采用加速度的方式响应传感器
//		for (int i=0; i<mModifiers.size(); i++) {
//			mModifiers.get(i).apply(this, realMiliseconds);
//		}
        for (int i=0; i<mModifiers.size(); i++) {
            mModifiers.get(i).preApply(this, miliseconds);
        }

		mCurrentX = mInitialX+mSpeedX*realMiliseconds+mAccelerationX*realMiliseconds*realMiliseconds;
		mCurrentY = mInitialY+mSpeedY*realMiliseconds+mAccelerationY*realMiliseconds*realMiliseconds;
		mRotation = mInitialRotation + mRotationSpeed*realMiliseconds/1000;
		// 采用位移的方式响应传感器
		for (int i=0; i<mModifiers.size(); i++) {
			mModifiers.get(i).apply(this, realMiliseconds);
		}
		updateMatrix();
		return true;
	}

    private void preUpdateMatrix() {
        mCamera.save();
        mCamera.rotateY(mRotation);
        //mCamera.rotateX(-mRotation);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
    }

    private void afterUpdateMatrix() {
        mCamera.save();
    }

    public void draw (Canvas c) {
		mPaint.setAlpha(mAlpha);		
		c.drawBitmap(mImage, mMatrix, mPaint);
	}

	private float getDisplayScale() {
		return initialScale * mScale;
	}
	private void updateMatrix() {
		mMatrix.reset();
		//preUpdateMatrix();
		mMatrix.postRotate(mRotation, mBitmapHalfWidth, mBitmapHalfHeight);
		mMatrix.postScale(getDisplayScale(), getDisplayScale(), mBitmapHalfWidth, mBitmapHalfHeight);
		mMatrix.postTranslate(mCurrentX, mCurrentY);
		afterUpdateMatrix();
	}

	public Particle activate(long startingMilisecond, List<ParticleModifier> modifiers) {
		mStartingMilisecond = startingMilisecond;
		// We do store a reference to the list, there is no need to copy, since the modifiers do not carte about states 
		mModifiers = modifiers;
		return this;
	}
}
