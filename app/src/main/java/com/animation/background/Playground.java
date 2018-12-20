package com.animation.background;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.android.inputmethod.pinyin.R;
import com.plattysoft.leonids.surface.ParticlesBridgeView;

/*
 * 这个类就是加工了SurfaceView之后的类，所有要运动的物件都最终放在这里进行绘制
 */
public class Playground extends ParticlesBridgeView {
    private Sprite flyingIcon;

    public Playground(Context c) {
        this(c, null);
    }

    public Playground(Context c, AttributeSet attrs) {
        super(c, attrs);

        Bitmap img = BitmapFactory.decodeResource(c.getResources(), R.drawable.app_icon);
        this.flyingIcon = new Sprite(img);
    }

    @SuppressLint("WrongCall")
    @Override
    protected void updateParticles(Canvas canvas, long interval) {
        canvas.drawColor(Color.BLACK);
        super.updateParticles(canvas, interval);
        flyingIcon.getNextPos();
        flyingIcon.drawSelf(canvas); // 把SurfaceView的画布传给物件，物件会用这个画布将自己绘制到上面的某个位置
        onDraw(canvas);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        // 这里是SurfaceView发生变化的时候触发的部分
        flyingIcon.setBound(width, height);
    }
}
