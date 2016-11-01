package com.xpple.im.view.dialog;

import java.lang.reflect.Field;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpple.im.R;
import com.xpple.im.view.dialog.effects.BaseEffects;

/**
 * Created by lee on 2014/7/30.
 */
public class NiftyDialogBuilder extends Dialog implements DialogInterface {

	private final String defTextColor = "#FFFFFFFF";

	private final String defDividerColor = "#11000000";

	private final String defMsgColor = "#FFFFFFFF";

	private final String defDialogColor = "#FFE74C3C";

	private Effectstype type = null;

	private LinearLayout mLinearLayoutView;

	private RelativeLayout mRelativeLayoutView;

	private LinearLayout mLinearLayoutMsgView;

	private LinearLayout mLinearLayoutTopView;

	private FrameLayout mFrameLayoutCustomView;

	private View mDialogView;

	private View mDivider;

	private TextView mTitle;

	private TextView mMessage;

	private Button mButton1;

	private Button mButton2;

	private int mDuration = -1;

	private static int mOrientation = 1;

	private boolean isCancelable = true;

	private volatile static NiftyDialogBuilder instance;

	public NiftyDialogBuilder(Context context) {
		super(context);
		init(context);

	}

	public NiftyDialogBuilder(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(
				params);
	}

	public static NiftyDialogBuilder getInstance(Context context) {

		int ort = context.getResources().getConfiguration().orientation;
		if (mOrientation != ort) {
			mOrientation = ort;
			instance = null;
		}
		instance = new NiftyDialogBuilder(context, R.style.dialog_untran);

		return instance;

	}

	private void init(Context context) {
		mDialogView = View.inflate(context, R.layout.dialog_layout, null);
		mLinearLayoutView = (LinearLayout) mDialogView
				.findViewById(R.id.parentPanel);// null
		mLinearLayoutView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 源码点击Dialog本身空白处也自行消失，此处我做了处理，取消了这个问题，不行的朋友注解keepDialog和closeDialog有关的代码即可
				// TODO Auto-generated method stub
				keepDialog(NiftyDialogBuilder.this);
			}
		});
		mRelativeLayoutView = (RelativeLayout) mDialogView
				.findViewById(R.id.main);// null
		mLinearLayoutTopView = (LinearLayout) mDialogView
				.findViewById(R.id.topPanel);// null
		mLinearLayoutMsgView = (LinearLayout) mDialogView
				.findViewById(R.id.contentPanel);// null
		mFrameLayoutCustomView = (FrameLayout) mDialogView
				.findViewById(R.id.customPanel);// null
		mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);// null
		mMessage = (TextView) mDialogView.findViewById(R.id.message);// null
		mDivider = mDialogView.findViewById(R.id.titleDivider);
		mButton1 = (Button) mDialogView.findViewById(R.id.button1);
		mButton2 = (Button) mDialogView.findViewById(R.id.button2);
		setContentView(mDialogView);

		this.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {

				mLinearLayoutView.setVisibility(View.VISIBLE);
				type = Effectstype.Shake;// 启动动画
				start(type);
			}
		});
		mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isCancelable) {
					getDismiss();
				}
			}
		});
	}

	public void toDefault() {
		mTitle.setTextColor(Color.parseColor(defTextColor));
		mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
		mMessage.setTextColor(Color.parseColor(defMsgColor));
		mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
	}

	public NiftyDialogBuilder withDividerColor(String colorString) {
		mDivider.setBackgroundColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyDialogBuilder withTitle(CharSequence title) {
		toggleView(mLinearLayoutTopView, title);
		mTitle.setText(title);
		return this;
	}

	public NiftyDialogBuilder withTitleColor(String colorString) {
		mTitle.setTextColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyDialogBuilder withMessage(int textResId) {
		toggleView(mLinearLayoutMsgView, textResId);
		mMessage.setText(textResId);
		return this;
	}

	public NiftyDialogBuilder withMessage(CharSequence msg) {
		toggleView(mLinearLayoutMsgView, msg);
		mMessage.setText(msg);
		return this;
	}

	public NiftyDialogBuilder withMessageColor(String colorString) {
		mMessage.setTextColor(Color.parseColor(colorString));
		return this;
	}

	public NiftyDialogBuilder withDuration(int duration) {
		this.mDuration = duration;
		return this;
	}

	public NiftyDialogBuilder withEffect(Effectstype type) {
		this.type = type;
		return this;
	}

	public NiftyDialogBuilder withButtonDrawable(int resid) {
		mButton1.setBackgroundResource(resid);
		mButton2.setBackgroundResource(resid);
		return this;
	}

	public NiftyDialogBuilder withButton1Text(CharSequence text) {
		mButton1.setVisibility(View.VISIBLE);
		mButton1.setText(text);

		return this;
	}

	public NiftyDialogBuilder withButton2Text(CharSequence text) {
		mButton2.setVisibility(View.VISIBLE);
		mButton2.setText(text);
		return this;
	}

	public NiftyDialogBuilder setButton1Click(View.OnClickListener click) {
		mButton1.setOnClickListener(click);
		return this;
	}

	public NiftyDialogBuilder setButton2Click(View.OnClickListener click) {
		mButton2.setOnClickListener(click);
		return this;
	}

	public NiftyDialogBuilder setCustomView(int resId, Context context) {
		if (resId == 0) {
			return this;
		}
		View customView = View.inflate(context, resId, null);
		if (mFrameLayoutCustomView.getChildCount() > 0) {
			mFrameLayoutCustomView.removeAllViews();
		}
		mFrameLayoutCustomView.addView(customView);
		return this;
	}

	public NiftyDialogBuilder setCustomView(View view, Context context) {
		if (view == null)
			return this;
		if (mFrameLayoutCustomView.getChildCount() > 0) {
			mFrameLayoutCustomView.removeAllViews();
		}
		mFrameLayoutCustomView.addView(view);

		return this;
	}

	public NiftyDialogBuilder isCancelableOnTouchOutside(boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCanceledOnTouchOutside(cancelable);
		return this;
	}

	public NiftyDialogBuilder isCancelable(boolean cancelable) {
		this.isCancelable = cancelable;
		this.setCancelable(cancelable);
		return this;
	}

	private void toggleView(View view, Object obj) {
		if (obj == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void show() {

		super.show();
	}

	private void start(Effectstype type) {
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mRelativeLayoutView);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mLinearLayoutView.setVisibility(View.GONE);
		mButton1.setVisibility(View.GONE);
		mButton2.setVisibility(View.GONE);

	}

	/*
	 * //结束动画 布局有点bug Title部分和主体分离，
	 * 时间比较紧张，只能写一个代码去弥补，有能力的朋友可以自行修复，简化代码。最好通知我让我也修复啊，谢谢了
	 */
	public void getDismiss() {

		type = Effectstype.DialogCancel;
		BaseEffects animator = type.getAnimator();
		if (mDuration != -1) {
			animator.setDuration(Math.abs(mDuration));
		}
		animator.start(mRelativeLayoutView);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dismiss();
				closeDialog(NiftyDialogBuilder.this);
			}
		}, 500);
	}

	public void keepDialog(DialogInterface mDialog) {
		try {
			Field field = mDialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(mDialog, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeDialog(DialogInterface mDialog) {
		try {
			Field field = mDialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(mDialog, true);
			mDialog.dismiss();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
