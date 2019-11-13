package com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.udayasreesoftwaresolution.mybusinessanalysis.R;


public class ProgressDialog {
    private LoadingProgressDialog loadingProgressDialog;
    public ProgressDialog(Context context) {
        loadingProgressDialog = new LoadingProgressDialog(context);
        loadingProgressDialog.setCancelable(false);
    }

    public void show() {
        if (loadingProgressDialog != null && !loadingProgressDialog.isShowing()) {
            loadingProgressDialog.show();
        }
    }

    public void dismiss() {
        if (loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            loadingProgressDialog.dismiss();
        }
    }

    public class LoadingProgressDialog extends Dialog {
        private Context mContext;
        private LoadingTextView loadingProgressTextView;
        private LoadingProgress loadingProgress;
        LoadingProgressDialog(@NonNull Context context) {
            super(context);
            this.mContext = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.loading_progress);
            loadingProgress = new LoadingProgress();
            loadingProgressTextView = findViewById(R.id.loading_text_id);
            loadingProgressTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/blackjack.otf"));
            loadingProgress.start(loadingProgressTextView);
        }

        @Override
        protected void onStop() {
            super.onStop();
            loadingProgress.cancel();
        }
    }
}
