package com.common.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil
{
	public static void show(Context context, String title, String message)
	{
		show(context, title, message, "确定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		}, null, null);
	}

	public static void show(Context context, String title, String message, String okButtonText, OnClickListener okButtonListener)
	{
		show(context, title, message, okButtonText, okButtonListener, null, null);
	}

	public static void show(Context context, String title, String message, String okButtonText, OnClickListener okButtonListener, String cancelButtonText, OnClickListener cancelButtonListener)
	{
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(okButtonText, okButtonListener);
		builder.setNegativeButton(cancelButtonText, cancelButtonListener);
		builder.create().show();
	}

}
