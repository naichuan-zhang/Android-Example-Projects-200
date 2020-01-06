package com.example.qqmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class QQMenu extends HorizontalScrollView {

    private LinearLayout scrollView;
    private ViewGroup menu;     // 菜单区域
    private ViewGroup content;  // 主显示区域
    private int screenWidth;
    private int menuRightPadding = 50;
    private boolean call;   // 只设置一次横向滚动视图与子视图的宽度
    private int menuWidth;

    public QQMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取窗口管理器服务
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        // 获取当前屏幕的尺寸
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
        }
        // 屏幕宽度
        screenWidth = outMetrics.widthPixels;
        // 将50dp转换为像素
        menuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }

    // 设置滚动视图和子视图的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!call) {
            // 获取滚动视图
            scrollView = (LinearLayout) getChildAt(0);
            // 获取菜单区域
            menu = (ViewGroup) scrollView.getChildAt(0);
            // 获取主显示区域
            content = (ViewGroup) scrollView.getChildAt(1);
            // 设置菜单宽度
            menuWidth = menu.getLayoutParams().width = screenWidth - menuRightPadding;
            // 设置主显示区域宽度
            content.getLayoutParams().width = screenWidth;

            call = true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // 设置偏移量让菜单隐藏
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            this.scrollTo(menuWidth, 0);
        }
    }

    // 判断手指抬起时隐藏还是显示菜单
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX > menuWidth / 2) {
                    this.scrollTo(menuWidth, 0);
                } else {
                    this.scrollTo(0, 0);
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }
}
