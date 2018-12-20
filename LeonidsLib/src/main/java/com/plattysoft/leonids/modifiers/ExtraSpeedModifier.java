package com.plattysoft.leonids.modifiers;

import android.os.Handler;
import android.util.Log;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class ExtraSpeedModifier implements ParticleModifier {
	private final static String TAG = ExtraSpeedModifier.class.getSimpleName();

	private float randomStart;
	private float randomRange;
	private float xCurPercent = 0f;
	private float yCurPercent = 0f;
	private final Random random = new Random();

	private int nTotal = 10;
	private int timePerFrame = 34;


	public ExtraSpeedModifier(float start, float range, float xPercent, float yPercent) {
		this.randomStart = start;
		this.randomRange = range;
		update(xPercent, yPercent);
	}

    public void update(final float x, final  float y) {
		xCurPercent = x;
		yCurPercent = y;
    }

	@Override
	public void apply(Particle particle, long miliseconds) {
		Log.d(TAG, "apply origin xy: " + particle.mCurrentX + ", " + particle.mCurrentY);

		// 曲线运动
//		particle.mAccumulateX -= 2;
//		particle.mCurrentX += particle.mAccumulateX;

		// 雪花采用位移的方式左右移动
//		particle.mCurrentX += getRandomSpeedBase() * xPercent;
//		particle.mCurrentY += getRandomSpeedBase() * yPercent;
//		Log.d(TAG, "apply after xy: " + particle.mCurrentX + ", " + particle.mCurrentY);
	}


	@Override
	public void preApply(Particle particle, long miliseconds) {
		// 雪花采用加速度的方式左右移动
		if (miliseconds - particle.timeLast < timePerFrame ) {
			return;
		}

		if (particle.nCount >= nTotal) {
			if (particle.bFirstTime) {
				particle.bFirstTime = false;
			} else {
				particle.xOldPercent = particle.xNewPercent;
				particle.yOldPercent = particle.yNewPercent;
				particle.xNewPercent = xCurPercent;
				particle.yNewPercent = yCurPercent;
			}
			particle.nCount = 0;
		} else {
			if (particle.bFirstTime) {
				if (particle.nCount == 0) {
					particle.xOldPercent = xCurPercent;
					particle.yOldPercent = yCurPercent;
				}

				if (particle.nCount == nTotal - 1) {
					particle.xNewPercent = xCurPercent;
					particle.yNewPercent = yCurPercent;
				}

				particle.timeLast = miliseconds;
				particle.nCount++;

				return;
			}


		}

		float xPercent = 0f;
		int nIndex = particle.nCount % nTotal;
		xPercent = particle.xOldPercent + (particle.xNewPercent - particle.xOldPercent) * nIndex / nTotal;
		Log.d("tests", "xOldPercent = " + particle.xOldPercent + ", xNewPercent = " + particle.xNewPercent);
		particle.timeLast = miliseconds;
		particle.nCount++;

		particle.mSpeedX = xPercent * 0.1f;
		particle.mSpeedX *= particle.mRandom;
//
//		if (xPercent < 0.15 && xPercent > -0.15) {
//			// 小范围不动
//			particle.mSpeedX /= 2;
//			return;
//		}

		Log.d("tests", "xPercent = " + xPercent + ", SpeedX = " + particle.mSpeedX);
	}

	private float getRandomSpeedBase() {
		return randomStart + random.nextFloat() * (randomRange - randomStart);
	}

}
