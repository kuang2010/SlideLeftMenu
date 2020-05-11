package com.kuang2010.slidemenuview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * author: kuangzeyu2019
 * date: 2020/5/11
 * time: 21:23
 * desc: 自定义ViewGroup
 */
public class SlideMenuView extends ViewGroup {

    private View mChild_main_content;
    private View mChild_left_menu;
    private int mMain_width;
    private int mMain_height;
    private int mLeft_width;
    private int mLeft_height;
    private float mDownX;
    private float mDownY;
    private Scroller mScroller;//屏幕scrollTo的动画类
    private long mDownMillis;
    private int mScaledWindowTouchSlop;
    private boolean isOpenLeftMenu = false;

    public SlideMenuView(Context context) {
        super(context);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaledWindowTouchSlop = viewConfiguration.getScaledWindowTouchSlop();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChild_main_content = getChildAt(0);
        mChild_left_menu = getChildAt(1);
        mMain_width = mChild_main_content.getLayoutParams().width;//-1
        mMain_height = mChild_main_content.getLayoutParams().height;//-1
        mLeft_width = mChild_left_menu.getLayoutParams().width;//xxx
        mLeft_height = mChild_left_menu.getLayoutParams().height;//-1
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mChild_main_content.measure(widthMeasureSpec, heightMeasureSpec);

        mChild_left_menu.measure(MeasureSpec.makeMeasureSpec(mLeft_width, MeasureSpec.EXACTLY), heightMeasureSpec);

        //setMeasuredDimension(mChild_main_content.getMeasuredWidth(),mChild_main_content.getMeasuredHeight());
        //or
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        //or
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int test_dx = 0;//mChild_left_menu.getMeasuredWidth();
        //main_content的初始布局位置
        int left_main = 0 + test_dx;
        int top_main = 0;
        int right_main = mChild_main_content.getMeasuredWidth() + test_dx;
        int bottom_main = mChild_main_content.getMeasuredHeight();
        mChild_main_content.layout(left_main, top_main, right_main, bottom_main);

        //left_menu的初始布局位置
        int left_menu = -mChild_left_menu.getMeasuredWidth() + test_dx;
        int top_menu = 0;
        int right_menu = 0 + test_dx;
        int bottom_menu = mChild_left_menu.getMeasuredHeight();
        mChild_left_menu.layout(left_menu, top_menu, right_menu, bottom_menu);

    }


    //事件的处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                mDownX = event.getX();
                mDownY = event.getY();

                break;

            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();

                int dx = Math.round(moveX - mDownX);

                scrollBy(-dx, 0);//移动屏幕，向touch相反的反向移动

                //边界判断
                int scrollX = getScrollX();//屏幕当前的x位置
                if (scrollX < -mChild_left_menu.getMeasuredWidth()) {
                    scrollTo(-mChild_left_menu.getMeasuredWidth(), 0);
                } else if (scrollX > 0) {
                    scrollTo(0, 0);
                }

                mDownX = moveX;

                break;

            case MotionEvent.ACTION_UP:
                //松手使其处于边界状态
                int screenX = getScrollX();//屏幕当前的x位置
                if (screenX > -mChild_left_menu.getMeasuredWidth() / 2) {
//                    scrollTo(0,0);
                    closeLeftMenu();
                } else {
//                    scrollTo(-mChild_left_menu.getMeasuredWidth(),0);
                    openLeftMenu();
                }
                break;

        }

        return true;//消费掉事件，不回传给父控件了
    }

    //打开左侧菜单+动画
    public void openLeftMenu() {
        int screenX = getScrollX();//屏幕当前的x位置
        //x从 屏幕的位置 滑动到 -mChild_left_menu.getMeasuredWidth()这个位置
        mScroller.startScroll(screenX, 0, -mChild_left_menu.getMeasuredWidth() - screenX, 0, 500);
        invalidate();
        isOpenLeftMenu = true;
    }

    //关闭左侧菜单+动画
    public void closeLeftMenu() {
        int screenX = getScrollX();//屏幕当前的x位置
        //x从 屏幕的位置 滑动到 0这个位置
        mScroller.startScroll(screenX, 0, 0 - screenX, 0, 500);
        invalidate();
        isOpenLeftMenu = false;
    }

    public boolean getLeftMenuIsOpen(){
        return isOpenLeftMenu;
    }

    //需要覆盖此方法mScroller.startScroll才能滑动，动画本质上时不停的掉scrollTo(currX,0)，invalidate();
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int currX = mScroller.getCurrX();
            scrollTo(currX, 0);
            invalidate();
        }
        /*if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }

            // Keep on drawing until the animation has finished.
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }*/
    }


    //由于左侧菜单的scrollView消费掉了上下和左右划的事件（不回传给父控件），所以要在该父控件里拦截左右划的事件，以便自己处理onTouchEvent
    //return true：事件拦截不传递，return false和super：事件传递不拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();
                float dx = moveX - mDownX;
                float dy = moveY - mDownY;

                if (Math.abs(dx)>Math.abs(dy)){
                    //x方向划
                    return true;//拦截不传递
                }

                break;
            case MotionEvent.ACTION_UP:
                break;

        }

        return super.onInterceptTouchEvent(ev);
    }


    //点击关闭左侧菜单
    //return true和false事件不分发下去（终止事件），后面的两个方法及子控件的三个方法都执行不了。return super.事件会分发下去
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mDownMillis = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                float upX = ev.getX();
                float upY = ev.getY();
                long upMillis = System.currentTimeMillis();
                if (Math.abs(mDownX-upX)<mScaledWindowTouchSlop && Math.abs(mDownY-upY) < mScaledWindowTouchSlop){
                    // 认为点的位置不变
                    long l = upMillis - mDownMillis;
                    Log.d("dispatchTouchEvent","l:"+l);
                    if (l < 500){
                        //点的时间很短
                        //到此是点击事件了
                        /*if (isOpenLeftMenu){
                            closeLeftMenu();
                            //return true;//如果事件继续向下传到onTouchEvent方法，它又会调用openLeftMenu()，所以返回true终止掉事件,但这样又会导致子控件的点击事件执行不了，所以不能终止！
                        }*/
                    }
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
