package ort.edu.uy.photoplaces.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import ort.edu.uy.photoplaces.R;

public abstract class Question {

    protected AlertDialog dialog;

    public Question(Context context, String title, String message, String confimButtonText, String cancelButtonText, int icon){
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context,R.style.AppTheme_Dialog);
        popupBuilder.setTitle(title); popupBuilder.setMessage(message);
        popupBuilder.setIcon(icon);
        popupBuilder.setPositiveButton(confimButtonText, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { dialog.dismiss(); onConfirm(); }
        });
        popupBuilder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { dialog.dismiss(); onCancel(); }
        });
        this.dialog = popupBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setFocusable(true); positiveButton.setFocusableInTouchMode(true);
                positiveButton.requestFocus();
            }
        });
        this.dialog.show();
    }

    public Question(Context context, String title, String message, int icon){
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context,R.style.AppTheme_Dialog);
        popupBuilder.setTitle(title); popupBuilder.setMessage(message);
        popupBuilder.setIcon(icon);
        popupBuilder.setPositiveButton(context.getString(R.string.dialogo_yes), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { dialog.dismiss(); onConfirm(); }
        });
        popupBuilder.setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { dialog.dismiss(); onCancel(); }
        });
        this.dialog = popupBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setFocusable(true); positiveButton.setFocusableInTouchMode(true);
                positiveButton.requestFocus();
            }
        });
        this.dialog.show();
    }

    public abstract void onConfirm();
    public abstract void onCancel();

}
