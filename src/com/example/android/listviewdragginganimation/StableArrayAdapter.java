/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.listviewdragginganimation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<String> {

	final int INVALID_ID = -1;

	HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	private List<String> objects;
	private Context context;
	private DynamicListView listView;
	public StableArrayAdapter(Context context, List<String> objects, DynamicListView listView) {
		super(context, R.layout.contact_list_item, objects);
		this.objects = objects;
		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
		}
		this.context = context;
		this.listView = listView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {

			rowView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.youtube = (YoutubeLayout) rowView.findViewById(R.id.youtubeLayout);
			viewHolder.tv_name = (TextView) rowView.findViewById(R.id.viewHeader);
			
//			rowView.setOnLongClickListener(new OnLongClickListener(){
//
//				@Override
//				public boolean onLongClick(View arg0) {
//					Toast.makeText(context, "long click!", Toast.LENGTH_SHORT).show();
//					listView.handleLongClick(position);
//					return true;
//				}
//				
//			});
			
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.tv_name.setText(objects.get(position));

		return rowView;
	}

	public static class ViewHolder {
		public TextView tv_name;
		public YoutubeLayout youtube;
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= mIdMap.size()) {
			return INVALID_ID;
		}
		String item = getItem(position);
		return mIdMap.get(item);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
