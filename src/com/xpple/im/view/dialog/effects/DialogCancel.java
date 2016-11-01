package com.xpple.im.view.dialog.effects;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

public class DialogCancel extends BaseEffects {

	@Override
	protected void setupAnimation(View view) {
		// TODO Auto-generated method stub
		getAnimatorSet().playTogether(
				ObjectAnimator.ofFloat(view, "rotation", 720, 1080, 0, 360)
						.setDuration(mDuration),// 旋转
				ObjectAnimator.ofFloat(view, "alpha", 1, 0).setDuration(
						mDuration * 3 / 2),// 渐变
				ObjectAnimator.ofFloat(view, "scaleX", 1, 0.5f, 0).setDuration(
						mDuration),
				ObjectAnimator.ofFloat(view, "scaleY", 1, 0.5f, 0).setDuration(
						mDuration)

		);
	}

}
