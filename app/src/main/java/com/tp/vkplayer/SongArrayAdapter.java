package com.tp.vkplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * author s.titaevskiy on 19.05.15.
 */
public class SongArrayAdapter extends ArrayAdapter<SongObject> {

	public SongArrayAdapter(Context context, List<SongObject> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext())
					.inflate(R.layout.layout_song_fragment, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.titleText = (TextView) convertView.findViewById(R.id.song_fragment_title);
			viewHolder.artistText = (TextView) convertView.findViewById(R.id.song_fragment_artist);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.titleText.setText(getItem(position).getTitle());
		viewHolder.artistText.setText(getItem(position).getArtist());
		return convertView;
	}

	private class ViewHolder {
		public TextView titleText;
		public TextView artistText;
	}
}
