package appface.brongo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

import appface.brongo.R;
import appface.brongo.activity.LoginActivity;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.WalkThroughActivity;
import appface.brongo.model.ApiModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.Utils;

/**
 * Created by Rohit Kumar on 9/28/2017.
 */

public class WalkPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<ApiModel.WalkThroughModel> arrayList;
    private SharedPreferences pref;

    public WalkPagerAdapter(Context mContext, ArrayList<ApiModel.WalkThroughModel> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        pref = mContext.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.walkthrough_page, container, false);
        TextView title = (TextView)itemView.findViewById(R.id.walk_title);
        TextView subtitle = (TextView)itemView.findViewById(R.id.walk_sub_title);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.walk_image);
        title.setText(arrayList.get(position).getTitle());
        subtitle.setText(arrayList.get(position).getSubTitle());
       // imageView.setImageResource(arrayList.get(position).getImageId());
        Glide.with(mContext).load(arrayList.get(position).getImageId()).into(imageView);
        //Glide.with(mContext).load(R.drawable.walkthrough).into(imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
       // Glide.clear(((RelativeLayout) object).getChildAt(position));
    }
}
