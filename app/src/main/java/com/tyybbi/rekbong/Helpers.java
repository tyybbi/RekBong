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
