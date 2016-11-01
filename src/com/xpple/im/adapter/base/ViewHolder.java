package com.xpple.im.adapter.base;

import android.util.SparseArray;
import android.view.View;

/**
 * Viewholder的简化
 * 
 * @ClassName: ViewHolder
 * @Description: TODO
 * @author nEdAy
 * @date 2015-4-17 下午5:05:00
 */
@SuppressWarnings("unchecked")
public class ViewHolder {
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
