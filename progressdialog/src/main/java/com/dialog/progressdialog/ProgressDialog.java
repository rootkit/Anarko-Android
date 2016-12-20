package com.dialog.progressdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Administrator on 10/8/2016.
 */
public class ProgressDialog extends Dialog {

    public Context          context;
    private TextView        lblProgress;
    private ProgressBar     progressBar;

    public ProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) getContext().getResources().getDisplayMetrics().density * 240;
        lp.height = (int) getContext().getResources().getDisplayMetrics().density * 80;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        this.setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.dialog_progress);

        lblProgress = (TextView) findViewById(R.id.recorder_progress_progresstext);
        progressBar = (ProgressBar) findViewById(R.id.recorder_progress_progressbar);
    }

    public void setProgress(int progress) {
        lblProgress.setText(String.valueOf(progress) + "%");
        progressBar.setProgress(progress);
    }

}
