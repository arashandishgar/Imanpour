package ir.imanpour.imanpour.widjet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerCustom  extends ViewPager {
  public ViewPagerCustom(Context context) {
    super(context);
  }
  public ViewPagerCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  private boolean canSwipe=true;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if(!canSwipe){
      return false;
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if(!canSwipe){
      return false;
    }
    return super.onInterceptTouchEvent(ev);
  }
  public void setCanSwipe(boolean canSwipe){
    this.canSwipe=canSwipe;
  }
}
