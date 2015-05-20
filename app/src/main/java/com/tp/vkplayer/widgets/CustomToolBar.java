package com.tp.vkplayer.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.tp.vkplayer.R;

/**
 * Created by Mily-V on 20.05.2015.
 */
public class CustomToolBar extends LinearLayout {
	private static final String TAG = "Custom";
	private ImageButton imageButton;
	private EditText editText;

	public CustomToolBar(Context context) {
		super(context);
	}

	public CustomToolBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.CustomSearchView, 0, 0);
			a.recycle();
		}

		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		((Activity) getContext())
				.getLayoutInflater()
				.inflate(R.layout.layout_tool_bar, this, true);

		imageButton = (ImageButton) findViewById(R.id.search_view_button_start_search);
		editText = (EditText) findViewById(R.id.search_view_edittext);

	}

	public void onClickStartSearch() {
		// TODO
	}

	public String getQuery() {
		Log.d(TAG, editText.getText().toString());
		return editText.getText().toString();
	}

}
