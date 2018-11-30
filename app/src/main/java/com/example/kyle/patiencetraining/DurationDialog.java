package com.example.kyle.patiencetraining;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

public class DurationDialog extends Dialog {
    /**
     * Todo
     * Make each spinner automatically roll onto the next one, once days gets to 7 it spins weeks to +1 etc
     * Set the dialog to the current set value on the button
     */

    private int hours;
    private int days;
    private int weeks;

    public DurationDialog(@NonNull Context context, final OnDurationSetListener listener) {
        super(context);
        setContentView(R.layout.duration_picker);
        setTitle(R.string.duration_title);
        final NumberPicker hourPicker = findViewById(R.id.hourPicker);
        hourPicker.setMaxValue(23);
        final NumberPicker dayPicker = findViewById(R.id.dayPicker);
        dayPicker.setMaxValue(7);
        final NumberPicker weekPicker = findViewById(R.id.weekPicker);
        weekPicker.setMaxValue(100);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hours = hourPicker.getValue();
                days = dayPicker.getValue();
                weeks = weekPicker.getValue();
                listener.onDurationSet(hours,days,weeks);
                dismiss();
            }
        });
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public interface OnDurationSetListener{
        void onDurationSet(int hours, int days, int weeks);
    }
}
