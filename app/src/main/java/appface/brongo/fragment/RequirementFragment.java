package appface.brongo.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ClientDetailsModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementFragment extends Fragment implements NoInternetTryConnectListener{
    private String posting_type,property_type,deal_id,client_mobile;
    private LinearLayout req_linear,req_image_linear;
    private Bundle bundle;
    private Context context;
    private Toolbar toolbar;
    ArrayList<ClientDetailsModel.DealObject> deallist;
    ArrayList<String> imageList ;
    private SharedPreferences pref;
    private ImageView edit_icon,delete_icon,add_icon;
    private ImageView propertyImage;
    private TextView req_deal_id,req_post,toolbar_title;
    private String location,location_area,project_name,prop_type,bhk_type,budget,martial_status,avoid_project,orientation,furnishing,other_req;

    public RequirementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        posting_type = property_type = deal_id = "";
        if(getArguments() != null){
            bundle = getArguments();
            posting_type = bundle.getString("postingType");
            property_type = bundle.getString("propertyType");
            deal_id = bundle.getString("dealId");
            client_mobile = bundle.getString("clientMobile");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requirement, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        location=location_area=project_name=prop_type=bhk_type=budget=martial_status=avoid_project=orientation=furnishing=other_req="";
        deallist = new ArrayList<>();
        imageList = new ArrayList<>();
        req_deal_id = (TextView)view.findViewById(R.id.req_deal_id);
        req_post = (TextView)view.findViewById(R.id.req_post_type);
        req_linear = (LinearLayout)view.findViewById(R.id.requirement_linear);
        req_image_linear = (LinearLayout)view.findViewById(R.id.requirement_image_linear);
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon =(ImageView) getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Requirements");
        setView();
       populateList();
    }
    private void populateList(){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            ClientDetailsModel.PropertyModel propertyModel = new ClientDetailsModel.PropertyModel();
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            propertyModel.setBrokerMobileNo(mobileNo);
            propertyModel.setPostingType(posting_type);
            propertyModel.setPropertyId(deal_id);
            propertyModel.setPropertyType(property_type);
            propertyModel.setClientMobileNo(client_mobile);
            Call<ClientDetailsModel.DealModel> call = retrofitAPIs.fetchDealApi(tokenaccess, "android", deviceId, propertyModel);
            call.enqueue(new Callback<ClientDetailsModel.DealModel>() {
                @Override
                public void onResponse(Call<ClientDetailsModel.DealModel> call, Response<ClientDetailsModel.DealModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        if (response.isSuccessful()) {
                            ClientDetailsModel.DealModel dealModel = response.body();
                            int statusCode = dealModel.getStatusCode();
                            String message = dealModel.getMessage();
                            if (statusCode == 200) {
                                ArrayList<ClientDetailsModel.DealObject> list = dealModel.getData().get(0).getContent();
                                ArrayList<String> image_list = dealModel.getData().get(0).getImages();
                                if (list.size() != 0) {
                                    deallist.clear();
                                   deallist.addAll(list);
                                    addLayout(deallist);
                                }
                                if(image_list.size()>0){
                                    imageList.clear();
                                    imageList.addAll(image_list);
                                    addImages(imageList);
                                }
                            /*if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                            }
                        } else {
                            String responseString = null;
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                               populateList();
                                } else {
                                    Utils.showToast(context,message);
                                }
                           /* if(pd.isShowing()) {
                                pd.dismiss();
                            }*/
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<ClientDetailsModel.DealModel> call, Throwable t) {
                    Toast.makeText(context, "Some Problem Occured", Toast.LENGTH_SHORT).show();
                    Utils.LoaderUtils.dismissLoader();
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void addLayout(ArrayList<ClientDetailsModel.DealObject> arrayList){
        try {
            for(int i=0;i<arrayList.size();i++) {
                if(arrayList.get(i).getContent()!= null && !arrayList.get(i).getContent().isEmpty()) {
                    String text1 = arrayList.get(i).getTitle();
                    String text2 = arrayList.get(i).getContent();
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.requirement_child_one, req_linear, false);
                    TextView text_header = (TextView) layout2.findViewById(R.id.require_hearder_one);
                    TextView text_child = (TextView) layout2.findViewById(R.id.require_child_one);
                    text_header.setText(text1);
                    text_child.setText(text2);
                    req_linear.addView(layout2);
                }
            }
        } catch (Exception e) {
            String error = e.toString();
        }
    }
    private void addImages(ArrayList<String> imagelist){
        try {
            for(int i=0;i<imagelist.size();i++) {
                String stringurl = imagelist.get(i);
                if(stringurl != null && !stringurl.isEmpty()) {
                        stringurl = Utils.getImageUrl(stringurl,pref);
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.deal_image_item, req_image_linear, false);
                    ImageView imageview = (ImageView) layout2.findViewById(R.id.deal_image);
                    Glide.with(context).load(stringurl).apply(CustomApplicationClass.getPropertyImage(true)).into(imageview);
                    final String finalStringurl = stringurl;
                    imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fullImageDialog(finalStringurl);
                        }
                    });
                    req_image_linear.addView(layout2);
                }
            }
        } catch (Exception e) {
            String error = e.toString();
        }
    }
    private void fullImageDialog(String imageUrl){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
        dialog.setContentView(R.layout.item1);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        PhotoView photoView = (PhotoView)dialog.findViewById(R.id.photo_view);
        ImageView property_image = (ImageView) dialog.findViewById(R.id.imageView);
        property_image.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);
       /* Glide.with(context).load(imageUrl).placeholder(R.drawable.no_image).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);*/
        Glide.with(context)
                .load(imageUrl)
                .apply(CustomApplicationClass.getPropertyImage(true))
                .into(photoView);
        dialog.show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        populateList();
    }
    private void setView(){
        req_deal_id.setText("DEAL ID:"+deal_id);
        String backColor = Utils.getPostingColor(posting_type);
        req_post.setBackgroundColor(Color.parseColor(backColor));
        req_post.setText(posting_type.toUpperCase()+"/"+property_type.toUpperCase());
    }
}
