package com.example.team17zooseeker;
import android.app.Activity;
import android.app.AlertDialog;

import java.util.Optional;

public class Utilities {

    private static boolean updatedCurrentlyPrompted = false;

    public static void showAlert(Activity activiy, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activiy);

        alertBuilder.setTitle("Alert!").setMessage(message).
                setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                }).setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void promptUpdatePath(Activity activiy, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activiy);

        //While prompting user don't check for more reroutes and don't prompt again
        DynamicDirections.setDynamicEnabled(false);

        //We are displaying a message
        updatedCurrentlyPrompted = true;

        alertBuilder.setTitle("New Path Found!").setMessage(message).
                setPositiveButton("Yes. Reroute Me!", (dialog, id) -> {
                    //Check for any more reroutes
                    DynamicDirections.setDynamicEnabled(true);
                    DynamicDirections.pathApproved();
                    dialog.cancel();
                    updatedCurrentlyPrompted = false;
                }).setNegativeButton("No Thanks!", (dialog, id) -> {
                    //dynamic directions will remain off
                    updatedCurrentlyPrompted = false;
                    dialog.cancel();
                }).setCancelable(false);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static boolean getUpdateCurrentlyPrompted(){ return updatedCurrentlyPrompted; }
}
