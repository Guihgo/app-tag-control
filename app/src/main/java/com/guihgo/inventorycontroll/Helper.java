package com.guihgo.inventorycontroll;

import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigInteger;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.VIBRATOR_SERVICE;

public class Helper {
    public static void confirmTouch(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (!v.hasVibrator()) return;
        long timeToVibrate = 50;
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
            Helper.stopReadNFC(activity);
        });

    }
    public static void stopReadNFC(Activity activity) {
        if(NfcAdapter.getDefaultAdapter(activity).isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                NfcAdapter.getDefaultAdapter(activity).disableReaderMode(activity);
            }
        }
    }
    public static String bytesToHex(byte[] bytes) {
        return (new BigInteger(1, bytes)).toString(16);
    }
}
