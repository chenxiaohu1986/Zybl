package com.ihandy.zybl.entity;

import java.util.ArrayList;
import java.util.List;

public class ItemMatch {

	private String keywords;
	private String ctime;

	private String num;      //jc编号  001
	private String matchId;  //jc比赛id  201803191005

	private String home;  //主队
	private String away;  //客队


	private String homeIcon;
	private String awayIcon;

	private String zyblUrl;
	private String oddUrl;


	private List<ItemZybl> list = new ArrayList<>();
//	private Odds[] odds;


	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getAway() {
		return away;
	}

	public void setAway(String away) {
		this.away = away;
	}

	public String getHomeIcon() {
		return homeIcon;
	}

	public void setHomeIcon(String homeIcon) {
		this.homeIcon = homeIcon;
	}

	public String getAwayIcon() {
		return awayIcon;
	}

	public void setAwayIcon(String awayIcon) {
		this.awayIcon = awayIcon;
	}

	public String getZyblUrl() {
		return zyblUrl;
	}

	public void setZyblUrl(String zyblUrl) {
		this.zyblUrl = zyblUrl;
	}

	public String getOddUrl() {
		return oddUrl;
	}

	public void setOddUrl(String oddUrl) {
		this.oddUrl = oddUrl;
	}

	public List<ItemZybl> getList() {
		return list;
	}

	public void setList(List<ItemZybl> list) {
		this.list = list;
	}

//	public Odds[] getOdds() {
//		return odds;
//	}
//
//	public void setOdds(Odds[] odds) {
//		this.odds = odds;
//	}
}
