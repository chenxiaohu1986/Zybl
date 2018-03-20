package com.ihandy.zybl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.ihandy.zybl.entity.ItemMatch;
import com.ihandy.zybl.entity.ItemZybl;
import com.ihandy.zybl.entity.Odds;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxh on 18/3/15.
 */

public class MatchAdapter extends BaseAdapter {

	Context context;

	private LayoutInflater mInflater;
	private List<ItemMatch> list;

	public MatchAdapter(Context context, List<ItemMatch> list) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return 0;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_match, null);


			holder.tvHead = (TextView) convertView.findViewById(R.id.tv_head);

			holder.home = (TextView) convertView.findViewById(R.id.home);
			holder.away = (TextView) convertView.findViewById(R.id.away);
			holder.copyBtn = (ImageView) convertView.findViewById(R.id.copy);


			holder.homeOdds = (TextView) convertView.findViewById(R.id.homeOdds);
			holder.drawOdds = (TextView) convertView.findViewById(R.id.drawOdds);
			holder.awayOdds = (TextView) convertView.findViewById(R.id.awayOdds);
			holder.lethomeOdds = (TextView) convertView.findViewById(R.id.lethomeOdds);
			holder.letdrawOdds = (TextView) convertView.findViewById(R.id.letdrawOdds);
			holder.letawayOdds = (TextView) convertView.findViewById(R.id.letawayOdds);
			holder.lvZybl = (ListViewForScrollView) convertView.findViewById(R.id.lv_zybl);
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}


		try {
			if (position == 4) {
				String me = "";
				Log.e("zybl", "position " + me);
			}
			ItemMatch itemMatch = list.get(position);
			if (itemMatch.getKeywords() == null) {
				holder.tvHead.setText(itemMatch.getNum());
			} else {
				holder.tvHead.setText(itemMatch.getNum() + "    " + itemMatch.getKeywords().split(",")[2]);
			}

			Odds[] odds = itemMatch.getOdds();
			if (odds != null) {
				for (Odds odd : odds) {
					String home = df.format(Float.valueOf(odd.getHome()));
					String draw = df.format(Float.valueOf(odd.getDraw()));
					String away = df.format(Float.valueOf(odd.getAway()));
					if ("JC_ZQ_SPF_GG".equals(odd.getPlayType())) {    //竞彩胜平负
						holder.homeOdds.setText(home);
						holder.drawOdds.setText(draw);
						holder.awayOdds.setText(away);
					} else if ("JC_ZQ_RQSPF_GG".equals(odd.getPlayType())) {
						holder.lethomeOdds.setText(home);
						holder.letdrawOdds.setText(draw);
						holder.letawayOdds.setText(away);
					}
				}
			}

			holder.home.setText(itemMatch.getHome());
			holder.away.setText(itemMatch.getAway());

			try {
				final List<ItemZybl> zyblList = itemMatch.getList();
				ZyblAdapter zyblAdapter = new ZyblAdapter(context, itemMatch);
				holder.lvZybl.setAdapter(zyblAdapter);

				holder.copyBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String content = getZyblContent(zyblList);
						copy(content, context);
						Toast.makeText(context, "复制成功...", Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {

			}
		} catch (Exception e) {
			Log.e("zybl", e.getMessage());
		}
		return convertView;
	}

	public final class ViewHolder {
		public TextView tvHead;

		public TextView home;
		public TextView away;
		public ImageView copyBtn;

		public TextView homeOdds;
		public TextView drawOdds;
		public TextView awayOdds;
		public TextView lethomeOdds;
		public TextView letdrawOdds;
		public TextView letawayOdds;


		public ListViewForScrollView lvZybl;
	}

	private String getZyblContent(List<ItemZybl> itemContents) {
		String content = "";
		if (itemContents != null && itemContents.size() > 0) {
			for (ItemZybl itemZybl : itemContents) {
				content += itemZybl.getContent();
			}
		}
		return content;
	}

	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("", content);
		cmb.setPrimaryClip(clip);
	}

	//保留两位小数
	DecimalFormat df = new DecimalFormat("0.00");

}
