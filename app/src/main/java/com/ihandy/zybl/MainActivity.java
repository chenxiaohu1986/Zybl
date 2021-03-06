package com.ihandy.zybl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends Activity {

	private ImageView time;
	private TextView tvTime;

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView ;
	private List<ItemMatch> list = new ArrayList<>();
	private String baseUrl = "http://cms.8win.com";

//	http://cms.8win.com/zybl/addPrizeOdd/201803191001
	private String baseOddUrl = "http://cms.8win.com/zybl/addPrizeOdd/";

//	http://cms.8win.com/zybl?label=history&tipsLabel=&matchDate=2018-03-19
	String zyblUrl = "http://cms.8win.com/zybl?label=history&tipsLabel=&matchDate=";

	public static String matchDate = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd"));   //比赛日期  默认是当天

	private int year;
	private int month;
	private int day;


	private TextView searchNum;
	private TextView search;


	private MatchAdapter matchAdapter;


	private static Map<String,ItemMatch> map  = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Calendar now = Calendar.getInstance();
		year =  now.get(Calendar.YEAR);
		month = now.get(Calendar.MONTH) + 1;
		day = now.get(Calendar.DAY_OF_MONTH);
		time =  findViewById(R.id.time);
		tvTime = findViewById(R.id.tvTime);
		tvTime.setText(TimeUtils.getNowString(DEFAULT_DATE_FORMAT));  //当前时间
		searchNum = findViewById(R.id.searchNum);
		search = findViewById(R.id.search);

		swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
		recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));


		loadData();
		time.setOnClickListener(listener);
		tvTime.setOnClickListener(listener);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadData();
			}
		});

		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(searchNum.getText())){
					loadData();
				} else {
					String num = searchNum.getText().toString();
					loadItemData(num);
				}
			}
		});


	}

	private void loadItemData(String num) {
		ItemMatch itemMatch = map.get(num);
		itemMatch.setNum(num);

		String matchWeek = getMatchWeek();
		String matchId = matchWeek + num;
		itemMatch.setMatchId(matchId);
		itemMatch.setOddUrl(baseOddUrl + matchId);
		String zyblUrl = baseUrl + "/zybl/" + matchId;
		itemMatch.setZyblUrl(zyblUrl);

		list.clear();
		list.add(itemMatch);
		// 执行完毕后给handler发送一个空消息
		handler.sendEmptyMessage(0);

	}

	// 对话框下的DatePicker
	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
			dialog.show();
			DatePicker picker = new DatePicker(MainActivity.this);

			picker.setDate(year, month);
			picker.setMode(DPMode.SINGLE);
			picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
				@Override
				public void onDatePicked(String date) {
					tvTime.setText(date);
					dialog.dismiss();
					matchDate = date;
					loadData();
				}
			});
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			dialog.getWindow().setContentView(picker, params);
			dialog.getWindow().setGravity(Gravity.CENTER);
		}
	};


	Runnable runnable = new Runnable() {
		@Override
		public void run() {

			try {
				String url = zyblUrl + matchDate;
				Connection conn = Jsoup.connect(url);
				conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
				Document doc = null;
				try {
					doc = conn.get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Element newMatchList = doc.getElementById("newMatchList");
				Elements matchItem = newMatchList.child(0).children();
				String matchWeek = getMatchWeek();
				for (int i = 1 ; i < matchItem.size() ; i++) {
					Element item = matchItem.get(i);
					ItemMatch itemMatch = new ItemMatch();
					String num = "";
					String home = "";
					String away = "";
					if ( "a".equals(item.tagName())) {    //a href
						home = item.child(0).child(1).text();
						away = item.child(0).child(3).text();
						num = item.child(0).child(0).text();
					} else {
						home = item.child(1).text();
						away = item.child(3).text();
						num = item.child(0).text();
					}
					itemMatch.setHome(home);
					itemMatch.setAway(away);
					itemMatch.setNum(num);
					String matchId = matchWeek + num;
					itemMatch.setMatchId(matchId);
					itemMatch.setOddUrl(baseOddUrl + matchId);
					String zyblUrl = baseUrl + "/zybl/" + matchId;
					itemMatch.setZyblUrl(zyblUrl);
					list.add(itemMatch);
					map.put(num,itemMatch);
				}
			}catch (Exception e){
				Log.e("zybl",e.getMessage());
			}

			// 执行完毕后给handler发送一个空消息
			handler.sendEmptyMessage(0);

		}
	};


	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 收到消息后执行handler
			show();
		}
	};




	// 将数据填充到ListView中
	private void show() {
		if(list.isEmpty()) {
			Toast.makeText(this,"暂无数据...",Toast.LENGTH_LONG).show();
		} else {
			matchAdapter = new MatchAdapter(this,list);
			recyclerView.setAdapter(matchAdapter);
		}
		swipeRefreshLayout.setRefreshing(false);
	}


	private void loadData(){
		if (NetworkUtils.isConnected()){
			list.clear();
			map.clear();
			// 显示“正在加载”窗口
			swipeRefreshLayout.setRefreshing(true);
			if (matchAdapter != null){
				matchAdapter.notifyDataSetChanged();
			}
			new Thread(runnable).start();  // 子线程

		}else{
			// 弹出提示框
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("当前没有网络连接！")
					.setPositiveButton("重试",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							loadData();
						}
					}).setNegativeButton("退出",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);  // 退出程序
				}
			}).show();
		}
	}




	private String getMatchWeek(){
		Date match = TimeUtils.string2Date(matchDate,DEFAULT_DATE_FORMAT);
		String date = TimeUtils.date2String(match,DATE_FORMAT_DATE);
		return date + getWeek(match);
	}

	private String getMatchId(String num){
		Date match = TimeUtils.string2Date(matchDate,DEFAULT_DATE_FORMAT);
		String date = TimeUtils.date2String(match,DATE_FORMAT_DATE);
		return date + getWeek(match) + num;
	}


	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyyMMdd");

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


}
