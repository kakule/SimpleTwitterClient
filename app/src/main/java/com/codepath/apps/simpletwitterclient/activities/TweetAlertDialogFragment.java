package com.codepath.apps.simpletwitterclient.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created on 10/31/2016.
 */

public class TweetAlertDialogFragment extends DialogFragment  {


    public interface AlertDialogListener {
        void onFinishAlertDialog(boolean decision);
    }


    public TweetAlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static TweetAlertDialogFragment newInstance(String title) {
        TweetAlertDialogFragment frag = new TweetAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Do you want to save draft ?");
        alertDialogBuilder.setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBackResult(true);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBackResult(false);
            }
        });

        return alertDialogBuilder.create();
    }

    public void sendBackResult(boolean choice) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        AlertDialogListener listener = (AlertDialogListener) getTargetFragment();
        listener.onFinishAlertDialog(choice);
        //dismiss();
    }

}