package com.seungchanahn.pizzaclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by anirban on 5/21/17.
 */

public class TimerView extends View {

	private static final String TAG = TimerView.class.getName();

	private static int RADIUS = 400;
	private static final int BACKGROUND_COLOR = 0xffffffff;
	private static final int FOREGROUND_COLOR = 0xa0ff0000;

	private static float STARTING_ANGLE = -90;
	private static final float FULL_CIRCLE = 360f;

	private ShapeDrawable circle;
	private ShapeDrawable arc;

	private float lastFraction;

	public TimerView(Context context) {
		super(context);
		this.init();
	}

	public TimerView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.init();
	}

	private void init() {
		this.circle = new ShapeDrawable(new OvalShape());
		this.circle.getPaint().setColor(BACKGROUND_COLOR);
		this.circle.setBounds(0, 0, RADIUS, RADIUS);
		this.lastFraction = 0f;
		this.arc = new ShapeDrawable(new ArcShape(STARTING_ANGLE, this.lastFraction));
		this.arc.getPaint().setColor(FOREGROUND_COLOR);
		this.arc.setBounds(0, 0, RADIUS, RADIUS);
	}

	protected void onDraw(Canvas canvas) {
		this.circle.draw(canvas);
		this.arc.draw(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.setMeasuredDimension(RADIUS, RADIUS);
	}

	public void settingRadius(int myRadious){
		RADIUS = myRadious;
		this.init();
	}

	public void settingStartingAngle(int startingAngle){
		STARTING_ANGLE = startingAngle;
		this.init();
	}

	public void setFraction(float fraction) {
		if (fraction < 0f || fraction > 1.f) {
			throw new IllegalArgumentException("Out of range: " + fraction);
		}
		if (fraction != this.lastFraction) {
			float sweepingAngle = FULL_CIRCLE * fraction;
			this.arc.setShape(new ArcShape(STARTING_ANGLE, sweepingAngle));
			this.postInvalidate();
			this.lastFraction = fraction;
		} else {
			Log.v(TAG, "Error");
		}
	}
}
