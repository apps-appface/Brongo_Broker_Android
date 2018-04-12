package in.brongo.brongo_broker.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.other.NoTokenTryListener;
import in.brongo.brongo_broker.uiwidget.TouchImageView;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementFragment extends Fragment implements NoInternetTryConnectListener,NoTokenTryListener,AllUtils.test{
    private String posting_type,property_type,deal_id,client_mobile,sub_propertyType;
    private LinearLayout req_linear,req_image_linear;
    private Bundle bundle;
    private Context context;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
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
        posting_type = property_type = deal_id = sub_propertyType="";
        if(getArguments() != null){
            bundle = getArguments();
            posting_type = bundle.getString("postingType");
            property_type = bundle.getString("propertyType");
            deal_id = bundle.getString("dealId");
            client_mobile = bundle.getString("clientMobile");
            sub_propertyType = bundle.getString("sub_propertyType");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requirement, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            location=location_area=project_name=prop_type=bhk_type=budget=martial_status=avoid_project=orientation=furnishing=other_req="";
            deallist = new ArrayList<>();
            imageList = new ArrayList<>();
            req_deal_id = view.findViewById(R.id.req_deal_id);
            req_post = view.findViewById(R.id.req_post_type);
            req_linear = view.findViewById(R.id.requirement_linear);
            req_image_linear = view.findViewById(R.id.requirement_image_linear);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("Requirements");
            setView();
            populateList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void populateList(){
        try {
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
                propertyModel.setSubPropertyType(sub_propertyType);
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
                                        openTokenDialog(context);
                                    } else {
                                       Utils.setSnackBar(parentLayout,message);
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
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                           openTokenDialog(context);
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            }else{
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addLayout(ArrayList<ClientDetailsModel.DealObject> arrayList){
        try {
            for(int i=0;i<arrayList.size();i++) {
                if(arrayList.get(i).getContent()!= null && !arrayList.get(i).getContent().isEmpty()) {
                    String text1 = arrayList.get(i).getTitle();
                    String text2 = arrayList.get(i).getContent();
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.requirement_child_one, req_linear, false);
                    TextView text_header = layout2.findViewById(R.id.require_hearder_one);
                    TextView text_child = layout2.findViewById(R.id.require_child_one);
                    text_header.setText(text1);
                    text_child.setText(text2);
                    req_linear.addView(layout2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.toString();
        }
    }
    private void addImages(ArrayList<String> imagelist){
        try {
            for(int i=0;i<imagelist.size();i++) {
                String stringurl = imagelist.get(i);
                if(stringurl != null && !stringurl.isEmpty()) {
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.deal_image_item, req_image_linear, false);
                    ImageView imageview = layout2.findViewById(R.id.deal_image);
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
            e.printStackTrace();
            String error = e.toString();
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
            ImageView property_image = dialog.findViewById(R.id.imageView);
            TouchImageView imgDisplay = dialog.findViewById(R.id.imgDisplay);
            property_image.setVisibility(View.GONE);
            imgDisplay.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(imgDisplay);
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

    @Override
    public void onTryReconnect() {
        populateList();
    }
    private void setView(){
        try {
            req_deal_id.setText("DEAL ID:"+deal_id);
            String backColor = Utils.getPostingColor(posting_type);
            req_post.setBackgroundColor(Color.parseColor(backColor));
            req_post.setText(posting_type.toUpperCase()+"/"+property_type.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryRegenerate() {
        getToken(context);
    }
    private void openTokenDialog(Context context){
        try {
            if(!getActivity().isFinishing()) {
                Utils.tokenDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getToken(Context context){
        try {
            new AllUtils().getToken(context,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessRes(boolean isSuccess) {
        if(isSuccess){
            populateList();
        }else{
            Utils.LoaderUtils.dismissLoader();
            openTokenDialog(context);
        }
    }
}
