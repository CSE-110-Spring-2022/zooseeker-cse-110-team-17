package com.example.team17zooseeker;
import android.app.Activity;
import android.app.AlertDialog;

import java.util.Optional;

public class Utilities {
    public static void showAlert(Activity activiy, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activiy);

        alertBuilder.setTitle("Alert!").setMessage(message).
                setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                }).setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static Optional<Integer> parseCount(String s){
        try{
            int maxCount = Integer.parseInt(s);
            return Optional.of(maxCount);
        }catch(NumberFormatException e){
            return Optional.empty();
        }
    }
}
