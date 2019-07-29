package com.tyybbi.rekbong;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Helpers {
    private Helpers() {}

    static double calculatePercent(double spottedPlates) {
        final double total = 999;
        return (spottedPlates / total) * 100;
    }

    static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    static String convertDateToStr(long datetimeMS) {
        SimpleDateFormat simpleFormatDT = new SimpleDateFormat("d.M.yyyy HH:mm");
        Date readableDate = new Date(datetimeMS);

        return simpleFormatDT.format(readableDate);
    }

    static long convertDateToLong(String dateStr) {
        long dateInMillis = 0;
        SimpleDateFormat simpleFormatDT = new SimpleDateFormat("d.M.yyyy HH:mm");
        try {
            Date dateL = simpleFormatDT.parse(dateStr);
            dateInMillis = dateL.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInMillis;
    }
}
