package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class AlphaInitializer implements ParticleInitializer {

    private int mMaxAlpha;
    private int mMinAlpha;

    public AlphaInitializer(int minAlpha, int maxAlpha) {
        mMinAlpha = minAlpha;
        mMaxAlpha = maxAlpha;
    }

    @Override
    public void initParticle(Particle p, Random r) {
        int alpha = (int)(r.nextFloat()*(mMaxAlpha-mMinAlpha)) + mMinAlpha;
        if (alpha > 255) {
            alpha = 255;
        } else if (alpha < 0) {
            alpha = 0;
        }

        p.mAlpha = alpha;
    }

}
