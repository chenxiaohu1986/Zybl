package com.ihandy.zybl;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by cxh on 18/3/19.
 */

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.init(this);
	}
}
