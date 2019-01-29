package com.example.kyle.patiencetraining.util;

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
        double weeks = 0;
        double days = 0;
        double hours = 0;

        weeks = Weeks.weeksBetween(fromDateTime, toDateTime).getWeeks();
        days = Days.daysBetween(fromDateTime, toDateTime).getDays();
        hours = Hours.hoursBetween(fromDateTime, toDateTime).getHours();

        double quantifier;
        int qualifier;
        if (years > 0) {
            qualifier = R.string.year;
            quantifier = months / 12d;
        } else if (months > 0) {
            qualifier = R.string.month;
            quantifier = weeks / 4d;
        } else if (weeks > 0) {
            qualifier = R.string.week;
            quantifier = days / 7d;
        } else{
            qualifier = R.string.day;
            quantifier = hours / 24d;
        }

        String qString = context.getString(qualifier);
        return context.getString(R.string.time, quantifier,qString);
    }
}
