package com.example.daysmatter.ui.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static java.lang.Math.abs;

import com.example.daysmatter.R;
import com.example.daysmatter.logic.room.Message;

public class ViewLayout extends ViewGroup {

    public ViewLayout(Context context) {
        super(context);
    }

    public ViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //支持子 View 设置 margin（外边距）如果没有重写这个方法，子 View 设置的 layout_margin 将会失效。
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int leftWidth = 0;
        int leftHeight = 0;
        int rightWidth = 0;
        int rightHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (i == 0 || i == 2) {
                leftWidth = Math.max(leftWidth, childWidth);
                leftHeight += childHeight;
            } else if (i == 1 || i == 3) {
                rightWidth += childWidth;
                rightHeight = Math.max(rightHeight, childHeight);
            }
        }

        int totalWidth = leftWidth + rightWidth + getPaddingLeft() + getPaddingRight();
        int totalHeight = Math.max(leftHeight, rightHeight) + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(
                resolveSize(totalWidth, widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int leftStart = getPaddingLeft();
        int topStart = getPaddingTop();


        int currentTop = topStart;
        int[] leftIndices = {0, 2};
        for (int i : leftIndices) {
            if (i >= getChildCount()) continue;
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int cl = leftStart + lp.leftMargin;
            int ct = currentTop + lp.topMargin;
            child.layout(cl, ct, cl + child.getMeasuredWidth(), ct + child.getMeasuredHeight());
            currentTop = ct + child.getMeasuredHeight() + lp.bottomMargin;
        }

        // ===== 右边横排（index 1 和 3）=====
        int leftMaxWidth = Math.max(getChildMeasuredWidthSafe(0), getChildMeasuredWidthSafe(2));
        int contentHeight = b - t - getPaddingTop() - getPaddingBottom();
        int rightTotalHeight = Math.max(getChildMeasuredHeightSafe(1), getChildMeasuredHeightSafe(3));
        int rightStartTop = topStart + (contentHeight - rightTotalHeight) / 2;
        int rightStartLeft = leftStart + leftMaxWidth + dpToPx(16);

        int currentLeft = rightStartLeft;
        int[] rightIndices = {1, 3};
        for (int i : rightIndices) {
            if (i >= getChildCount()) continue;
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int cl = currentLeft + lp.leftMargin;
            int ct = rightStartTop + lp.topMargin;
            child.layout(cl, ct, cl + child.getMeasuredWidth(), ct + child.getMeasuredHeight());
            currentLeft = cl + child.getMeasuredWidth() + lp.rightMargin;
        }
    }

    private int getChildMeasuredWidthSafe(int index) {
        if (index >= getChildCount()) return 0;
        View child = getChildAt(index);
        if (!(child.getLayoutParams() instanceof MarginLayoutParams)) return 0;
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    private int getChildMeasuredHeightSafe(int index) {
        if (index >= getChildCount()) return 0;
        View child = getChildAt(index);
        if (!(child.getLayoutParams() instanceof MarginLayoutParams)) return 0;
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    public interface OnMessageClickListener {
        void onClick(Message msg);
    }

    public void setPinnedMessages(List<Message> messages, OnMessageClickListener listener) {
        removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Message msg : messages) {
            View itemView = inflater.inflate(R.layout.top_item, this, false);
            @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate today = LocalDate.now();
            @SuppressLint({"NewApi", "LocalSuppress"}) long daysBetween = ChronoUnit.DAYS.between(today, LocalDate.parse(msg.getAimdate()));

            TextView title = itemView.findViewById(R.id.topTitle);
            TextView time = itemView.findViewById(R.id.topTime);
            TextView aimDate = itemView.findViewById(R.id.topAimdate);
            TextView day = itemView.findViewById(R.id.topDay);

            if (title != null && time != null && aimDate != null && day != null) {
                if (daysBetween == 0) {
                    title.setText(msg.getTitle() + "就是今天");
                    time.setText(String.valueOf(daysBetween));
                    aimDate.setText("目标日:" + msg.getAimdate());
                    day.setText("Day");
                } else if (daysBetween > 0) {
                    title.setText(msg.getTitle() + "还有");
                    time.setText(String.valueOf(daysBetween));
                    aimDate.setText("目标日：" + msg.getAimdate());
                    day.setText(daysBetween > 1 ? "Days" : "Day");
                } else {
                    title.setText(msg.getTitle() + "已经");
                    time.setText(String.valueOf(abs(daysBetween)));
                    aimDate.setText("起始日：" + msg.getAimdate());
                    day.setText(abs(daysBetween) > 1 ? "Days" : "Day");
                }

                itemView.setOnClickListener(v -> listener.onClick(msg));
                addView(itemView);
            }
        }
    }
}
