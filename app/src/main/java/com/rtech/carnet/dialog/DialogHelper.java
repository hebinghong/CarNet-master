package com.rtech.carnet.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rtech.carnet.R;


/*
 * Created by yingzhimeng on 2015/12/5.
 */
public class DialogHelper {

    public static void showCommonDialog(Context context, String title, String message, String buttonText, final View.OnClickListener listener) {
        final View dialogView = LayoutInflater.from(context) .inflate(R.layout.dialog_common, null, false);
        ((TextView)dialogView.findViewById(R.id.common_dialog_title)).setText(title);
        ((TextView)dialogView.findViewById(R.id.common_dialog_message)).setText(message);
        ((Button)dialogView.findViewById(R.id.button)).setText(buttonText);
        final AlertDialog dialog =  showViewDialog(context, dialogView);
        dialogView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) listener.onClick(v);
            }
        });
    }

    public static AlertDialog showViewDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

}
