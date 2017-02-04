package at.danielbigler.viewpagerindicatorview.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import at.danielbigler.viewpagerindicatorview.R;
import at.danielbigler.viewpagerindicatorview.utils.DensityUtils;

/**
 * Created by Daniel Bigler on 28.11.2016.
 *
 * @author Daniel Bigler
 * @version 1.1
 */

public class ViewPagerIndicatorView extends View implements ViewPager.OnPageChangeListener {

    private static final String TAG = ViewPagerIndicatorView.class.getSimpleName();

    @SuppressWarnings("FieldCanBeLocal")
    private static final int DEFAULT_COUNT = 3;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int DEFAULT_RADIUS_DP = 5;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int DEFAULT_SPACING_DP = 8;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int DEFAULT_PADDING_DP = 3;
    @SuppressWarnings("FieldCanBeLocal")
    private static final float DEFAULT_SCALE = 1.15f;

    private static final String[] positionStrings = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private final Paint strokePaint = new Paint();
    private final Paint fillPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Rect bounds = new Rect();
    private int count;
    private int radius;
    private int spacing;
    private int padding;
    private float scale;
    private boolean showNumbers;
    private int selection;
    @ColorInt
    @SuppressWarnings("FieldCanBeLocal")
    private int color;
    @SuppressWarnings("FieldCanBeLocal")
    private String numberString;

    @SuppressWarnings("FieldCanBeLocal")
    private ViewPager mViewPager;
    private int mViewPagerId;

    public ViewPagerIndicatorView(Context context) {
        super(context);
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ViewPagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("WeakerAccess")
    public ViewPagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Initializes all needed data
     *
     * @param attrs attribute set
     */
    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicatorView);

            mViewPagerId = typedArray.getResourceId(R.styleable.ViewPagerIndicatorView_viewPager, 0);
            radius = DensityUtils.dpToPx((int) typedArray.getDimension(R.styleable.ViewPagerIndicatorView_radius, DEFAULT_RADIUS_DP));
            spacing = DensityUtils.dpToPx((int) typedArray.getDimension(R.styleable.ViewPagerIndicatorView_spacing, DEFAULT_SPACING_DP));
            padding = DensityUtils.dpToPx((int) typedArray.getDimension(R.styleable.ViewPagerIndicatorView_padding, DEFAULT_PADDING_DP));
            scale = typedArray.getFloat(R.styleable.ViewPagerIndicatorView_bigScale, DEFAULT_SCALE);
            showNumbers = typedArray.getBoolean(R.styleable.ViewPagerIndicatorView_showNumbers, false);
            color = typedArray.getColor(R.styleable.ViewPagerIndicatorView_color, Color.BLACK);

            // Getting count from attributes is only for development/debug/preview purposes.
            // The final value is determined by the elements in ViewPagerAdapter, see setViewPager()
            count = typedArray.getInteger(R.styleable.ViewPagerIndicatorView_circleCount, DEFAULT_COUNT);

            typedArray.recycle();

        } else {
            // Set the default values if no Attribute set is available
            radius = DEFAULT_RADIUS_DP;
            spacing = DEFAULT_SPACING_DP;
            padding = DEFAULT_PADDING_DP;
            scale = DEFAULT_SCALE;
            showNumbers = false;
            color = ContextCompat.getColor(getContext(), android.R.color.white);
            count = DEFAULT_COUNT;
        }

        strokePaint.setStyle(Paint.Style.FILL);
        strokePaint.setColor(ColorUtils.setAlphaComponent(color, 100));
        strokePaint.setAntiAlias(true);

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        fillPaint.setAntiAlias(true);

        textPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        textPaint.setTextSize((int) (radius * 1.5));
        textPaint.getTextBounds("1234567890", 0, "1234567890".length(), bounds);
    }

    /**
     * Sets the view pager to be used with the indicator
     */
    private void setViewPager() {
        Context context = getContext();

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View view = activity.findViewById(mViewPagerId);

            if (view != null && view instanceof ViewPager) {
                mViewPager = (ViewPager) view;
                mViewPager.addOnPageChangeListener(this);

                PagerAdapter adapter = mViewPager.getAdapter();
                if (adapter != null) {
                    count = adapter.getCount();
                    requestLayout();
                } else {
                    Log.e(TAG, "ViewPager adapter not found");
                }
            } else {
                throw new NullPointerException("ViewPager reference is invalid!");
            }
        }
    }

    /**
     * Called when the view is attached to the window
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setViewPager();
    }

    /**
     * Called when view is measured
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredHeight = Math.round((2 * radius) * scale) + (2 * padding);
        int desiredWidth = (count * (2 * radius)) + ((count - 1) * spacing) + (2 * padding);

        int width = 0;
        int height = 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.UNSPECIFIED:
                width = desiredWidth;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = desiredHeight;
                break;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * Called when the view is about to be drawn
     *
     * @param canvas Canvas sheet
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int y = getHeight() / 2;
        int x = radius + padding;

        for (int i = 0; i < count; i++) {
            draw(canvas, i, x, y);
            x += (2 * radius) + spacing;
        }
    }

    /**
     * Draws the indicators
     *
     * @param canvas   Canvas sheet to draw on
     * @param position position of the current indicators
     * @param x        x coordinate
     * @param y        y coordinate
     */
    private void draw(@NonNull Canvas canvas, int position, int x, int y) {
        if (position == selection) {
            canvas.drawCircle(x, y, (int) (radius * scale), fillPaint);
        } else {
            canvas.drawCircle(x, y, radius, strokePaint);
        }

        if (showNumbers && selection == position) {
            drawNumbers(canvas, position, x, y);
        }
    }

    /**
     * Draws the position onto the canvas sheet
     *
     * @param canvas   Canvas element to draw on
     * @param position position/number to be drawn
     * @param x        xCoordinate
     * @param y        yCoordinate
     */
    private void drawNumbers(@NonNull Canvas canvas, int position, int x, int y) {
        numberString = convertIntToString(position + 1);
        canvas.drawText(
                numberString,
                x - (textPaint.measureText(numberString) / 2),
                y + (bounds.height() / 2),
                textPaint
        );
    }

    /**
     * Converts integer to string. Uses lookup in string array due to better performance than String.valueOf()
     * Only fast for less than 11 objects
     *
     * @param position Position to lookup = value to be the string
     * @return string representation of position
     */
    private String convertIntToString(int position) {
        if (position < 10) {
            return positionStrings[position];
        } else {
            return String.valueOf(position);
        }
    }

    /**
     * Sets the current position/selection of the viewpager
     *
     * @param position current selection of the viewpager
     */
    private void setSelection(@IntRange(from = 0) int position) {
        selection = position;
        invalidate();
    }

    /**
     * Called when the view pager is scrolled
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * Called when a page is selected by the viewpager
     *
     * @param position selected position
     */
    @Override
    public void onPageSelected(int position) {
        setSelection(position);
    }

    /**
     * Called when the scroll state of the page changes
     */
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                //Log.d(DEBUG_TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                invalidate();
                return true;
            case (MotionEvent.ACTION_UP) :
                //Log.d(DEBUG_TAG,"Action was UP");
                checkIntersection(event.getX(), event.getY());
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                //Log.d(DEBUG_TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                //Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                //       "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }

    }

    private void checkIntersection(float x, float y) {
        Rect touch = new Rect((int) x, (int) y, radius, radius);
    }*/
}
