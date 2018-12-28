package com.example.kyle.patiencetraining.Util;

import android.content.Context;

import com.example.kyle.patiencetraining.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Months;
import org.joda.time.Weeks;

import java.util.Calendar;
import java.util.Date;

public abstract class TimeString {

    public static String getTimeFromLong(long time, Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        Date startDate = calendar.getTime();
        calendar.setTimeInMillis(time);
        Date endDate = calendar.getTime();
        return getTimeStringBetween(startDate, endDate, context);
    }
    public static String getTimeStringBetween(Date fromDate, Date toDate, Context context){

        DateTime fromDateTime = new DateTime(fromDate);
        DateTime toDateTime = new DateTime(toDate);


        long months = Months.monthsBetween(fromDateTime, toDateTime).getMonths();
        long years = months/12;
        int weeks = 0;
        int days = 0;
        int hours = 0;

        if(months == 0) {
            weeks = Weeks.weeksBetween(fromDateTime, toDateTime).getWeeks();
            days = Days.daysBetween(fromDateTime, toDateTime).getDays();
            hours = Hours.hoursBetween(fromDateTime, toDateTime).getHours();
        }

        float quantifier;
        int qualifier;
        if (years > 0) {
            qualifier = R.string.year;
            quantifier = (float) months / 12;
        } else if (months > 0) {
            qualifier = R.string.month;
            quantifier = (float) weeks / 4;
        } else if (weeks > 0) {
            qualifier = R.string.week;
            quantifier = (float) days / 7;
        } else{
            qualifier = R.string.day;
            quantifier = (float) hours / 24;
        }

        String qString = context.getString(qualifier);
        return context.getString(R.string.time, quantifier,qString);
    }
}
