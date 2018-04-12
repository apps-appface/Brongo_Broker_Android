package in.brongo.brongo_broker.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.WalkThroughActivity;
import in.brongo.brongo_broker.adapter.SubmenuAdapter;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.other.AllUtils;
import in.brongo.brongo_broker.other.NoInternetTryConnectListener;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.RecyclerItemClickListener;
import in.brongo.brongo_broker.util.RetrofitAPIs;
import in.brongo.brongo_broker.util.RetrofitBuilders;
import in.brongo.brongo_broker.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.brongo.brongo_broker.util.AppConstants.FRAGMENT_TAGS.CONTACT;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment implements NoInternetTryConnectListener {
    private Context context;
    private ArrayList<String> arrayList;
    private Toolbar toolbar;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private RelativeLayout parentLayout;
    private SharedPreferences pref;

    public SupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_support, container, false);
        context = getActivity();
        parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
        arrayList = new ArrayList<String>(Arrays.asList("Take me to the app walkthrough", "Contact Brongo Customer Support", "I want to unsubscribe"));
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        RecyclerView support_recycle = view.findViewById(R.id.support_recycle);
        edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Support");
        LinearLayoutManager verticalmanager = new LinearLayoutManager(context, 0, false);
        verticalmanager.setOrientation(LinearLayoutManager.VERTICAL);
        support_recycle.setLayoutManager(verticalmanager);
        SubmenuAdapter legalAdapter = new SubmenuAdapter(context,arrayList);
        support_recycle.setAdapter(legalAdapter);
        support_recycle.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                changePage(position);
            }
        }));
        return view;
    }
    private void changePage(int position){
        try {
            switch(position){
                case 0:
                    Intent intent = new Intent(getActivity(), WalkThroughActivity.class);
                    intent.putExtra("from_activity","menu");
                    context.startActivity(intent);
                    break;
                case 1:
                    ContactFragment contactFragment = new ContactFragment();
                    Utils.replaceFragment(getFragmentManager(),contactFragment,R.id.inventory_frag_container,CONTACT);
                    break;
                case 2:
                    unSubscribe();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void unsubscribeDialog(Context context){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.unsubscribe_dialog);
            final ImageView cross_btn = dialog.findViewById(R.id.unsubscribe_dialog_close);
            final Button got_it_btn = dialog.findViewById(R.id.unsubscribe_dialog_btn);
            cross_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            got_it_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void unSubscribe() {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
                ApiModel.UnsubscribeModel unsubscribeModel = new  ApiModel.UnsubscribeModel();
                String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                unsubscribeModel.setMobileNo(mobileNo);
                unsubscribeModel.setMsg("");
                unsubscribeModel.setPlanType(pref.getString(AppConstants.PLAN_TYPE,""));
                Call<ApiModel.ResponseModel> call = retrofitAPIs.unSubscribeApi(tokenaccess, "android", deviceId, unsubscribeModel);
                call.enqueue(new Callback<ApiModel.ResponseModel>() {
                    @Override
                    public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                        Utils.LoaderUtils.dismissLoader();
                        if (response != null) {
                            if (response.isSuccessful()) {
                                ApiModel.ResponseModel responseModel = response.body();
                                int statusCode = responseModel.getStatusCode();
                                String message = responseModel.getMessage();
                                if (statusCode == 200) {
                                  unsubscribeDialog(context);
                                }
                            } else {
                                String responseString = null;
                                try {
                                    responseString = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    String message = jsonObject.optString("message");
                                    int statusCode = jsonObject.optInt("statusCode");
                                    if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                        new AllUtils().getTokenRefresh(context);
                                        Utils.setSnackBar(parentLayout,"please try again");
                                    } else {
                                        Utils.setSnackBar(parentLayout,message);
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel.ResponseModel> call, Throwable t) {
                        Utils.LoaderUtils.dismissLoader();
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            Utils.setSnackBar(parentLayout,"please try again");
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            } else {
                Utils.internetDialog(context, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {
        unSubscribe();
    }
}
