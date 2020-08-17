package ort.edu.uy.photoplaces.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import ort.edu.uy.photoplaces.R;

public abstract class Alert {

    protected AlertDialog dialog;

    public Alert(Context context, String title, String message, int icon){
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        popupBuilder.setTitle(title); popupBuilder.setMessage(message);
        popupBuilder.setIcon(icon);
        popupBuilder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { close(); onConfim(); }
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
        this.show();
    }

    public Alert(Context context, String title, String message){
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        popupBuilder.setTitle(title); popupBuilder.setMessage(message);
        popupBuilder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        popupBuilder.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) { close(); onConfim(); }
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
        this.show();
    }

    public void show() { if(this.dialog !=null){ this.dialog.show(); } }
    public void close() { if(this.dialog !=null){ this.dialog.dismiss(); } }

    public abstract void onConfim();

}
