package com.seungchanahn.pizzaclock;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.seungchanahn.pizzaclock.events.ClockDrawEvent;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private TimerView pieChart;
    private Drawable mDial;
    private int eventHour = 0;
    private int eventMin = 0;
    private int currentHour;
    private EditText eventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieChart = (TimerView) findViewById(R.id.pieChart);
        mDial = ResourcesCompat.getDrawable(getResources(), R.drawable.dial, null);
        eventTime = (EditText) findViewById(R.id.eventTime);

        // 1/24 = .041666666
        // Single hour = .041666666
        Calendar rightNow = Calendar.getInstance();
        currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMin = rightNow.get(Calendar.MINUTE);

        // 1.3 becoz dial image should be bigger than clock
        // startingHours - event start time
        // Clock angle 00:00 - -90 degree && 24:00 -270 degree
        // Each hour 15 degree
        pieChart.settingRadius((int) (mDial.getIntrinsicWidth() / 1.3));
        pieChart.settingStartingAngle(-90 + (currentHour * 15 + ((15 * currentMin) / 60)));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ClockDrawEvent event) {
        Calendar rightNow = Calendar.getInstance();
        currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMin = rightNow.get(Calendar.MINUTE);

        if (eventHour != 0) {
            pieChart.settingStartingAngle(-90 + (currentHour * 15 + ((15 * currentMin) / 60)));

            int minDiff = 0;
            minDiff = currentMin - eventMin;

            if (currentHour <= eventHour) {
                if (currentHour == eventHour && minDiff > 0)
                    pieChart.setFraction((float) (.041666666 / 60) * (24 * 60 - minDiff));
                else
                    pieChart.setFraction((float) (.041666666 / 60) * ((eventHour - currentHour) * 60 - minDiff));
            } else
                pieChart.setFraction((float) (.041666666 / 60) * ((Math.abs(currentHour - eventHour - 24) * 60) - minDiff));
        }
    }

    public void selectingEventTime(final View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                eventHour = selectedHour;
                eventMin = selectedMinute;

                ((EditText) view).setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }
}
