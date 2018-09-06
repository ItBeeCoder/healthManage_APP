package com.pillow.mainpage;

import java.util.List; 

import com.pillow.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAdapter extends BaseAdapter {
	/** TAG */
	private final static String TAG = "DragAdapter";
	/** ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** postion */
	private int holdPosition = -1;
	
	private boolean isChanged = false;

	boolean isVisible = true;
	
	public List<ChannelItem> channelList;
	/** TextView  */
	private TextView item_text;
	
	private ImageView item_image;
	
	/**  position */
	public int remove_position = -1;
	public boolean isIconVisible = false;
	public static int selectedPos = -1;
	public boolean isReset = false;

	public DragAdapter(Context context, List<ChannelItem> channelList) {
		this.context = context;
		this.channelList = channelList;
		selectedPos = -1;
	}

	public int getHoldPosition() {
		return holdPosition;
	}

	public void setHoldPosition(int holdPosition) {
		this.holdPosition = holdPosition;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public ChannelItem getItem(int position) {
		// TODO Auto-generated method stub
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.text_item);
		item_image = (ImageView) view.findViewById(R.id.iv_item);
		ChannelItem channel = getItem(position);
		item_text.setText(channel.getName());
		item_image.setBackgroundResource(channel.getImage());
		ImageView item_icon = (ImageView) view.findViewById(R.id.delete_icon);
		item_icon.setVisibility(View.GONE);

		if ((position == holdPosition && isItemShow)) {
			item_icon.setVisibility(View.VISIBLE);
			selectedPos = position;
			if (isReset) {
				item_icon.setVisibility(View.GONE);
				selectedPos = -1;
				holdPosition = -1;
			}
		}
		
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (remove_position == position) {
			item_text.setText("");
		}
		return view;
	}

	public void addItem(ChannelItem channel) {
		channelList.add(channel);
		notifyDataSetChanged();
	}

	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ChannelItem dragItem = getItem(dragPostion);
		Log.d(TAG, "startPostion=" + dragPostion + ";endPosition="
				+ dropPostion);
		if (dragPostion < dropPostion) {
			channelList.add(dropPostion + 1, dragItem);
			channelList.remove(dragPostion);
		} else {
			channelList.add(dropPostion, dragItem);
			channelList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}

	
	public List<ChannelItem> getChannnelLst() {
		return channelList;
	}

	/** ����ɾ���position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	
	public void remove() {
		channelList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}
	
	public void remove(int deletePosition) {
		channelList.remove(deletePosition);
		notifyDataSetChanged();
	}
	

	public void setListDate(List<ChannelItem> list) {
		channelList = list;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}


	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}

	public boolean isReset() {
		return isReset;
	}

	public void setReset(boolean isReset) {
		this.isReset = isReset;
	}

}