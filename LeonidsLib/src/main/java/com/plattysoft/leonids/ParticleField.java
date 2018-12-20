package com.plattysoft.leonids;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

class ParticleField extends View {
	private ParticleModel model;

	public ParticleField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public ParticleField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ParticleField(Context context) {
		super(context);
	}

	void setModel(ParticleModel model) {
		this.model = model;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null != model) {
			synchronized (model) {
				model.draw(canvas);
			}
		}
	}
}
