package com.elegion.myfirstapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getFormattedDate(String jsonTimeStamp) {
        Date curDate = new Date();
        Date commentDate;
        SimpleDateFormat outDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outTime = new SimpleDateFormat("hh:mm:ss");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss+hh:mm");
        try {
            commentDate = dateFormat.parse(jsonTimeStamp);
        } catch (java.text.ParseException e) {
            commentDate = new Date();
        }
        long diffSeconds = (curDate.getTime() - commentDate.getTime())/1000;
        if (diffSeconds > 24 * 60 * 60 ) {
            return outDate.format(commentDate);
        } else {
            return outTime.format(commentDate.getTime());
        }
    }
}
