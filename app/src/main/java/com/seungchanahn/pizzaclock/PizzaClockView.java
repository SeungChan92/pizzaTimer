package com.seungchanahn.pizzaclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.seungchanahn.pizzaclock.events.ClockDrawEvent;

import java.util.TimeZone;

import de.greenrobot.event.EventBus;

/**
 * Created by anirban on 5/21/17.
 */

public class PizzaClockView extends View {
    private final Handler mHandler = new Handler();
    Context mContext;
    MyCount counter = new MyCount(10000, 1000);
    boolean mSeconds = false;
    float mSecond = 0;
    private Time mCalendar;
    private Drawable mHourHand;
    private Drawable mDial;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }
            onTimeChanged();
            invalidate();
        }
    };

    public PizzaClockView(Context context) {
        super(context);
    }

    public PizzaClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PizzaClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources r = context.getResources();
        mContext = context;
        mDial = ResourcesCompat.getDrawable(getResources(), R.drawable.dial, null);
        mHourHand = ResourcesCompat.getDrawable(getResources(), R.drawable.clock_hour, null);
        mCalendar = new Time();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }

        mCalendar = new Time();
        onTimeChanged();
        counter.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            counter.cancel();
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
                resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }
        boolean seconds = mSeconds;
        if (seconds) {
            mSeconds = false;
        }
        int availableWidth = canvas.getWidth();
        int availableHeight = canvas.getHeight();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = canvas.getWidth();
        int h = canvas.getHeight();


        if (availableWidth < w || availableHeight < h) {
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 24.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = canvas.getWidth()-100;
            h = canvas.getHeight()-100;
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
    }

    private void onTimeChanged() {
        mCalendar.setToNow();

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;
    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            counter.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mCalendar.setToNow();

            int hour = mCalendar.hour;
            int minute = mCalendar.minute;
            int second = mCalendar.second;

            mSecond = 6.0f * second;
            mSeconds = true;
            EventBus.getDefault().postSticky(new ClockDrawEvent(1000));

            PizzaClockView.this.invalidate();
        }
    }
}
