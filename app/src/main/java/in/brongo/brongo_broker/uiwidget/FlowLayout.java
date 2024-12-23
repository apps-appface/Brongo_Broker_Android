package in.brongo.brongo_broker.uiwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 1/10/2018.
 */

public class FlowLayout extends ViewGroup {
    public static final int LEFT_TO_RIGHT = 0;
    public static final int TOP_DOWN = 1;
    public static final int RIGHT_TO_LEFT = 2;
    public static final int BOTTOM_UP = 3;

    private int mGravity;
    private int mElementSpacing;
    private int mLineSpacing;
    private int mFlowDirection;
    private int mMaxLines;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        initFromAttributes(context, attributeSet);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initFromAttributes(context, attributeSet);
    }

    private boolean isHorizontal() {
        return mFlowDirection == LEFT_TO_RIGHT || mFlowDirection == RIGHT_TO_LEFT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int widthSize = (widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(widthMeasureSpec)) - getPaddingLeft() - getPaddingRight();
        final int heightSize = (heightMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(heightMeasureSpec)) - getPaddingTop() - getPaddingBottom();

        final boolean isWrapContentWidth = widthMode != MeasureSpec.EXACTLY;
        final boolean isWrapContentHeight = heightMode != MeasureSpec.EXACTLY;

        final boolean horizontal = isHorizontal();

        boolean isWrapLength;
        int lineLengthLimit;
        if (horizontal) {
            isWrapLength = isWrapContentWidth;
            lineLengthLimit = widthSize;
        } else {
            isWrapLength = isWrapContentHeight;
            lineLengthLimit = heightSize;
        }

        int lineLength = 0;
        int lineThickness = 0;
        int linePos = 0;

        int totalLength = 0;
        int totalThickness = 0;

        // temporary list of lines, used for adjusting children positions according to gravity
        ArrayList<Pair<ArrayList<LayoutParams>, Integer>> lines = new ArrayList<Pair<ArrayList<LayoutParams>, Integer>>();
        ArrayList<LayoutParams> currentLine = new ArrayList<LayoutParams>();

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();

            int childWidthMeasureSpec = makeMeasureSpec(childLayoutParams.width, widthSize, isWrapContentWidth);
            int childHeightMeasureSpec = makeMeasureSpec(childLayoutParams.height, heightSize, isWrapContentHeight);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            int childLength;
            int childThickness;
            if (horizontal) {
                childLength = child.getMeasuredWidth();
                childThickness = child.getMeasuredHeight();
            } else {
                childLength = child.getMeasuredHeight();
                childThickness = child.getMeasuredWidth();
            }

            childLayoutParams.mLength = childLength;
            childLayoutParams.mThickness = childThickness;

            int newLineLength = lineLength + childLength;

            if (i == 0 || childLayoutParams.breakLine || newLineLength > lineLengthLimit) {
                totalLength = Math.max(lineLength, totalLength);
                lines.add(new Pair<ArrayList<LayoutParams>, Integer>(currentLine, lineLength));
                currentLine = new ArrayList<LayoutParams>();
                linePos += lineThickness + (i == 0 ? 0 : mLineSpacing);

                childLayoutParams.mDepth = 0;
                lineLength = childLength;
                lineThickness = childThickness;
            } else {
                childLayoutParams.mDepth = lineLength + mElementSpacing;
                lineLength = newLineLength + mElementSpacing;
                lineThickness = Math.max(lineThickness, childThickness);
            }

            if (mMaxLines == 0 || (mMaxLines > 0 && lines.size() <= mMaxLines)) {
                totalThickness = linePos + lineThickness;
            }

            childLayoutParams.mPos = linePos;
            currentLine.add(childLayoutParams);

        }

        lines.add(new Pair<ArrayList<LayoutParams>, Integer>(currentLine, lineLength));

        totalLength = Math.max(lineLength, totalLength);

        adjustDepths(lines, isWrapLength ? totalLength : lineLengthLimit);

        if (horizontal) {
            this.setMeasuredDimension(
                    (isWrapContentWidth ? totalLength : widthSize) + getPaddingLeft() + getPaddingRight(),
                    (isWrapContentHeight ? totalThickness : heightSize) + getPaddingTop() + getPaddingBottom());
        } else {
            this.setMeasuredDimension(
                    (isWrapContentWidth ? totalThickness : widthSize) + getPaddingLeft() + getPaddingRight(),
                    (isWrapContentHeight ? totalLength : heightSize) + getPaddingTop() + getPaddingBottom());
        }
    }

    private static int makeMeasureSpec(int size, int parentSize, boolean parentWrapContent) {
        int childMeasureSpec;
        if (size >= 0) {
            childMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        } else if (parentWrapContent || size == ViewGroup.LayoutParams.WRAP_CONTENT) {
            childMeasureSpec = MeasureSpec.makeMeasureSpec(parentSize, MeasureSpec.AT_MOST);
        } else {
            childMeasureSpec = MeasureSpec.makeMeasureSpec(parentSize, MeasureSpec.EXACTLY);
        }

        return childMeasureSpec;
    }

    private void adjustDepths(ArrayList<Pair<ArrayList<LayoutParams>, Integer>> lines, int lineLengthLimit) {
        boolean center;
        boolean fill;
        boolean left;
        if (isHorizontal()) {
            center = (mGravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL;
            fill = (mGravity & Gravity.FILL_HORIZONTAL) == Gravity.FILL_HORIZONTAL;
            left = (mGravity & Gravity.LEFT) == Gravity.LEFT;
        } else {
            center = (mGravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL;
            fill = (mGravity & Gravity.FILL_VERTICAL) == Gravity.FILL_VERTICAL;
            left = (mGravity & Gravity.LEFT) == Gravity.LEFT;
        }

        if (!(center || fill || left)) {
            return;
        }

        for (Pair<ArrayList<LayoutParams>, Integer> lineInfo : lines) {
            int emptySpaceAtEnd = lineLengthLimit - lineInfo.second;
            ArrayList<LayoutParams> line = lineInfo.first;
            if (fill) {
                int childCount = line.size();
                if (childCount > 1) {
                    int spacing = emptySpaceAtEnd / (childCount - 1);
                    for (int i = 1; i < childCount; i++) {
                        LayoutParams childLayoutParams = line.get(i);
                        childLayoutParams.mDepth += spacing * i;
                    }
                }
            } else {
                int spacing = emptySpaceAtEnd / 2;
                for (LayoutParams childLayoutParams : line) {
                    childLayoutParams.mDepth += spacing;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                layoutChild(child, width, height);
            }
        }
    }

    private void layoutChild(View child, int layoutWidth, int layoutHeight) {
        LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();
        int left, top, right, bottom;
        switch (mFlowDirection) {
            case RIGHT_TO_LEFT:
                right = layoutWidth - childLayoutParams.mDepth;
                top = childLayoutParams.mPos;
                left = layoutWidth - childLayoutParams.mDepth - childLayoutParams.mLength;
                bottom = childLayoutParams.mPos + childLayoutParams.mThickness;
                break;
            case TOP_DOWN:
                left = childLayoutParams.mPos;
                top = childLayoutParams.mDepth;
                right = childLayoutParams.mPos + childLayoutParams.mThickness;
                bottom = childLayoutParams.mDepth + childLayoutParams.mLength;
                break;
            case BOTTOM_UP:
                left = childLayoutParams.mPos;
                bottom = layoutHeight - childLayoutParams.mDepth;
                right = childLayoutParams.mPos + childLayoutParams.mThickness;
                top = layoutHeight - childLayoutParams.mDepth - childLayoutParams.mLength;
                break;
            default:
                left = childLayoutParams.mDepth;
                top = childLayoutParams.mPos;
                right = childLayoutParams.mDepth + childLayoutParams.mLength;
                bottom = childLayoutParams.mPos + childLayoutParams.mThickness;
                break;
        }

        child.layout(left + getPaddingLeft(), top + getPaddingTop(), right + getPaddingLeft(), bottom + getPaddingTop());
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private void initFromAttributes(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);
        try {
            mElementSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_elementSpacing, 0);
            mLineSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_lineSpacing, 0);
            mGravity = a.getInt(R.styleable.FlowLayout_android_gravity, Gravity.NO_GRAVITY);
            mFlowDirection = a.getInt(R.styleable.FlowLayout_flowDirection, LEFT_TO_RIGHT);
            mMaxLines = a.getInt(R.styleable.FlowLayout_android_maxLines, 0);
        } finally {
            a.recycle();
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private int mLength, mThickness, mDepth, mPos;
        public boolean breakLine;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_Layout);
            try {
                breakLine = a.getBoolean(R.styleable.FlowLayout_Layout_layout_breakLine, false);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }
}

