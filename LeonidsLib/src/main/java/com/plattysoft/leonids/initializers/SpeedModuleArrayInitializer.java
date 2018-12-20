package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class SpeedModuleArrayInitializer implements ParticleInitializer {

	private float mMinValue;
	private float mMaxValue;

	private float mSpeedMin;
	private float mSpeedMax;
	private int[] mMinAngle;

	public SpeedModuleArrayInitializer(float minAcceleration, float maxAcceleration, float speedMin, float speedMax, int[] minAngle) {
		mMinValue = minAcceleration;
		mMaxValue = maxAcceleration;
		mSpeedMin = speedMin;
		mSpeedMax = speedMax;
		mMinAngle = minAngle;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		float speed = r.nextFloat()*(mSpeedMax-mSpeedMin) + mSpeedMin;
		int angle;
		if (null == mMinAngle || mMinAngle.length < 1) {
			angle = 0;
		}
		else {
			angle = mMinAngle[r.nextInt(mMinAngle.length)];
		}

		double angleInRads = Math.toRadians(angle);
		p.mSpeedX = (float) (speed * Math.cos(angleInRads));
		p.mSpeedY = (float) (speed * Math.sin(angleInRads));
		p.mInitialRotation = (1 + r.nextFloat()) * angle;

		float value = r.nextFloat()*(mMaxValue-mMinValue)+mMinValue;
		p.mAccelerationX = (float) (value * Math.cos(angleInRads));
		p.mAccelerationY = (float) (value * Math.sin(angleInRads));
	}

}
