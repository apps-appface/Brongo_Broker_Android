package in.brongo.brongo_broker.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.payu.india.Payu.Payu;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.fragment.AboutUsFragment;
import in.brongo.brongo_broker.fragment.AddInventoryFragment;
import in.brongo.brongo_broker.fragment.ContactFragment;
import in.brongo.brongo_broker.fragment.FaqFragment;
import in.brongo.brongo_broker.fragment.HelpFaqFragment;
import in.brongo.brongo_broker.fragment.HistoricalDealFragment;
import in.brongo.brongo_broker.fragment.IndividualInventoryFragment;
import in.brongo.brongo_broker.fragment.InventoryListFragment;
import in.brongo.brongo_broker.fragment.LegalFragment;
import in.brongo.brongo_broker.fragment.MatchingPropertyFragment;
import in.brongo.brongo_broker.fragment.NotificationFragment;
import in.brongo.brongo_broker.fragment.PremiumFragment;
import in.brongo.brongo_broker.fragment.RatingFragment;
import in.brongo.brongo_broker.fragment.ReferFragmentMore;
import in.brongo.brongo_broker.fragment.RequirementFragment;
import in.brongo.brongo_broker.fragment.ReviewFragment;
import in.brongo.brongo_broker.fragment.SettingFragment;
import in.brongo.brongo_broker.fragment.SubscriptionFragment;
import in.brongo.brongo_broker.fragment.SupportFragment;
import in.brongo.brongo_broker.fragment.TandC_Fragment;
import in.brongo.brongo_broker.util.Utils;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ABOUTUS;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.CONTACT;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.HELP_FAQ;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.HISTORICAL;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.INVENTORY_LIST;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.LEGAL;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.MATCHING_PROPERTY;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.NOTIFICATION;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.PREMIUM;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.RATING;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.REFER_MORE;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.REQUIREMENT;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.REVIEW;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.SETTING;
import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.SUBSCRIPTION;

public class Menu_Activity extends AppCompatActivity {

    private Bundle bundle;
    private Toolbar toolbar;
    private String activity_name = "";
    private RelativeLayout parentLayout;
    private ImageView back_image, toolbar_delete, toolbar_edit, toolbar_add;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Payu.setInstance(this);
        String intentFragment = getIntent().getExtras().getString("frgToLoad");
        activity_name = getIntent().getExtras().getString("activity_name");
        bundle = new Bundle();
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        setContentView(R.layout.activity_menu);
        parentLayout = findViewById(R.id.menu_parent_relative);
        toolbar = findViewById(R.id.inventory_toolbar);
        back_image = findViewById(R.id.toolbar_inventory_back);
        toolbar_delete = findViewById(R.id.toolbar_inventory_delete);
        toolbar_edit = findViewById(R.id.toolbar_inventory_edit);
        toolbar_add = findViewById(R.id.toolbar_inventory_add);
        changeFragment(intentFragment);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.Fragment currentFragment = fragmentManager.findFragmentById(R.id.inventory_frag_container);
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeFragment(String intentFragment) {
        try {
            switch (intentFragment) {
                case "InventoryListFragment":
                    InventoryListFragment inventoryListFragment = new InventoryListFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), inventoryListFragment, R.id.inventory_frag_container, INVENTORY_LIST);
                    break;
                case "AddInventoryFragment":
                    AddInventoryFragment addInventoryFragment = new AddInventoryFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), addInventoryFragment, R.id.inventory_frag_container, ADD_INVENTORY);
                    break;
                case "HistoricalFragment":
                    HistoricalDealFragment historicalDealFragment = new HistoricalDealFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), historicalDealFragment, R.id.inventory_frag_container, HISTORICAL);
                    break;
                case "NotificationFragment":
                    NotificationFragment notificationFragment = new NotificationFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), notificationFragment, R.id.inventory_frag_container, NOTIFICATION);
                    break;
                case "MatchingPropertyFragment":
                    MatchingPropertyFragment matchingPropertyFragment = new MatchingPropertyFragment();
                    matchingPropertyFragment.setArguments(bundle);
                    Utils.replaceFragment(getSupportFragmentManager(), matchingPropertyFragment, R.id.inventory_frag_container, MATCHING_PROPERTY);
                    break;
                case "ReferFragmentMore":
                    ReferFragmentMore referFragment1 = new ReferFragmentMore();
                    Utils.replaceFragment(getSupportFragmentManager(), referFragment1, R.id.inventory_frag_container, REFER_MORE);
                    break;
                case "SettingFragment":
                    SettingFragment settingFragment = new SettingFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), settingFragment, R.id.inventory_frag_container, SETTING);
                    break;
                case "AboutUsFragment":
                    AboutUsFragment aboutUsFragment = new AboutUsFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), aboutUsFragment, R.id.inventory_frag_container, ABOUTUS);
                    break;
                case "HelpFragment":
                    HelpFaqFragment helpFaqFragment = new HelpFaqFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), helpFaqFragment, R.id.inventory_frag_container, HELP_FAQ);
                    break;
                case "RatingFragment":
                    RatingFragment ratingFragment = new RatingFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), ratingFragment, R.id.inventory_frag_container, RATING);
                    break;
                case "SubscriptionFragment":
                    SubscriptionFragment subscriptionFragment = new SubscriptionFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), subscriptionFragment, R.id.inventory_frag_container, SUBSCRIPTION);
                    break;
                case "ReviewFragment":
                    ReviewFragment reviewFragment = new ReviewFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), reviewFragment, R.id.inventory_frag_container, REVIEW);
                    break;
                case "premiumFragment":
                    PremiumFragment premiumfragment = new PremiumFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), premiumfragment, R.id.inventory_frag_container, PREMIUM);
                    break;
                case "requirement_page":
                    RequirementFragment requirementFragment = new RequirementFragment();
                    requirementFragment.setArguments(bundle);
                    Utils.replaceFragment(getSupportFragmentManager(), requirementFragment, R.id.inventory_frag_container, REQUIREMENT);
                    break;
                case "ContactFragment":
                    ContactFragment contactFragment = new ContactFragment();
                    Utils.replaceFragment(getSupportFragmentManager(), contactFragment, R.id.inventory_frag_container, CONTACT);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                SettingFragment.myOnKeyDown(keyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                SettingFragment.myOnKeyDown(keyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                SettingFragment.myOnKeyDown(keyCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Utils.LoaderUtils.dismissLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
