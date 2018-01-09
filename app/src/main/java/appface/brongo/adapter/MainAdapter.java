package appface.brongo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import appface.brongo.activity.MainActivity;
import appface.brongo.fragment.ItemFragment;
import appface.brongo.model.ApiModel;

/**
 * Created by Rohit Kumar on 12/26/2017.
 */

public class MainAdapter extends FragmentStatePagerAdapter {

    public final static float BIG_SCALE = 0.9f;
    public final static float RATIO_SCALE = 0.3f;
    public final static float DIFF_SCALE = BIG_SCALE - RATIO_SCALE;
    private MainActivity context;
    private FragmentManager fragmentManager;
    private ArrayList<ApiModel.BuyAndRentModel> arrayList;
    private float scale;
    private ArrayList<Fragment> fragmentlist;
    private ViewPager pager;
    private ItemFragment.ViewListener viewListener;

    public MainAdapter(MainActivity context, FragmentManager fm, ArrayList<ApiModel.BuyAndRentModel> arrayList, ItemFragment.ViewListener viewListener, final ViewPager pager) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.arrayList = arrayList;
        this.viewListener = viewListener;
        fragmentlist = new ArrayList<>();
        this.pager =pager;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = ItemFragment.newInstance(position, scale,arrayList,viewListener);
        fragmentlist.add(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
    public void deletePage(int position) {
        if (canDelete()) {
            fragmentlist.remove(position);
            notifyDataSetChanged();
        }
    }

    boolean canDelete() {
        return fragmentlist.size() > 0;
    }
    public Fragment getFragment(int position){
        return fragmentlist.get(position);
    }

    // This is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}
