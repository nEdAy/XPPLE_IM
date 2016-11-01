package com.xpple.im.view.dialog;

import com.xpple.im.view.dialog.effects.BaseEffects;
import com.xpple.im.view.dialog.effects.DialogCancel;
import com.xpple.im.view.dialog.effects.FadeIn;
import com.xpple.im.view.dialog.effects.Fall;
import com.xpple.im.view.dialog.effects.FlipH;
import com.xpple.im.view.dialog.effects.FlipV;
import com.xpple.im.view.dialog.effects.NewsPaper;
import com.xpple.im.view.dialog.effects.RotateBottom;
import com.xpple.im.view.dialog.effects.RotateLeft;
import com.xpple.im.view.dialog.effects.Shake;
import com.xpple.im.view.dialog.effects.SideFall;
import com.xpple.im.view.dialog.effects.SlideBottom;
import com.xpple.im.view.dialog.effects.SlideLeft;
import com.xpple.im.view.dialog.effects.SlideRight;
import com.xpple.im.view.dialog.effects.SlideTop;
import com.xpple.im.view.dialog.effects.Slit;

/**
 * Created by lee on 2014/7/30.
 */
public enum Effectstype {
	DialogCancel(DialogCancel.class), Fadein(FadeIn.class), Slideleft(
			SlideLeft.class), Slidetop(SlideTop.class), SlideBottom(
			SlideBottom.class), Slideright(SlideRight.class), Fall(Fall.class), Newspager(
			NewsPaper.class), Fliph(FlipH.class), Flipv(FlipV.class), RotateBottom(
			RotateBottom.class), RotateLeft(RotateLeft.class), Slit(Slit.class), Shake(
			Shake.class), Sidefill(SideFall.class);
	private Class<? extends BaseEffects> effectsClazz;

	private Effectstype(Class<? extends BaseEffects> mclass) {
		effectsClazz = mclass;
	}

	public BaseEffects getAnimator() {
		BaseEffects bEffects = null;
		try {
			bEffects = effectsClazz.newInstance();
		} catch (ClassCastException e) {
			throw new Error("Can not init animatorClazz instance");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new Error("Can not init animatorClazz instance");
		}
		return bEffects;
	}
}
