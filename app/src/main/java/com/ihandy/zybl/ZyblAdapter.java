package com.ihandy.zybl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ihandy.zybl.entity.ItemMatch;
import com.ihandy.zybl.entity.ItemZybl;

import java.util.List;

/**
 * Created by cxh on 18/3/15.
 */

public class ZyblAdapter extends RecyclerView.Adapter<ZyblAdapter.MyViewHolder> {

	private final int EMPTY_VIEW = 0;
	private final int ZHU_VIEW = 1;
	private final int KE_VIEW = 2;


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

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = null;
		if (EMPTY_VIEW == viewType){
			view = mInflater.inflate(R.layout.item_empty, parent, false);
		} else if (ZHU_VIEW == viewType){
			view = mInflater.inflate(R.layout.item_zybl_home, parent, false);
		} else if (KE_VIEW == viewType){
			view = mInflater.inflate(R.layout.item_zybl_away, parent, false);
		}
		ZyblAdapter.MyViewHolder holder = new ZyblAdapter.MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		ItemZybl itemZybl = list.get(position);
		String zhuke = itemZybl.getZhuke().trim();
		//Log.i("zybl", itemZybl.getDes() +  "------" +zhuke);
		if ("".equals(zhuke)){
			holder.tvEmpty.setText("暂无消息");
		}else {
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
		}
	}

	@Override
	public int getItemViewType(int position) {
		ItemZybl itemZybl = list.get(position);
		String zhuke = itemZybl.getZhuke();
		if ("".equals(zhuke)){
			return EMPTY_VIEW;
		}
		else if ("主".equals(zhuke)) {
			return ZHU_VIEW;
		}else if ("客".equals(zhuke)){
			return KE_VIEW;
		}
		return super.getItemViewType(position);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder holder = null;
//		ItemZybl itemZybl = list.get(position);
//		String zhuke = itemZybl.getZhuke().trim();
//		if (convertView == null) {
//			holder = new ViewHolder();
//			if ("主".equals(zhuke)){
//				convertView = mInflater.inflate(R.layout.item_zybl_home, null);
//			}else if ("客".equals(zhuke)){
//				convertView = mInflater.inflate(R.layout.item_zybl_away, null);
//			}
//			holder.des = (TextView) convertView.findViewById(R.id.des);
//			holder.title = (TextView) convertView.findViewById(R.id.title);
//			holder.content = (TextView) convertView.findViewById(R.id.content);
//			holder.teamIcon = (ImageView) convertView.findViewById(R.id.teamIcon);
//
//			convertView.setTag(holder);
//
//		} else {
//
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//
//		holder.des.setText(itemZybl.getDes());
//
//		holder.title.setText(itemZybl.getTitle());
//
//		holder.content.setText(itemZybl.getContent());
//
//
//		try {
//			if ("主".equals(zhuke)) {
//				Glide.with(context).load(itemMatch.getHomeIcon()).into(holder.teamIcon);
//			} else if ("客".equals(zhuke)) {
//				Glide.with(context).load(itemMatch.getAwayIcon()).into(holder.teamIcon);
//			}
//		}catch (Exception e){
//			Log.e("zybl",e.getMessage());
//		}
//
//
//		return convertView;
//	}


	public class MyViewHolder extends RecyclerView.ViewHolder {

		public TextView tvEmpty;
		public ImageView teamIcon;
		public TextView des;
		public TextView title;
		public TextView content;

		public MyViewHolder(View view) {
			super(view);
			tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
			teamIcon = (ImageView) view.findViewById(R.id.teamIcon);
			des = (TextView) view.findViewById(R.id.des);
			title = (TextView) view.findViewById(R.id.title);
			content = (TextView) view.findViewById(R.id.content);
		}
	}


	public class EmptyViewHolder extends RecyclerView.ViewHolder{
		public EmptyViewHolder(View view) {
			super(view);

		}
	}


}
