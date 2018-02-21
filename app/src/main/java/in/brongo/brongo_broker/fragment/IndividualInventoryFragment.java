package in.brongo.brongo_broker.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.MainActivity;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.uiwidget.FlowLayout;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.ADD_INVENTORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class IndividualInventoryFragment extends Fragment implements NoInternetTryConnectListener,View.OnTouchListener{
   private ApiModel.InventoryPersoanlList inventoryPersoanlList = new ApiModel.InventoryPersoanlList();
    private TextView inven_individual_name,inven_individual_mobile,inven_individual_client_type,toolbar_title,inven_individual_email,inven_individual_notes;
    private ImageView inven_individual_image,toolbar_delete,toolbar_edit,toolbarAdd,imageView1;
    private String propertyImage2,propertyImage3,propertyId,postingType,microMarketName,microMarketCity,microMarketState,propertyType,propertyStatus,clientName,clientMobileNo,emailId,note,propertyImage1,bedRoomType,subPropertyType;
    private long budget;
    private Toolbar toolbar;
    private FlowLayout flowLayout;
    private LinearLayout pager_indicator;
    private ViewPager viewPager;
    private View email_view;
    private ImageView[] dots;
    private int dotsCount;
    private RelativeLayout parentLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    private MyCustomPagerAdapter customPagerAdapter;
    private SharedPreferences pref;
    private Context context;
    public IndividualInventoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            propertyImage2 = getArguments().getString("propertyImage2","");
            propertyImage3 = getArguments().getString("propertyImage3","");
            propertyId = getArguments().getString("propertyId","");
            postingType = getArguments().getString("postingType","");
            microMarketName = getArguments().getString("microMarketName","");
            microMarketCity = getArguments().getString("microMarketCity","");
            microMarketState = getArguments().getString("microMarketState","");
            propertyType = getArguments().getString("propertyType","");
            propertyStatus = getArguments().getString("propertyStatus","");
            clientName = getArguments().getString("clientName","");
            emailId = getArguments().getString("emailId","");
            note = getArguments().getString("note","");
            clientMobileNo = getArguments().getString("clientMobileNo","");
            propertyImage1 = getArguments().getString("propertyImage1","");
            bedRoomType = getArguments().getString("bedRoomType","");
            budget = getArguments().getLong("budget");
            subPropertyType = getArguments().getString("subPropertyType","");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_individual_inventory, container, false);
        initialise(view);
        setView();
        try {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
            });
            toolbar_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inventory_delete_dialog();
                }
            });
            toolbar_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inventoryEdit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            flowLayout = view.findViewById(R.id.individual_inven_flowlayout);
            ArrayList<String> images = new ArrayList<>();
            if(!propertyImage1.equalsIgnoreCase("")){
              images.add(propertyImage1);
            }
            if(!propertyImage2.equalsIgnoreCase("")){
                images.add(propertyImage2);
            }
            if(!propertyImage3.equalsIgnoreCase("")){
                images.add(propertyImage3);
            }
            pref=context.getSharedPreferences(AppConstants.PREF_NAME,0);
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            inven_individual_name = view.findViewById(R.id.inventory_individual_name);
            inven_individual_client_type = view.findViewById(R.id.inventory_individual_client);
            inven_individual_mobile = view.findViewById(R.id.inventory_individual_mobile);
            inven_individual_email = view.findViewById(R.id.inventory_individual_email);
            email_view = view.findViewById(R.id.email_view);
            pager_indicator = view.findViewById(R.id.viewPagerCountDots1);
            viewPager = view.findViewById(R.id.viewPager_inventory);
            inven_individual_notes = view.findViewById(R.id.inventory_individual_notes);
            inven_individual_image = view.findViewById(R.id.inventory_individual_image);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar_edit = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            toolbarAdd = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            toolbar_delete = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            toolbar_edit.setVisibility(View.VISIBLE);
            toolbar_delete.setVisibility(View.VISIBLE);
            toolbarAdd.setVisibility(View.GONE);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Inventory Details");
            customPagerAdapter = new MyCustomPagerAdapter(context, images);
            viewPager.setAdapter(customPagerAdapter);
            if(images.size()>0){
                inven_individual_image.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }else{
                inven_individual_image.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
            }
            setUiPageViewController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setView(){
        try {
            String budget1 = budget+"";
            budget1 = Utils.stringToInt(budget1);
            inven_individual_name.setText(clientName);
            inven_individual_mobile.setText(clientMobileNo);
            inven_individual_client_type.setText(postingType.toUpperCase()+"/"+propertyType.toUpperCase());
            inven_individual_email.setText(emailId);
            if(emailId.equalsIgnoreCase("")){
                inven_individual_email.setVisibility(View.GONE);
                email_view.setVisibility(View.GONE);
            }
            addview(microMarketName);
            addview(bedRoomType);
            addview(budget1);
            addview(subPropertyType);
            addview(propertyStatus);
            inven_individual_notes.setText(note);

            Glide.with(context)
                    .load(propertyImage1)
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(inven_individual_image);
            if(postingType.equalsIgnoreCase("sell")){
                inven_individual_client_type.setBackgroundColor(Color.parseColor("#3664cb"));
            }else if(postingType.equalsIgnoreCase("rent_out")){
                inven_individual_client_type.setBackgroundColor(Color.parseColor("#80cb36"));
            }else if(postingType.equalsIgnoreCase("rent")){
                inven_individual_client_type.setBackgroundColor(Color.parseColor("#80cb36"));
            }else{
                inven_individual_client_type.setBackgroundColor(Color.parseColor("#ea8737"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inventory_delete_dialog(){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_delete_inventory);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            TextView delete_client_message = dialog.findViewById(R.id.delete_inventory_client_name);
            Button delete_client_cancel = dialog.findViewById(R.id.inventory_delete_cancel);
            Button delete_client_delete = dialog.findViewById(R.id.inventory_delete_delete);
            String message = "Are you sure you want to delete the \n inventory details of '"+clientName+"'?";
            delete_client_message.setText(message);
            delete_client_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            delete_client_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        deleteInventory();

                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteInventory(){
        try {
            if(Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
                clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
                clientAcceptModel.setPropertyId(propertyId);
                clientAcceptModel.setReason("");
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                Call<ResponseBody> call = retrofitAPIs.dropInventoryApi(tokenaccess, "android", deviceId, clientAcceptModel);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            String responseString = null;
                            if (response.isSuccessful()) {
                                try {
                                    responseString = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 200 && message.equalsIgnoreCase("Property Deleted Successfully")) {
                                        Utils.setSnackBar(parentLayout,message);
                                        context.startActivity(new Intent(context, MainActivity.class));
                                        getActivity().finish();
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        deleteInventory();
                                    } else {
                                        Utils.setSnackBar(parentLayout, message);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utils.showToast(context, t.getLocalizedMessage().toString(),"Failure");
                        Utils.LoaderUtils.dismissLoader();
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inventoryEdit(){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("bedRoomType",bedRoomType);
            bundle.putLong("budget",budget);
            bundle.putString("propertyImage1",propertyImage1);
            bundle.putString("note",note);
            bundle.putString("emailId",emailId);
            bundle.putString("clientMobileNo",clientMobileNo);
            bundle.putString("clientName",clientName);
            bundle.putString("propertyStatus",propertyStatus);
            bundle.putString("propertyType",propertyType);
            bundle.putString("microMarketState",microMarketState);
            bundle.putString("microMarketCity",microMarketCity);
            bundle.putString("microMarketName",microMarketName);
            bundle.putString("postingType",postingType);
            bundle.putString("propertyId",propertyId);
            bundle.putString("propertyImage3",propertyImage3);
            bundle.putString("propertyImage2",propertyImage2);
            bundle.putString("edit_inventory","edit_inventory");
            bundle.putString("subPropertyType",subPropertyType);
            AddInventoryFragment addInventoryFragment = new AddInventoryFragment();
            addInventoryFragment.setArguments(bundle);
            Utils.replaceFragment(getFragmentManager(),addInventoryFragment,R.id.inventory_frag_container,ADD_INVENTORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        deleteInventory();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<String> images;
        LayoutInflater layoutInflater;


        public MyCustomPagerAdapter(Context context, ArrayList<String> images) {
            this.context = context;
            this.images = images;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.item1, container, false);
            PhotoView photoView = itemView.findViewById(R.id.photo_view);
            ImageView imageView = itemView.findViewById(R.id.imageView);
            imageView.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);


            Glide.with(context)
                    .load(images.get(position))
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(imageView);
            container.addView(itemView);

            //listening to image click
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullImageDialog(images.get(position));
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }
    private void setUiPageViewController() {

        try {
            dotsCount = customPagerAdapter.getCount();
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(context);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pager_indicator.addView(dots[i], params);
            }
            if(dotsCount > 0) {
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
    private void fullImageDialog(String imageUrl){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //  dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
            dialog.setContentView(R.layout.item1);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            PhotoView photoView = dialog.findViewById(R.id.photo_view);
            imageView1 = dialog.findViewById(R.id.imageView);
            imageView1.setVisibility(View.GONE);
            photoView.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(imageUrl)
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(photoView);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }
    private void addview(String text) {
        try {
            if(text != null) {
                if (!text.isEmpty()) {
                    try {
                        View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.deal_child, flowLayout, false);
                        TextView deal_textview = layout2.findViewById(R.id.deal_text);
                        deal_textview.setText(text);
                        flowLayout.addView(layout2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String error = e.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
