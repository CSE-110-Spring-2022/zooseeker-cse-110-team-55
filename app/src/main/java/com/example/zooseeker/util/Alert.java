package com.example.zooseeker.util;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.function.Consumer;

public class Alert {
    public static void emptyListAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Empty List")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void alert(Activity activity, String title, String message, Runnable acceptHandler, Runnable rejectHandler) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                    acceptHandler.run();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    rejectHandler.run();
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public interface AlertHandler {
        void acceptHandler();
        void rejectHandler();
    }
}
