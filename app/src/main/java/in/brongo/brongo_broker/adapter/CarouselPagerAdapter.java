package in.brongo.brongo_broker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import in.brongo.brongo_broker.activity.MainActivity;
import in.brongo.brongo_broker.fragment.ItemFragment;
import in.brongo.brongo_broker.model.ApiModel;

/**
 * Created by Rohit Kumar on 10/4/2017.
 */

public class CarouselPagerAdapter extends FragmentStatePagerAdapter{

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

    public CarouselPagerAdapter(MainActivity context, FragmentManager fm, ArrayList<ApiModel.BuyAndRentModel> arrayList, ItemFragment.ViewListener viewListener, final ViewPager pager) {
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




   /* @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        return ItemFragment.newInstance(context, position, scale,arrayList,viewListener);
    }

    @Override
    public int getCount() {
        int count = 0;
        try {
            count = arrayList.size() * MainActivity.LOOPS;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return count;
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + pager.getId() + ":" + position;
    }

    public void removeFragment(int position){
        notifyDataSetChanged();
        Fragment fragment = getItem(position);
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
       // pager.removeView((View)(pager.getChildAt(position)));
    }
*/
}
