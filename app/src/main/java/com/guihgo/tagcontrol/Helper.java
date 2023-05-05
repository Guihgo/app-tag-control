package com.guihgo.tagcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigInteger;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.VIBRATOR_SERVICE;

public class Helper {

    public static int LANGUAGE_ARRAY = R.array.settings_languages;
    public static String LANGUAGE_FILE_NAME = "language";
    public static String LANGUAGE_KEY_LANGUAGE_CODE = "lang_code";
    public static String LANGUAGE_KEY_COUNTRY_CODE = "country_code";


    public static String THEME_FILE_NAME = "theme";
    public static String THEME_KEY_STYLE = "style";

    public static void saveTheme(Context context, String themeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(THEME_KEY_STYLE, themeName);

        editor.apply();
    }

    public static boolean restoreTheme(Activity activity, boolean noActionBar) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(THEME_FILE_NAME, Context.MODE_PRIVATE);

        String themeStyle = sharedPreferences.getString(THEME_KEY_STYLE, "dark").toLowerCase();
        int style = R.style.Theme_TagControl;
        boolean isDarkTheme = true;
        switch (themeStyle) {
            case "dark":
                isDarkTheme = true;
                style = (noActionBar) ? R.style.Theme_TagControl_NoActionBar : R.style.Theme_TagControl;
                break;
            case "light":
                isDarkTheme = false;
                style = (noActionBar) ? R.style.Theme_TagControl_NoActionBar_Light : R.style.Theme_TagControl_Light;
                break;
        }

        activity.getTheme().applyStyle(style, true);

        return isDarkTheme;
    }

    public static boolean isDarkTheme(String themeStyle) {
        return (themeStyle.equalsIgnoreCase("dark"));
    }

    public static void saveApplicationLocale(Context context, Locale locale) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LANGUAGE_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LANGUAGE_KEY_LANGUAGE_CODE, locale.getLanguage());
        editor.putString(LANGUAGE_KEY_COUNTRY_CODE, locale.getCountry());

        editor.apply();
    }

    public static void restoreApplicationLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LANGUAGE_FILE_NAME, Context.MODE_PRIVATE);

        Locale locale = new Locale(sharedPreferences.getString(LANGUAGE_KEY_LANGUAGE_CODE, "en"), sharedPreferences.getString(LANGUAGE_KEY_COUNTRY_CODE, ""));

        Helper.setApplicationLocale(context, locale);
    }

    public static void setApplicationLocale(Context context, Locale locale) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        resources.updateConfiguration(config, dm);
    }

    public static Locale getApplicationLocale(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        return config.locale;
    }

    public static String getApplicationLanguage(Context context, Locale locale) {
        String[] languages = context.getResources().getStringArray(LANGUAGE_ARRAY);
        return languages[Helper.getPositionByLocale(context, locale)];
    }

    public static Locale getLocaleByPosition(Context context, int position) {
        String[] languages = context.getResources().getStringArray(LANGUAGE_ARRAY);
        String[] lang = languages[position].substring(languages[position].indexOf("(") + 1, languages[position].indexOf(")")).trim().split("_");
        return new Locale(lang[0], (lang.length == 2) ? lang[1] : "");
    }

    public static int getPositionByLocale(Context context, Locale locale) {
        String[] languages = context.getResources().getStringArray(LANGUAGE_ARRAY);

        int position = languages.length - 1;
        for (; position >= 0; position--) {
            String[] lang = languages[position].substring(languages[position].indexOf("(") + 1, languages[position].indexOf(")")).trim().split("_");
            Locale _locale = new Locale(lang[0], (lang.length == 2) ? lang[1] : "");
            if (_locale.toString().equalsIgnoreCase(locale.toString())) {
                return position;
            }
        }

        return position;
    }

    public static void confirmTouch(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (!v.hasVibrator()) return;
        long timeToVibrate = 20;
        // Vibrate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(timeToVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(timeToVibrate);
        }
    }

    public interface OnScanTagListener {
        void onStop();

        void onScanned(Tag tag);
    }

    ;

    public static void scanTag(Activity activity, OnScanTagListener onScanTagListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Toast.makeText(activity, "Your device isn't compatibility with NFc", Toast.LENGTH_SHORT).show();
            return;
        }

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Helper.confirmTouch(activity);
            }
        };
        t.scheduleAtFixedRate(tt, 0, 1000);

        MaterialAlertDialogBuilder madbConfirm = new MaterialAlertDialogBuilder(activity);

        String title = activity.getString(R.string.dialog_scan_tag_title);

        String message = activity.getString(R.string.dialog_scan_tag_message);


        madbConfirm.setTitle(title);
        madbConfirm.setIcon(R.drawable.ic_contactless);
        madbConfirm.setMessage(message);

        madbConfirm.setPositiveButton(activity.getResources().getString(R.string.dialog_scan_tag_button_stop), (dialog, which) -> {
            Helper.stopReadNFC(activity);
            onScanTagListener.onStop();
        });

        AlertDialog dialog = madbConfirm.show();
        /* Documentation: https://developer.android.com/reference/android/nfc/NfcAdapter#enableReaderMode(android.app.Activity,%20android.nfc.NfcAdapter.ReaderCallback,%20int,%20android.os.Bundle)*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            NfcAdapter.getDefaultAdapter(activity).enableReaderMode(activity, new NfcAdapter.ReaderCallback() {
                @Override
                public void onTagDiscovered(Tag tag) {
                    NfcAdapter.getDefaultAdapter(activity).disableReaderMode(activity);
                    dialog.dismiss();
                    onScanTagListener.onScanned(tag);
                }
            }, NfcAdapter.FLAG_READER_NFC_A |
                    NfcAdapter.FLAG_READER_NFC_B |
                    NfcAdapter.FLAG_READER_NFC_F |
                    NfcAdapter.FLAG_READER_NFC_V |
                    NfcAdapter.FLAG_READER_NFC_BARCODE, null);
        }

        dialog.setOnDismissListener(dialog1 -> {
            t.cancel();
            Helper.stopReadNFC(activity);
        });

    }

    public static void stopReadNFC(Activity activity) {
        if (NfcAdapter.getDefaultAdapter(activity).isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                NfcAdapter.getDefaultAdapter(activity).disableReaderMode(activity);
            }
        }
    }

    public static String bytesToHex(byte[] bytes) {
        return (new BigInteger(1, bytes)).toString(16);
    }
}
