package com.example.satellitemenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.satellitemenu.R;

public class SatelliteMenu extends ViewGroup {

    private int screenWidth;
    private int screenHeight;

    private int radius;

    // 主按钮（正中心的按钮）
    private View button;

    public enum Status { OPEN, CLOSED }
    private Status currentStatus = Status.CLOSED;

    private OnSatelliteMenuItemClickListener mMenuItemClickListener;

    public interface OnSatelliteMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public SatelliteMenu(Context context) {
        this(context,null);
    }

    public SatelliteMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取窗口管理器服务
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 创建显示尺寸的对象
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
        }
        // 获取屏幕的宽和高
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

//        screenHeight = windowManager.getDefaultDisplay().getHeight();
//        screenWidth = windowManager.getDefaultDisplay().getWidth();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SatelliteMenu, defStyleAttr, 0);
        radius = (int) a.getDimension(R.styleable.SatelliteMenu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics()));

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 测量所有按钮的宽度和高度
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            buttonLayout();
            childLayout();
        }
    }

    // 获取主按钮的位置（正中心的按钮）
    private void buttonLayout() {
        button = getChildAt(0);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateButton(v, 0f, 360f, 500);     // 主按钮旋转
                shrinkMenu(500);       // 收缩子按钮动画
            }
        });

        int l = 0;  // 左边距
        int t = 0;  // 上边距
        int width = button.getMeasuredWidth();  // 主按钮宽度
        int height = button.getMeasuredHeight();    // 主按钮高度

        l = screenWidth / 2 - width / 2;
        t = getMeasuredHeight() - height;

        // 设置主按钮的位置
        button.layout(l, t, l + width, t + height);
    }

    // 获取其余子按钮的位置（圆周上的按钮）
    private void childLayout() {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            // 遍历子按钮
            View child = getChildAt(i + 1);
            // 隐藏子按钮
            child.setVisibility(View.GONE);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int centerX = screenWidth / 2 - childWidth / 2;
            int centerY = getMeasuredHeight() - childHeight / 2;

            int l = (int) (radius * Math.sin(Math.PI / (count - 2) * i));
            int t = (int) (radius * Math.sin(Math.PI / (count - 2) * i));

            t = getMeasuredHeight() - childHeight - t;
            if (i == 0) {
                l = centerX - radius;
                t = getMeasuredHeight() - childHeight;
            } else if (i == 1) {
                l = centerX - l;
            } else if (i == 2) {
                l = centerX;
                t = getMeasuredHeight() - childHeight - radius;
            } else if (i == 3) {
                l = centerX + l;
            } else if (i == 4) {
                l = centerX + radius;
                t = getMeasuredHeight() - childHeight;
            }

            child.layout(l, t, l + childWidth, t + childHeight);
        }
    }

    private void rotateButton(View v, float start, float end, int time) {
        RotateAnimation rotateAnimation = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(time);
        rotateAnimation.setFillAfter(true);     // 停止最后一帧的位置
        v.startAnimation(rotateAnimation);
    }

    private void shrinkMenu(int time) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            // 动画开始和结束的位置
            int clx = (int) (radius * Math.sin(Math.PI / (count - 2) * i));
            int cty = (int) (radius * Math.cos(Math.PI  / (count - 2) * i));

            if (i == 0) {
                clx = radius;
                cty = 0;
            } else if (i == 1) {

            } else if (i == 2) {
                clx = 0;
                cty = radius;
            } else if (i == 3) {
                clx = (int) (radius * Math.cos(Math.PI / (count - 2) * i));
                cty = -cty;
            } else if (i == 4) {
                clx = -radius;
                cty = 0;
            }

            AnimationSet animationSet = new AnimationSet(true);
            Animation animation = null;

            if (currentStatus == Status.CLOSED) {
                animation = new TranslateAnimation(clx, 0, cty, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {
                animation = new TranslateAnimation(0, clx, 0, cty);
                childView.setClickable(false);
                childView.setFocusable(false);
            }

            animation.setDuration(time);
            animation.setFillAfter(true);
            animation.setStartOffset((i * 100) / count);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (currentStatus == Status.CLOSED) {
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(time);       //设置旋转的时间
            rotateAnim.setFillAfter(true);      //停止最后一帧的位置
            animationSet.addAnimation(rotateAnim);   //添加旋转动画
            animationSet.addAnimation(animation);     //添加平移动画
            childView.startAnimation(animationSet);  //启动动画
            final int pos = i + 1;
            //子按钮单击事件
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null)
                        mMenuItemClickListener.onClick(childView, pos);
                    childButtonClickAnim(pos - 1);      //调用字按钮单击动画方法
                    menuStatus();                       //调用菜单状态方法
                }
            });
        }

        menuStatus();
    }

    private void childButtonClickAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(200));
            } else {
                childView.startAnimation(scaleSmallAnim(200));
            }

            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    private Animation scaleSmallAnim(int time) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(time);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private Animation scaleBigAnim(int time) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(time);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private void menuStatus() {
        currentStatus = (currentStatus == Status.CLOSED ?
                Status.OPEN : Status.CLOSED);
    }
}
