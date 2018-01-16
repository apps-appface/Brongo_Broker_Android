package appface.brongo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.WalkPagerAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.util.AppConstants;
import appface.brongo.util.Utils;

public class WalkThroughActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    protected View view;
    private ViewPager viewpager;
    private LinearLayout pager_indicator;
    private Button walk_skip_btn;
    private int dotsCount;
    ArrayList<String> titleList;
    ArrayList<String> sub_titleList;
    private String activity_name="";
    ArrayList<Integer> imageIdList;
    ArrayList<ApiModel.WalkThroughModel> arrayList;
    private WalkPagerAdapter mAdapter;
    private ImageView[] dots;
    private boolean isLastPageSwiped;
    private int counterPageScroll;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(getIntent().getExtras() != null) {
            activity_name = getIntent().getExtras().getString("from_activity");
        }
        // To make activity full screen.
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);
        pref = getSharedPreferences(AppConstants.PREF_NAME,0);
        setReference();
        walk_skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getBoolean(AppConstants.LOGIN_STATUS,false)) {
                  onBackPressed();
                }else{
                    startActivity(new Intent(WalkThroughActivity.this, LoginActivity.class));
                }
            }
        });

    }
    private void setReference() {
        arrayList = new ArrayList<>();
        titleList = new ArrayList<>();
        sub_titleList = new ArrayList<>();
        imageIdList = new ArrayList<>();
        walk_skip_btn = (Button)findViewById(R.id.walk_skip_btn);
        String[] title = getResources().getStringArray(R.array.walkthrough_title);
        String[] subtitle = getResources().getStringArray(R.array.walkthrough_subtitle);
        for(int i= 0;i<title.length;i++){
            titleList.add(title[i]);
            sub_titleList.add(subtitle[i]);
        }
        imageIdList.add(R.drawable.walk1);
        imageIdList.add(R.drawable.walk2);
        imageIdList.add(R.drawable.walk3);
        imageIdList.add(R.drawable.walk4);
        imageIdList.add(R.drawable.walk5);
        imageIdList.add(R.drawable.walk6);
        for(int i=0;i<titleList.size();i++){
            ApiModel.WalkThroughModel walkThroughModel = new ApiModel.WalkThroughModel();
            walkThroughModel.setTitle(titleList.get(i));
            walkThroughModel.setSubTitle(sub_titleList.get(i));
            walkThroughModel.setImageId(imageIdList.get(i));
            arrayList.add(walkThroughModel);
        }

        viewpager = (ViewPager)findViewById(R.id.pager_introduction);

        pager_indicator = (LinearLayout)findViewById(R.id.viewPagerCountDots);

        mAdapter = new WalkPagerAdapter(WalkThroughActivity.this, arrayList);
        viewpager.setAdapter(mAdapter);
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(this);
        setUiPageViewController();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 5 && positionOffset == 0 && !isLastPageSwiped){
            Intent intent;
            if(counterPageScroll != 0){
                isLastPageSwiped=true;
                if(pref.getBoolean(AppConstants.LOGIN_STATUS,false)) {
                   onBackPressed();
                }else{
                     intent = new Intent(WalkThroughActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            }
            counterPageScroll++;
        }else{
            counterPageScroll=0;
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onBackPressed() {
        try {
            switch (activity_name) {
                case "splash":
                    finishAffinity();
                    break;
                case "menu":
                   /* Intent intent = new Intent(WalkThroughActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);*/
                    super.onBackPressed();
                    break;
            }
        }catch (Exception e){
        }

    }
}
