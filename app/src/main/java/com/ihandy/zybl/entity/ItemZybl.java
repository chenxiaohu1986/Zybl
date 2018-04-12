package com.ihandy.zybl.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxh on 18/3/15.
 */

public class ItemZybl {


	private String zhuke; //主  客   ""   当没有章鱼爆料时为""
	private String des; //伤病 交锋  状态 赛程

	private String title;
	private String content;

	public String getZhuke() {
		return zhuke;
	}

	public void setZhuke(String zhuke) {
		this.zhuke = zhuke;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
