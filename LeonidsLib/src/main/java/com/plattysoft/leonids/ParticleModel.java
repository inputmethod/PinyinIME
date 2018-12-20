package com.plattysoft.leonids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.plattysoft.leonids.initializers.ParticleInitializer;
import com.plattysoft.leonids.modifiers.ParticleModifier;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleModel {
    private final List<Particle> mParticles = new ArrayList<>();
    private final List<Particle> mActiveParticles = new ArrayList<>();

    private float mParticlesPerMillisecond;
    private long mEmittingTime;


    private final List<ParticleModifier> mModifiers = new ArrayList<>();
    private final List<ParticleInitializer> mInitializers = new ArrayList<>();

    private Random mRandom = new Random();
    private final int mMaxParticles;
    public ParticleModel(int maxParticles) {
        mMaxParticles = maxParticles;
    }

    void draw(Canvas canvas) {
        // Draw all the particles
        synchronized (mActiveParticles) {
            for (int i = 0; i < mActiveParticles.size(); i++) {
                mActiveParticles.get(i).draw(canvas);
            }
        }
    }

    void cleanupAnimation() {
        mParticles.addAll(mActiveParticles);
    }


    public void startEmitting(int particlesPerSecond, long emittingTime) {
        mParticlesPerMillisecond = particlesPerSecond / 1000f;
        setEmittingTime(emittingTime);
//        mEmittingTime = emittingTime;
//        mParticlesPerMillisecond = particlesPerSecond / 1000f;
//        mEmittingTime = -1; // Meaning infinite
    }

    public void setEmittingTime(long emittingTime) {
        mEmittingTime = emittingTime;
    }

    public void onUpdate(long mTimeToLive, long miliseconds) {
        while (((mEmittingTime > 0 && miliseconds < mEmittingTime) || mEmittingTime == -1) && // This point should emit
                !mParticles.isEmpty() && // We have particles in the pool
                mActiveParticles.size() < mParticlesPerMillisecond * miliseconds) { // and we are under the number of particles that should be launched
            // Activate a new particle
            activateParticle(mInitializers, mModifiers, mTimeToLive, miliseconds);
        }
        synchronized (mActiveParticles) {
            for (int i = 0; i < mActiveParticles.size(); i++) {
                boolean active = mActiveParticles.get(i).update(miliseconds);
                if (!active) {
                    Particle p = mActiveParticles.remove(i);
                    i--; // Needed to keep the index at the right position
                    mParticles.add(p);
                }
            }
        }
    }

    public void initParticle(Drawable drawable) {
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            for (int i = 0; i < mMaxParticles; i++) {
                mParticles.add(new AnimatedParticle(animation));
            }
        } else {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
            initParticle(bitmap);
//            for (int i = 0; i < mMaxParticles; i++) {
//                mParticles.add(new Particle(bitmap));
//            }
        }
    }
    void initParticle(Bitmap bitmap) {
        for (int i = 0; i < mMaxParticles; i++) {
            mParticles.add(new Particle(bitmap));
        }
    }

    public void onShot(long mTimeToLive, int numParticles) {
        // We create particles based in the parameters
        for (int i = 0; i < numParticles && i < mMaxParticles; i++) {
            activateParticle(mInitializers, mModifiers, mTimeToLive, 0);
        }
    }

    private void activateParticle(List<ParticleInitializer> mInitializers,
                                  List<ParticleModifier> mModifiers, long mTimeToLive, long delay) {
        Particle p = mParticles.remove(0);
        p.init();
        // Initialization goes before configuration, scale is required before can be configured properly
        for (int i = 0; i < mInitializers.size(); i++) {
            mInitializers.get(i).initParticle(p, mRandom);
        }

        int particleX = getFromRange(mEmitterXMin, mEmitterXMax);
        int particleY = getFromRange(mEmitterYMin, mEmitterYMax);
        p.configure(mTimeToLive, particleX, particleY);
        p.activate(delay, mModifiers);
        mActiveParticles.add(p);
    }

    private int getFromRange(int minValue, int maxValue) {
        if (minValue == maxValue) {
            return minValue;
        }
        if (minValue < maxValue) {
            return mRandom.nextInt(maxValue - minValue) + minValue;
        } else {
            return mRandom.nextInt(minValue - maxValue) + maxValue;
        }
    }

    private int mEmitterXMin;
    private int mEmitterXMax;
    private int mEmitterYMin;
    private int mEmitterYMax;

    void configureEmitter(int[] mParentLocation, int emitterX, int emitterY) {
        // We configure the emitter based on the window location to fix the offset of action bar if present
        mEmitterXMin = emitterX - mParentLocation[0];
        mEmitterXMax = mEmitterXMin;
        mEmitterYMin = emitterY - mParentLocation[1];
        mEmitterYMax = mEmitterYMin;
    }

    void configureEmitter(int[] mParentLocation, int[] location, int width, int height, int gravity) {
        // Check horizontal gravity and set range
        if (hasGravity(gravity, Gravity.LEFT)) {
            mEmitterXMin = location[0] - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else if (hasGravity(gravity, Gravity.RIGHT)) {
            mEmitterXMin = location[0] + width - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else if (hasGravity(gravity, Gravity.CENTER_HORIZONTAL)) {
            mEmitterXMin = location[0] + width / 2 - mParentLocation[0];
            mEmitterXMax = mEmitterXMin;
        } else {
            // All the range
            mEmitterXMin = location[0] - mParentLocation[0];
            mEmitterXMax = location[0] + width - mParentLocation[0];
        }

        // Now, vertical gravity and range
        if (hasGravity(gravity, Gravity.TOP)) {
            mEmitterYMin = location[1] - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else if (hasGravity(gravity, Gravity.BOTTOM)) {
            mEmitterYMin = location[1] + height - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else if (hasGravity(gravity, Gravity.CENTER_VERTICAL)) {
            mEmitterYMin = location[1] + height / 2 - mParentLocation[1];
            mEmitterYMax = mEmitterYMin;
        } else {
            // All the range
            mEmitterYMin = location[1] - mParentLocation[1];
            mEmitterYMax = location[1] + height - mParentLocation[1];
        }
    }

    private boolean hasGravity(int gravity, int gravityToCheck) {
        return (gravity & gravityToCheck) == gravityToCheck;
    }

    void add(ParticleModifier modifier) {
        mModifiers.add(modifier);
    }

    void add(ParticleInitializer initializer) {
        mInitializers.add(initializer);
    }

    public void drawParticles(Canvas canvas) {
        for (Particle particle : mActiveParticles) {
            particle.draw(canvas);
        }
    }
}
