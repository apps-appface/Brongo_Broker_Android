package appface.brongo.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import appface.brongo.R;
import io.branch.referral.Branch;

/**
 * Created by Rohit Kumar on 10/31/2017.
 */

public class CustomApplicationClass extends Application {
    private static RequestOptions requestOptions;
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Branch object
        Branch.getAutoInstance(this);
    }
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static RequestOptions getRequestOption(boolean showCached) {
        if (requestOptions == null) {
            if (showCached) {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder1);
                requestOptions.error(R.drawable.placeholder1);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.timeout(120000);
            } else {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder1);
                requestOptions.error(R.drawable.placeholder1);
                requestOptions.skipMemoryCache(true);
                requestOptions.timeout(120000);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        return requestOptions;
    }

    public static RequestOptions getRequestOptionSized(int width, int height) {
        if (requestOptions == null) {
            requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder1);
            requestOptions.error(R.drawable.placeholder1);
            requestOptions.skipMemoryCache(true);
            requestOptions.timeout(120000);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.override(width, height);
        }
        return requestOptions;
    }
    public static RequestOptions getRequestOptionProperty(boolean showCached) {
        if (requestOptions == null) {
            if (showCached) {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.no_image);
                requestOptions.error(R.drawable.no_image);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.timeout(120000);
            } else {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.no_image);
                requestOptions.error(R.drawable.no_image);
                requestOptions.skipMemoryCache(true);
                requestOptions.timeout(120000);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        return requestOptions;
    }
}
