package com.ihandy.zybl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cxh on 18/3/15.
 */

public class MatchAdapter extends BaseAdapter {

	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyyMMdd");

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


			holder.home.setText(itemMatch.getHome());
			holder.away.setText(itemMatch.getAway());

			setOdd(itemMatch.getOddUrl(),holder);

			setZybl(itemMatch,holder);


		} catch (Exception e) {
			Log.e("zybl", e.getMessage());
		}
		return convertView;
	}

	Gson gson = new Gson();
	OkHttpClient client = new OkHttpClient();

	private void setZybl(final ItemMatch itemMatch,final ViewHolder holder) {
		final String zyblUrl = itemMatch.getZyblUrl();
		if (StringUtils.isEmpty(zyblUrl)){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				//爆料内容
				Connection conn = Jsoup.connect(zyblUrl);
				conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
				Document doc = null;
				try {
					doc = conn.get();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String keywords = doc.select("meta[name=keywords]").first().attr("content");
				String ctime = doc.getElementById("ctime").text();
				String homeIcon = doc.getElementById("homeIcon").attr("src");
				String awayIcon = doc.getElementById("awayIcon").attr("src");

				Element zyblContent = doc.getElementById("zyblContent");   //章鱼爆料内容
				final List list = new ArrayList();
				Elements zyblElements = zyblContent.children();
				for (int i=0 ; i < zyblElements.size()-1 ; i++) {   //最后一行是js脚本
					Element item = zyblElements.get(i);
					String title = item.child(0).text();
					String zhuke = item.child(1).child(0).text();
					String des = item.child(1).text();
					String content = item.child(2).text();
					ItemZybl itemZybl = new ItemZybl();
					itemZybl.setTitle(title);
					itemZybl.setZhuke(zhuke);
					itemZybl.setDes(des);
					itemZybl.setContent(content);
					list.add(itemZybl);
				}

				itemMatch.setKeywords(keywords);
				itemMatch.setCtime(ctime);
				itemMatch.setHomeIcon(homeIcon);
				itemMatch.setAwayIcon(awayIcon);
				itemMatch.setList(list);

				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ZyblAdapter zyblAdapter = new ZyblAdapter(context, itemMatch);
						holder.lvZybl.setAdapter(zyblAdapter);

						holder.copyBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String content = getZyblContent(list);
								copy(content, context);
								Toast.makeText(context, "复制成功...", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});
			}
		}).start();
	}

	private String getMatchWeek(){
		Date match = TimeUtils.string2Date(((MainActivity)context).matchDate,DEFAULT_DATE_FORMAT);
		String date = TimeUtils.date2String(match,DATE_FORMAT_DATE);
		return date + getWeek(match);
	}

	private  String getWeek(Date date){
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay)){       //周天
			mWay ="7";
		}else if("2".equals(mWay)){
			mWay ="1";
		}else if("3".equals(mWay)){
			mWay ="2";
		}else if("4".equals(mWay)){
			mWay ="3";
		}else if("5".equals(mWay)){
			mWay ="4";
		}else if("6".equals(mWay)){
			mWay ="5";
		}else if("7".equals(mWay)){
			mWay ="6";
		}
		return mWay;
	}

	private void setOdd(String oddUrl,final ViewHolder holder){
//		String oddUrl = "https://www.8win.com/jsbf/zq/odd";
//		RequestBody body = new FormBody.Builder()
//				.add("matchTime",matchTime)
//				.add("lotteryCode","JC_ZQ")
//				.add("flag","1")
//				.build();
		Request request = new Request.Builder()
				.url(oddUrl)
//				.post(body)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.i("zybl",e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
			    String jsonData = response.body().string();
				Log.i("zybl",jsonData);
				Odds[] odds = gson.fromJson(jsonData, Odds[].class);
				if (odds != null) {
					for (final Odds odd : odds) {
						final String home = df.format(Float.valueOf(odd.getHome()));
						final String draw = df.format(Float.valueOf(odd.getDraw()));
						final String away = df.format(Float.valueOf(odd.getAway()));


						((Activity)context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
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
						});

					}
				}
			}
		});
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
			for (int j=0 ; j < itemContents.size() ; j++) {
				content += (j + 1) + "."+ itemContents.get(j).getContent();
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
