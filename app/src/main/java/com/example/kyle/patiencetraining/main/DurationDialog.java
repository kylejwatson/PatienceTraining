package com.example.kyle.patiencetraining.main;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.kyle.patiencetraining.R;

import androidx.annotation.NonNull;

class DurationDialog extends Dialog {
    private int hours;
    private int days;
    private int weeks;
    private final NumberPicker hourPicker;
    private final NumberPicker dayPicker;
    private final NumberPicker weekPicker;

    private int seconds;
    private final String error;
    private final  TextView errorText;
    private final ImageView errorIcon;
    private final Button okButton;
    private static final boolean DEBUG = true;

    DurationDialog(@NonNull Context context, String error, final OnDurationSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_duration_picker);
        errorText = findViewById(R.id.errorTextView);
        errorIcon = findViewById(R.id.errorImageView);
        this.error = error;
        errorText.setText(error);
        setTitle(R.string.duration_title);
        hourPicker = findViewById(R.id.hourPicker);
        hourPicker.setMaxValue(23);
        dayPicker = findViewById(R.id.dayPicker);
        dayPicker.setMaxValue(6);
        weekPicker = findViewById(R.id.weekPicker);
        weekPicker.setMaxValue(100);

        NumberPicker secondPicker = findViewById(R.id.secondPicker);
        if(DEBUG) {
            TextView secondLabel = findViewById(R.id.secondLabel);
            secondLabel.setVisibility(View.VISIBLE);
            secondPicker.setVisibility(View.VISIBLE);
            secondPicker.setMaxValue(60);
            secondPicker.setValue(1);
            secondPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
                seconds = i1;
                checkError();
            });
        }

        hourPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
            hours = newVal;
            if(oldVal == 23 && newVal == 0)
                dayPicker.setValue(++days);
            else if(oldVal == 0 && newVal == 23 && days > 0)
                dayPicker.setValue(--days);
            checkError();
        });

        dayPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
            days = newVal;
            if(oldVal == 6 && newVal == 0)
                weekPicker.setValue(++weeks);
            else if(oldVal == 0 && newVal == 23 && weeks > 0)
                weekPicker.setValue(--weeks);
            checkError();
        });

        weekPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
            weeks = newVal;
            checkError();
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> dismiss());
        okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(view -> {
            hours = hourPicker.getValue();
            days = dayPicker.getValue();
            weeks = weekPicker.getValue();
            listener.onDurationSet(hours, days, weeks, seconds);
            dismiss();
        });
    }

    private void checkError(){
        if(hours+days+weeks+seconds == 0 && DEBUG || hours+days+weeks == 0 && !DEBUG){
            setError(error);
        }else{
            setError(null);
        }
    }

    private void setError(String errorString){
        if(errorString == null) {
            errorText.setVisibility(View.INVISIBLE);
            errorIcon.setVisibility(View.INVISIBLE);
            okButton.setEnabled(true);
        }else{
            errorText.setVisibility(View.VISIBLE);
            errorIcon.setVisibility(View.VISIBLE);
            errorText.setText(errorString);
            okButton.setEnabled(false);
        }

    }

//    public int getHours() {
//        return hours;
//    }

    void setHours(int hours) {
        this.hours = hours;
        hourPicker.setValue(hours);
    }

//    public int getDays() {
//        return days;
//    }

    void setDays(int days) {
        this.days = days;
        dayPicker.setValue(days);
    }

//    public int getWeeks() {
//        return weeks;
//    }

    void setWeeks(int weeks) {
        this.weeks = weeks;
        weekPicker.setValue(weeks);
    }

    public interface OnDurationSetListener{
        void onDurationSet(int hours, int days, int weeks, int seconds);
    }
}
