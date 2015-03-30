package com.hg.android.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.EditText;

import com.ThreeParty.R;

public class EditTextDialog extends AlertDialog {
	OnFinishedListener	finishedListener;
	EditText			editText;

	@SuppressLint("NewApi")
	public EditTextDialog(Context context, CharSequence title, CharSequence hint, CharSequence inputText,
			OnFinishedListener l) {
		super(context);
		finishedListener = l;
		editText = new EditText(context);
		editText.setGravity(Gravity.CENTER_VERTICAL);
		setView(editText);
		editText.setText(inputText);
		editText.setHint(hint);
		setTitle(title);
		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.common_ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (finishedListener != null) {
					finishedListener.onFinished(editText.getText().toString());
				}
			}
		});
	}

	public EditText getEditText() {
		return editText;
	}

	public void setFinishedListener(OnFinishedListener finishedListener) {
		this.finishedListener = finishedListener;
	}

	public interface OnFinishedListener {
		public void onFinished(String text);
	}
}
