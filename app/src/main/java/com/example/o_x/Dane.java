package com.example.o_x;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marcin on 2016-02-03.
 */
public class Dane {
    public static String URL = "http://kolko-krzyzyk.rhcloud.com/webresources";
    public static Osoba osobaZalogowana;

    public static String wyswietlDate(Date date) {

        Long dzien = Long.valueOf(86400000);
        Long aktualnyCzas = new Date().getTime();

        Long roznica = aktualnyCzas - date.getTime();

        int zlicz = 0;
        for (int i = 0; i < 2; i++) {
            if (roznica < dzien)
                break;

            dzien *= 2;
            zlicz++;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar calendarPom = Calendar.getInstance();
        calendarPom.setTime(new Date());

        String wynik = null;
        switch (zlicz) {
            case 0:
                if (calendar.get(Calendar.HOUR_OF_DAY) > calendarPom.get(Calendar.HOUR_OF_DAY) ||
                        (calendar.get(Calendar.HOUR_OF_DAY) == calendarPom.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE) > calendarPom.get(Calendar.MINUTE)))
                    wynik = "wczoraj " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                else
                    wynik = "dziś " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                break;
            case 1:
                wynik = "wczoraj " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                break;
            default:
                wynik = calendar.get(Calendar.DAY_OF_MONTH) + " " + miesiac(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR);
                break;
        }

        return wynik;
    }

    public static String miesiac(int miesciac) {
        switch (miesciac) {
            case 0:
                return "sty";
            case 1:
                return "lut";
            case 2:
                return "mar";
            case 3:
                return "kwi";
            case 4:
                return "maj";
            case 5:
                return "cze";
            case 6:
                return "lip";
            case 7:
                return "sie";
            case 8:
                return "wrz";
            case 9:
                return "paź";
            case 10:
                return "lis";
            case 11:
                return "gru";
            default:
                return "sty";
        }
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }
}

