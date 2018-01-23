package appface.brongo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.payu.india.Payu.Payu;

import appface.brongo.R;
import appface.brongo.fragment.AboutUsFragment;
import appface.brongo.fragment.AddInventoryFragment;
import appface.brongo.fragment.ContactFragment;
import appface.brongo.fragment.FaqFragment;
import appface.brongo.fragment.HelpFaqFragment;
import appface.brongo.fragment.HistoricalDealFragment;
import appface.brongo.fragment.IndividualInventoryFragment;
import appface.brongo.fragment.InventoryListFragment;
import appface.brongo.fragment.LegalFragment;
import appface.brongo.fragment.MatchingPropertyFragment;
import appface.brongo.fragment.NotificationFragment;
import appface.brongo.fragment.PremiumFragment;
import appface.brongo.fragment.RatingFragment;
import appface.brongo.fragment.ReferFragmentMore;
import appface.brongo.fragment.RequirementFragment;
import appface.brongo.fragment.ReviewFragment;
import appface.brongo.fragment.SettingFragment;
import appface.brongo.fragment.SubscriptionFragment;
import appface.brongo.fragment.SupportFragment;
import appface.brongo.fragment.TandC_Fragment;
import appface.brongo.util.Utils;

import static appface.brongo.fragment.SettingFragment.myOnKeyDown;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.ABOUTUS;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.HELP_FAQ;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.HISTORICAL;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.INVENTORY_LIST;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.LEGAL;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.MATCHING_PROPERTY;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.NOTIFICATION;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.PREMIUM;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.RATING;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.REFER_MORE;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.REQUIREMENT;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.REVIEW;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.SETTING;
import static appface.brongo.util.AppConstants.FRAGMENT_TAGS.SUBSCRIPTION;

public class Menu_Activity extends AppCompatActivity {

    private Bundle bundle;
    private Toolbar toolbar;
    private String activity_name="";
    private ImageView back_image,toolbar_delete,toolbar_edit,toolbar_add;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Payu.setInstance(this);
        String intentFragment = getIntent().getExtras().getString("frgToLoad");
        activity_name = getIntent().getExtras().getString("activity_name");
        bundle =new Bundle();
        if(getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        setContentView(R.layout.activity_menu);
        toolbar = (Toolbar)findViewById(R.id.inventory_toolbar);
        back_image = (ImageView)findViewById(R.id.toolbar_inventory_back);
        toolbar_delete = (ImageView)findViewById(R.id.toolbar_inventory_delete);
        toolbar_edit = (ImageView)findViewById(R.id.toolbar_inventory_edit);
        toolbar_add = (ImageView)findViewById(R.id.toolbar_inventory_add);
        changeFragment(intentFragment);
     /* back_image.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              changeToolbarText();
          }
      });*/
     back_image.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onBackPressed();
         }
     });
    }

    @Override
    public void onBackPressed() {
      /*  Intent intent = new Intent(Menu_Activity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();*/
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
    private void changeFragment(String intentFragment)
    {
        switch (intentFragment){
            case "InventoryListFragment":
                InventoryListFragment inventoryListFragment = new InventoryListFragment();
                Utils.replaceFragment(getSupportFragmentManager(),inventoryListFragment,R.id.inventory_frag_container,INVENTORY_LIST);
                break;
            case "AddInventoryFragment":
                AddInventoryFragment addInventoryFragment = new AddInventoryFragment();
                Utils.replaceFragment(getSupportFragmentManager(),addInventoryFragment,R.id.inventory_frag_container,ADD_INVENTORY);
                break;
            case "HistoricalFragment":
                HistoricalDealFragment historicalDealFragment = new HistoricalDealFragment();
                Utils.replaceFragment(getSupportFragmentManager(),historicalDealFragment,R.id.inventory_frag_container,HISTORICAL);
                break;
            case "NotificationFragment":
                NotificationFragment notificationFragment = new NotificationFragment();
                Utils.replaceFragment(getSupportFragmentManager(),notificationFragment,R.id.inventory_frag_container,NOTIFICATION);
                break;
            case "MatchingPropertyFragment":
                MatchingPropertyFragment matchingPropertyFragment = new MatchingPropertyFragment();
                matchingPropertyFragment.setArguments(bundle);
                Utils.replaceFragment(getSupportFragmentManager(),matchingPropertyFragment,R.id.inventory_frag_container,MATCHING_PROPERTY);
                break;
            case "ReferFragmentMore":
                ReferFragmentMore referFragment1 = new ReferFragmentMore();
                Utils.replaceFragment(getSupportFragmentManager(),referFragment1,R.id.inventory_frag_container,REFER_MORE);
                break;
            case "SettingFragment":
                SettingFragment settingFragment = new SettingFragment();
                Utils.replaceFragment(getSupportFragmentManager(),settingFragment,R.id.inventory_frag_container,SETTING);
                break;
            case "AboutUsFragment":
                AboutUsFragment aboutUsFragment = new AboutUsFragment();
                Utils.replaceFragment(getSupportFragmentManager(),aboutUsFragment,R.id.inventory_frag_container,ABOUTUS);
                break;
            case "HelpFragment":
                HelpFaqFragment helpFaqFragment = new HelpFaqFragment();
                Utils.replaceFragment(getSupportFragmentManager(),helpFaqFragment,R.id.inventory_frag_container,HELP_FAQ);
                break;
            case "RatingFragment":
                RatingFragment ratingFragment = new RatingFragment();
                Utils.replaceFragment(getSupportFragmentManager(),ratingFragment,R.id.inventory_frag_container,RATING);
                break;
            case "SubscriptionFragment":
                SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                Utils.replaceFragment(getSupportFragmentManager(),subscriptionFragment,R.id.inventory_frag_container,SUBSCRIPTION);
                break;
            case "ReviewFragment":
                ReviewFragment reviewFragment = new ReviewFragment();
                Utils.replaceFragment(getSupportFragmentManager(),reviewFragment,R.id.inventory_frag_container,REVIEW);
                break;
            case "premiumFragment":
                PremiumFragment premiumfragment = new PremiumFragment();
                Utils.replaceFragment(getSupportFragmentManager(),premiumfragment,R.id.inventory_frag_container,PREMIUM);
                break;
            case "requirement_page":
                RequirementFragment requirementFragment = new RequirementFragment();
                requirementFragment.setArguments(bundle);
                Utils.replaceFragment(getSupportFragmentManager(),requirementFragment,R.id.inventory_frag_container,REQUIREMENT);
                break;
        }
    }
    private void changeToolbarText(){
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment currentFragment = fragmentManager.findFragmentById(R.id.inventory_frag_container);
        if(currentFragment instanceof LegalFragment || currentFragment instanceof AboutUsFragment || currentFragment instanceof SupportFragment || currentFragment instanceof ContactFragment
                ||currentFragment instanceof FaqFragment){
            HelpFaqFragment helpFaqFragment = new HelpFaqFragment();
            Utils.replaceFragment(getSupportFragmentManager(),helpFaqFragment,R.id.inventory_frag_container,HELP_FAQ);
            toolbar.setVisibility(View.VISIBLE);

        }else if(currentFragment instanceof HelpFaqFragment  || currentFragment instanceof NotificationFragment || currentFragment instanceof HistoricalDealFragment || currentFragment instanceof InventoryListFragment || currentFragment instanceof RatingFragment
                || currentFragment instanceof MatchingPropertyFragment || currentFragment instanceof SettingFragment || currentFragment instanceof ReviewFragment || currentFragment instanceof AddInventoryFragment || currentFragment instanceof RequirementFragment){
            toolbar_add.setVisibility(View.GONE);
            Intent intent = new Intent(Menu_Activity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }else if(currentFragment instanceof ReferFragmentMore){
            Intent intent = new Intent(Menu_Activity.this,ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }else if(currentFragment instanceof IndividualInventoryFragment){
            InventoryListFragment inventoryListFragment = new InventoryListFragment();
            Utils.replaceFragment(getSupportFragmentManager(),inventoryListFragment,R.id.inventory_frag_container,INVENTORY_LIST);
        }else if(currentFragment instanceof TandC_Fragment){
            LegalFragment legalFragment = new LegalFragment();
            Utils.replaceFragment(getSupportFragmentManager(),legalFragment,R.id.inventory_frag_container,LEGAL);
        }else if(currentFragment instanceof SubscriptionFragment){
            finish();
        }else if(currentFragment instanceof PremiumFragment){
            if(activity_name != null && activity_name.equalsIgnoreCase("profile")){
                finish();
            }else {
                SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                Utils.replaceFragment(getSupportFragmentManager(), subscriptionFragment, R.id.inventory_frag_container, SUBSCRIPTION);
                toolbar.setVisibility(View.VISIBLE);
            }
        }
    }
    private void backFragment(android.support.v4.app.FragmentManager fragmentManager, android.support.v4.app.Fragment fragment, int view_id) {
        if (!fragment.isAdded()) {

                fragmentManager.beginTransaction()
                        .replace(view_id, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            SettingFragment.myOnKeyDown(keyCode);
            //and so on...
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            SettingFragment.myOnKeyDown(keyCode);
            //and so on...
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
if(keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)  {
            SettingFragment.myOnKeyDown(keyCode);
            //and so on..
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

}
