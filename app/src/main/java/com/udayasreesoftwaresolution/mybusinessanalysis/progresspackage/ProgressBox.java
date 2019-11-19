package com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBox {

    private Context context;
    private ProgressDialog progressDialog;

    public ProgressBox(Context context) {
        this.context = context;
        build();
    }

    private void build() {
        progressDialog = new ProgressDialog(context, AlertDialog.THEME_HOLO_DARK);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Connecting to server. Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void show() {
        dismiss();
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
