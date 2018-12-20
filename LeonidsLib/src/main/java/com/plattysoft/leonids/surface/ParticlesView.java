package com.plattysoft.leonids.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/*
 * 这个类就是加工了SurfaceView之后的基类，所有要运动的物件都最终放在它的派生类进行绘制
 * 这里只维护管理绘制线程和更新频率。
 */
public abstract class ParticlesView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread thread; // SurfaceView通常需要自己单独的线程来播放动画
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public ParticlesView(Context c) {
        this(c, null);
    }

    public ParticlesView(Context c, AttributeSet attrs) {
        super(c, attrs);

        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (null != thread && changedView == this && visibility == GONE) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        while (true) {
            canvas = this.surfaceHolder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
            if (null == canvas) {
                return;
            }

            updateParticles(canvas, TIMER_TASK_INTERVAL);

            this.surfaceHolder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
            try {
                Thread.sleep(TIMER_TASK_INTERVAL); // 这个就相当于帧频了，数值越小画面就越流畅
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static long TIMER_TASK_INTERVAL = 1000;
    protected final void startRenderingThread(long timerTaskInterval) {
        TIMER_TASK_INTERVAL = timerTaskInterval;
        if (null != thread) {
            return;
        }

        this.thread = new Thread(this);
        this.thread.start();
    }

    abstract protected void updateParticles(Canvas canvas, long interval);
}
