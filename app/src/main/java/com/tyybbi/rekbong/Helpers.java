package com.tyybbi.rekbong;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

final class Helpers {
    private Helpers() {}

    static double calculatePercent(double spottedPlates) {
        final double total = 999;
        return (spottedPlates / total) * 100;
    }

    static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    static String convertDateToStr(long datetimeMS) {
        SimpleDateFormat simpleFormatDT = new SimpleDateFormat
                ("d.M.yyyy HH:mm", Locale.getDefault());
        Date readableDate = new Date(datetimeMS);

        return simpleFormatDT.format(readableDate);
    }

    static long convertDateToLong(String dateStr) {
        long dateInMillis = 0;
        SimpleDateFormat simpleFormatDT = new SimpleDateFormat
                ("d.M.yyyy HH:mm", Locale.getDefault());
        try {
            Date dateL = simpleFormatDT.parse(dateStr);
            dateInMillis = dateL.getTime();
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return dateInMillis;
    }

    static String getSpottingTime(Context context, long first, long latest) {
        SimpleDateFormat simpleFormatDT = new SimpleDateFormat
                ("d.M.yyyy HH:mm", Locale.getDefault());
        String startTime = convertDateToStr(first);
        String latestAdd = convertDateToStr(latest);
        String spottingTime = "";
        long timeDays = 0;
        long months = 0;
        long years = 0;

        try {
            Date date1 = simpleFormatDT.parse(startTime);
            Date date2 = simpleFormatDT.parse(latestAdd);
            long timeBetween = date1.getTime() - date2.getTime();
            timeDays = TimeUnit.MILLISECONDS.toDays(timeBetween);
            years = timeDays / 365;
            timeDays %= 365;
            months = timeDays / 30;
            timeDays %= 30;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        spottingTime = String.format(Locale.getDefault(), "%d", years) + MainActivity.SPACE
                + context.getString(R.string.about_dlg_progress_years) + MainActivity.SPACE
                + String.format(Locale.getDefault(), "%d", months) + MainActivity.SPACE
                + context.getString(R.string.about_dlg_progress_months) + MainActivity.SPACE
                + String.format(Locale.getDefault(), "%d", timeDays) + MainActivity.SPACE
                + context.getString(R.string.about_dlg_progress_days);

        return spottingTime;
    }

    static int getNextPlateNumber(ArrayList<Integer> plateNumbers, boolean reverse) {
        int nextPlate = -2;
        Collections.sort(plateNumbers);

        // If database is empty or full
        if ((plateNumbers.size() == 0) && (!reverse)) {
            return 1;
        } else if (plateNumbers.size() == 0) {
            return 999;
        } else if (plateNumbers.size() == 999) {
            return nextPlate;
        }

        for (int i = 1; i < plateNumbers.size(); i++) {
            if ((plateNumbers.get(i - 1) + 1) != (plateNumbers.get(i))) {
                if (reverse) {
                    return plateNumbers.get(i) - 1;
                } else {
                    return plateNumbers.get(i - 1) + 1;
                }
            }
        }

        if (!reverse) {
            return Collections.max(plateNumbers) + 1;
        } else {
            return Collections.min(plateNumbers) - 1;
        }
    }
}
