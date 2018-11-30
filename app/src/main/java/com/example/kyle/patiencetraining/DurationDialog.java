package com.example.kyle.patiencetraining;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DurationDialog extends Dialog {
    private int hours;
    private int days;
    private int weeks;
    private final NumberPicker hourPicker;
    private final NumberPicker dayPicker;
    private final NumberPicker weekPicker;
    private final String error;
    private final  TextView errorText;
    private final Button okButton;

    public DurationDialog(@NonNull Context context, String error, final OnDurationSetListener listener) {
        super(context);
        setContentView(R.layout.dialog_duration_picker);
        errorText = findViewById(R.id.errorTextView);
        this.error = error;
        errorText.setText(error);
        setTitle(R.string.duration_title);
        hourPicker = findViewById(R.id.hourPicker);
        hourPicker.setMaxValue(23);
        dayPicker = findViewById(R.id.dayPicker);
        dayPicker.setMaxValue(6);
        weekPicker = findViewById(R.id.weekPicker);
        weekPicker.setMaxValue(100);

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                hours = newVal;
                if(oldVal == 23 && newVal == 0)
                    dayPicker.setValue(++days);
                else if(oldVal == 0 && newVal == 23 && days > 0)
                    dayPicker.setValue(--days);
                checkError();
            }
        });

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                days = newVal;
                if(oldVal == 6 && newVal == 0)
                    weekPicker.setValue(++weeks);
                else if(oldVal == 0 && newVal == 23 && weeks > 0)
                    weekPicker.setValue(--weeks);
                checkError();
            }
        });

        weekPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                weeks = newVal;
                checkError();
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hours = hourPicker.getValue();
                days = dayPicker.getValue();
                weeks = weekPicker.getValue();
                listener.onDurationSet(hours, days, weeks);
                dismiss();
            }
        });
    }

    private void checkError(){
        if(hours+days+weeks == 0){
            setError(error);
        }else{
            setError(null);
        }
    }

    public void setError(String errorString){
        if(errorString == null) {
            errorText.setVisibility(View.INVISIBLE);
            okButton.setEnabled(true);
        }else{
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(errorString);
            okButton.setEnabled(false);
        }

    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
        hourPicker.setValue(hours);
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
        dayPicker.setValue(days);
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
        weekPicker.setValue(weeks);
    }

    public interface OnDurationSetListener{
        void onDurationSet(int hours, int days, int weeks);
    }
}
