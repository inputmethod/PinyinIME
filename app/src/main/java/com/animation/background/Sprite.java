package com.animation.background;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * 这个类用来当测试的物件，会沿着方形路线持续移动
 */
public class Sprite {
    private float x;
    private float y;
    private Bitmap img;
    private Paint paint;

    public Sprite(Bitmap img) {
        this.img = img;
        this.x = 100;
        this.y = 100;
        this.paint = new Paint();
    }

    // 在SurfaceView加锁同步后传给自己的Canvas上绘制自己
    public void drawSelf(Canvas canvas) {
        canvas.drawBitmap(img, x, y, paint);
    }

    // 获取物件下一次要绘制的位置(这里是沿着一个边长为400的正方形不断运动的)
    int[] velocity = {STEP, STEP};
    public void getNextPos() {
        x += velocity[0];
        y += velocity[1];
        if (x >= width || x <= 0) {
            velocity[0] = -velocity[0];
        }
        if (y >= height || y <= 0) {
            velocity[1] = -velocity[1];
        }
    }

    private int width = 500;
    private int height = 100;
    private static final int STEP = 5;
    public void setBound(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
