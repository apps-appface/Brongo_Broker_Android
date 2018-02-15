package in.brongo.brongo_broker.uiwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 2/14/2018.
 */

public class GifSplashView extends View {
    private static final String TAG = "GifView";
    private static final int DEFAULT_MOVIE_VIEW_DURATION = 1000;
    private int mMovieResourceId;
    private Movie movie;
    private long mMovieStart;
    private int mCurrentAnimationTime;
    private float mLeft;
    private float mTop;
    private float mScale;
    private int mMeasuredMovieWidth;
    private int mMeasuredMovieHeight;
    private volatile boolean mPaused;
    private boolean mVisible;

    public GifSplashView(Context context) {
        this(context, (AttributeSet) null);
    }

    public GifSplashView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifViewStyle);
    }

    public GifSplashView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mVisible = true;
        this.setViewAttributes(context, attrs, defStyle);
    }

    @SuppressLint({"NewApi"})
    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        if (Build.VERSION.SDK_INT >= 11) {
            this.setLayerType(1, (Paint) null);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifView, defStyle, R.style.Widget_GifView);
        this.mMovieResourceId = array.getResourceId(R.styleable.GifView_gif, -1);
        this.mPaused = array.getBoolean(R.styleable.GifView_paused, false);
        array.recycle();
        if (this.mMovieResourceId != -1) {
            this.movie = Movie.decodeStream(this.getResources().openRawResource(this.mMovieResourceId));
        }

    }

    public void setGifResource(int movieResourceId) {
        this.mMovieResourceId = movieResourceId;
        this.movie = Movie.decodeStream(this.getResources().openRawResource(this.mMovieResourceId));
        this.requestLayout();
    }

    public int getGifResource() {
        return this.mMovieResourceId;
    }

    public void play() {
        if (this.mPaused) {
            this.mPaused = false;
            this.mMovieStart = SystemClock.uptimeMillis() - (long) this.mCurrentAnimationTime;
            this.invalidate();
        }

    }

    public void pause() {
        if (!this.mPaused) {
            this.mPaused = true;
            this.invalidate();
        }

    }

    public boolean isPaused() {
        return this.mPaused;
    }

    public boolean isPlaying() {
        return !this.mPaused;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.movie != null) {
            int movieWidth = this.movie.width();
            int movieHeight = this.movie.height();
            float scaleH = 1.0F;
            int measureModeWidth = MeasureSpec.getMode(widthMeasureSpec);
            if (measureModeWidth != 0) {
                int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
                if (movieWidth > maximumWidth) {
                    scaleH = (float) movieWidth / (float) maximumWidth;
                }
            }

            float scaleW = 1.0F;
            int measureModeHeight = MeasureSpec.getMode(heightMeasureSpec);
            if (measureModeHeight != 0) {
                int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
                if (movieHeight > maximumHeight) {
                    scaleW = (float) movieHeight / (float) maximumHeight;
                }
            }

            this.mScale = 1.0F / Math.max(scaleH, scaleW);
            this.mMeasuredMovieWidth = (int) ((float) movieWidth * this.mScale);
            this.mMeasuredMovieHeight = (int) ((float) movieHeight * this.mScale);
            this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            this.setMeasuredDimension(this.getSuggestedMinimumWidth(), this.getSuggestedMinimumHeight());
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mLeft = (float) (this.getWidth() - this.mMeasuredMovieWidth) / 2.0F;
        this.mTop = (float) (this.getHeight() - this.mMeasuredMovieHeight) / 2.0F;
        this.mVisible = this.getVisibility() == View.VISIBLE;
    }

    protected void onDraw(Canvas canvas) {

        if (this.movie != null) {
            if (!this.mPaused) {
                this.updateAnimationTime();
                this.drawMovieFrame(canvas);
                this.invalidateView();
            } else {
                this.drawMovieFrame(canvas);
            }
        }

    }

    @SuppressLint({"NewApi"})
    private void invalidateView() {
        if (this.mVisible) {
            if (Build.VERSION.SDK_INT >= 16) {
                this.postInvalidateOnAnimation();
            } else {
                this.invalidate();
            }
        }

    }

    private void updateAnimationTime() {
        long now = SystemClock.uptimeMillis();
        if (this.mMovieStart == 0L) {
            this.mMovieStart = now;
        }

        int dur = this.movie.duration();
        if (dur == 0) {
            dur = 1000;
        }

        if (this.mCurrentAnimationTime >= 1850 && mMovieResourceId != R.drawable.splash_gif) {
            pause();
        } else {
            this.mCurrentAnimationTime = (int) ((now - this.mMovieStart) % (long) dur);
        }
    }

    private void drawMovieFrame(Canvas canvas) {
        this.movie.setTime(this.mCurrentAnimationTime);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.scale(this.mScale, this.mScale);
        this.movie.draw(canvas, this.mLeft / this.mScale, this.mTop / this.mScale);
        canvas.restore();
    }

    @SuppressLint({"NewApi"})
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        this.mVisible = screenState == 1;
        this.invalidateView();
    }

    @SuppressLint({"NewApi"})
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.mVisible = visibility == 0;
        this.invalidateView();
    }

    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mVisible = visibility == 0;
        this.invalidateView();
    }
}
