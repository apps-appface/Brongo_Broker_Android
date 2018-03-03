package in.brongo.brongo_broker.uiwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 8/30/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "CustomTextView";

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont = a.getString(R.styleable.CustomTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        if(asset != null) {
            try {
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Unable to load typeface: " + e.getMessage());
                return false;
            }
        }else{
            try {
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/lato_regular.ttf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setTypeface(typeface);
        return true;
    }
}

