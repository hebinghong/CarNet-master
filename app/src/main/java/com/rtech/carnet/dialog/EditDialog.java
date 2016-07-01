package com.rtech.carnet.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rtech.carnet.R;

/*
 * Created by zhimeng on 2016/4/26.
 */
public class EditDialog {

    public interface OnEditListener {
        void done(String string);
    }

    public EditDialog(Context context, String title, boolean isNumber, final OnEditListener listener) {
        final View dialogView = LayoutInflater.from(context) .inflate(R.layout.dialog_edit, null, false);
        if (isNumber) ((EditText) dialogView.findViewById(R.id.edit_dialog_content)).setInputType(InputType.TYPE_CLASS_NUMBER);
        ((TextView) dialogView.findViewById(R.id.edit_dialog_title)).setText(title);
        final AlertDialog dialog = DialogHelper.showViewDialog(context, dialogView);
        dialogView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener == null) return;
                listener.done(((EditText) dialogView.findViewById(R.id.edit_dialog_content)).getText().toString());
            }
        });
    }
}
