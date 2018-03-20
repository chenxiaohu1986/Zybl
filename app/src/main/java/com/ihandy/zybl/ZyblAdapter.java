package com.ihandy.zybl;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ihandy.zybl.entity.ItemMatch;
import com.ihandy.zybl.entity.ItemZybl;

import java.util.List;

/**
 * Created by cxh on 18/3/15.
 */

public class ZyblAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private ItemMatch itemMatch;
	private List<ItemZybl> list;

	public ZyblAdapter(Context context,ItemMatch itemMatch) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.itemMatch = itemMatch;
		this.list = itemMatch.getList();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ItemZybl itemZybl = list.get(position);
		String zhuke = itemZybl.getZhuke().trim();
		if (convertView == null) {
			holder = new ViewHolder();


			if ("主".equals(zhuke)){
				convertView = mInflater.inflate(R.layout.item_zybl_home, null);
			}else if ("客".equals(zhuke)){
				convertView = mInflater.inflate(R.layout.item_zybl_away, null);
			}
			holder.des = (TextView) convertView.findViewById(R.id.des);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.teamIcon = (ImageView) convertView.findViewById(R.id.teamIcon);

			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}


		holder.des.setText(itemZybl.getDes());

		holder.title.setText(itemZybl.getTitle());

		holder.content.setText(itemZybl.getContent());


		try {
			if ("主".equals(zhuke)) {
				Glide.with(context).load(itemMatch.getHomeIcon()).into(holder.teamIcon);
			} else if ("客".equals(zhuke)) {
				Glide.with(context).load(itemMatch.getAwayIcon()).into(holder.teamIcon);
			}
		}catch (Exception e){
			Log.e("zybl",e.getMessage());
		}


		return convertView;
	}



	public final class ViewHolder {
		public ImageView teamIcon;
		public TextView des;
		public TextView title;
		public TextView content;
	}

}
