package com.tp.vkplayer.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tp.vkplayer.R;

import java.util.zip.CheckedOutputStream;

/**
 * Created by Mily-V on 20.05.2015.
 */
public class CustomSearchView extends LinearLayout {
	private ImageView imageView;
	private EditText editText;
	private static final String TAG = "Custom";

	public CustomSearchView (Context context) {
		super(context);
	}

	public CustomSearchView (Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		((Activity)getContext())
				.getLayoutInflater()
				.inflate(R.layout.layout_search_view, this, true);

		imageView = (ImageView) findViewById(R.id.search_view_image);
		editText = (EditText) findViewById(R.id.search_view_edittext);

	}

	public String getQuery() {
//		Log.d(TAG, editText.getText().toString());
		return editText.getText().toString();
	}

}
