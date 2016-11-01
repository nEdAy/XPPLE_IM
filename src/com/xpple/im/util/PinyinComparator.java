package com.xpple.im.util;

import java.util.Comparator;

import com.xpple.im.bean.User;

public class PinyinComparator implements Comparator<User> {

	@Override
	public int compare(User o1, User o2) {
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
