package in.brongo.brongo_broker.util;

import android.app.Application;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import in.brongo.brongo_broker.R;
import io.branch.referral.Branch;

/**
 * Created by Rohit Kumar on 10/31/2017.
 */

public class CustomApplicationClass extends Application {
    private static RequestOptions requestOptions,requestOptions1;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Branch object
        Branch.getAutoInstance(this);
    }

    public static RequestOptions getRequestOption(boolean showCached) {
        if (requestOptions == null) {
            if (showCached) {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder1);
                requestOptions.error(R.drawable.placeholder1);
                requestOptions.circleCrop();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.timeout(120000);
            } else {
                requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.placeholder1);
                requestOptions.error(R.drawable.placeholder1);
                requestOptions.circleCrop();
                requestOptions.skipMemoryCache(true);
                requestOptions.timeout(120000);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        return requestOptions;
    }
    public static RequestOptions getPropertyImage(boolean showCached) {
        if (requestOptions1 == null) {
            if (showCached) {
                requestOptions1 = new RequestOptions();
                requestOptions1.placeholder(R.drawable.no_image);
                requestOptions1.error(R.drawable.no_image);
                requestOptions1.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions1.timeout(120000);
            } else {
                requestOptions1 = new RequestOptions();
                requestOptions1.placeholder(R.drawable.no_image);
                requestOptions1.error(R.drawable.no_image);
                requestOptions1.skipMemoryCache(true);
                requestOptions1.timeout(120000);
                requestOptions1.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        return requestOptions1;
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
}
