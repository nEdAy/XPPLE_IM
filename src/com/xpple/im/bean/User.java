package com.xpple.im.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
 * 
 * @ClassName: TextUser
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * //显示数据拼音的首字母
	 */
	private String sortLetters;

	/**
	 * //性别-true-男
	 */
	private boolean sex;

	/**
	 * 地理坐标
	 */
	private BmobGeoPoint location;//
	private int gameA;

	public BmobGeoPoint getLocation() {
		return location;
	}

	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public int getGameA() {
		return gameA;
	}

	public void setGameA(int gameA) {
		this.gameA = gameA;
	}

}
