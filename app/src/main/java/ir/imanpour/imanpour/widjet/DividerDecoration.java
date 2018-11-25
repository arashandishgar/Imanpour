package ir.imanpour.imanpour.widjet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class DividerDecoration extends RecyclerView.ItemDecoration {

  private final Paint mPaint;
  private final int color;
  private int mHeightDp;

  public DividerDecoration(Context context) {
    this(context, Color.argb((int) (255 * 0.2), 0, 0, 0), 1f);
  }

  public DividerDecoration(Context context, int color, float heightDp) {
    mPaint = new Paint();
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(color);
    this.color = color;
    mHeightDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, context.getResources().getDisplayMetrics());
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    int position = parent.getChildAdapterPosition(view);
    int viewType = parent.getAdapter().getItemViewType(position);
    outRect.set(0, 0, 0, mHeightDp);
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      View view = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(view);
      int viewType = parent.getAdapter().getItemViewType(position);
      mPaint.setColor(color);
      c.drawRect(view.getLeft(), view.getBottom(), view.getRight() - 300, view.getBottom() + mHeightDp, mPaint);
      mPaint.setColor(Color.TRANSPARENT);
      c.drawRect(view.getRight() - 300, view.getBottom(), view.getRight(), view.getBottom() + mHeightDp, mPaint);
    }
  }
}