package com.plattysoft.leonids.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleModel;
import com.plattysoft.leonids.ParticleSurface;

import java.lang.ref.WeakReference;

/*
 * 这个类就是加工了SurfaceView之后的实现类，所有要运动的物件都最终放在这里进行绘制
 * 这个类桥接SurfaceView和ParticleSurface类
 */
public class ParticlesBridgeView extends ParticlesView {
    public ParticlesBridgeView(Context c) {
        this(c, null);
    }

    public ParticlesBridgeView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility == GONE) {
            Toast.makeText(getContext(), "SurfaceView已经隐藏", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void updateParticles(Canvas canvas, long interval) {
        if (null == canvas || null == model) return;
        synchronized (model) {
            if (mPs.get() != null) {
                ParticleSurface ps = mPs.get();
                ps.onUpdate(ps.mCurrentTime);
                ps.mCurrentTime += interval;
            }

            model.drawParticles(canvas);
        }
    }


    private ParticleModel model;
    private WeakReference<ParticleSurface> mPs;
    public void schedule(ParticleSurface ps, ParticleModel model, long timerTaskInterval) {
        mPs = new WeakReference<>(ps);
        this.model = model;
        setRenderingInterval(timerTaskInterval);
    }
}
