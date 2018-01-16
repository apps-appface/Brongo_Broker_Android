package appface.brongo.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import appface.brongo.R;
import appface.brongo.activity.Menu_Activity;
import appface.brongo.activity.ReferActivity;
import appface.brongo.model.ApiModel;
import appface.brongo.model.ClientDetailsModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.uiwidget.FlowLayout;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.DatabaseHandler;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements NoInternetTryConnectListener {

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private int status = 5;
    private static ArrayList<ApiModel.BuyAndRentModel> itemList = new ArrayList<>();
    private static ViewListener viewListener1;
    private int screenWidth;
    private int screenHeight;
    private float rating1;
    private RelativeLayout relativeLayout;
    private ArrayList<String> arrayList;
    private ArrayList<String> timeList;
    private SharedPreferences pref;
    private LinearLayout mLinearLayout;
    private RatingBar ratingBar;
    private Bundle bundle;
    private FlowLayout flowLayout;
    private ProgressDialog pd;
    private Context context;


    public static Fragment newInstance(int position, float scale, ArrayList<ApiModel.BuyAndRentModel> arrayList,ViewListener viewListener) {
        ItemFragment itemFragment=new ItemFragment();
        Bundle b = new Bundle();
        b.putInt(POSITON, position);
        b.putFloat(SCALE, scale);
        b.putString("propertyId",arrayList.get(position).getPropertyId());
        b.putInt("lead_matched",arrayList.get(position).getMatchedProperty());
        b.putString("lead_name",arrayList.get(position).getClientName());
        b.putFloat("lead_rating",arrayList.get(position).getRating());
        b.putString("lead_plan",arrayList.get(position).getPlanType());
        b.putDouble("lead_commission",arrayList.get(position).getCommission());
        b.putString("lead_mobile",arrayList.get(position).getMobileNo());
        b.putString("lead_address",arrayList.get(position).getMicroMarketName());
        b.putString("lead_site",arrayList.get(position).getSiteVisit());
        b.putString("lead_bhk",arrayList.get(position).getBedRoomType());
        b.putString("lead_budget",arrayList.get(position).getBudget());
        b.putString("lead_prop_status",arrayList.get(position).getPropertyStatus());
        b.putString("lead_image",arrayList.get(position).getClientImage());
        b.putString("lead_prop_type",arrayList.get(position).getPropertyType());
        b.putString("lead_posting_type",arrayList.get(position).getPostingType());
        b.putString("lead_sub_prop_type",arrayList.get(position).getSubPropertyType());
        b.putStringArrayList("remainglist",arrayList.get(position).getDealStatus().getRemainingstatus());
        b.putStringArrayList("completedlist",arrayList.get(position).getDealStatus().getCompletedstatus());
        b.putStringArrayList("timelist",arrayList.get(position).getDealStatus().getStatusUpdatedTimes());
        b.putStringArrayList("sitevisitlist",arrayList.get(position).getDealStatus().getRemainingstatus());
        viewListener1 = viewListener;
        itemFragment.setArguments(b);
        return itemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
        context = getActivity();
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        context = getActivity();
        bundle = this.getArguments();
        pd = new ProgressDialog(context, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        arrayList = new ArrayList<>();
        timeList = getArguments().getStringArrayList("timelist");
        ArrayList<String> completedlist = getArguments().getStringArrayList("completedlist");
        ArrayList<String> remaininglist = getArguments().getStringArrayList("remainglist");
        arrayList.addAll(completedlist);
        arrayList.addAll(remaininglist);
        status = completedlist.size()-1;
        final int position = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);
       final String propertyId = getArguments().getString("propertyId");
        final String lead_name = getArguments().getString("lead_name");
        String lead_plan = getArguments().getString("lead_plan");
        String lead_address = getArguments().getString("lead_address");
        String lead_bhk = getArguments().getString("lead_bhk");
        String lead_image = getArguments().getString("lead_image");
        String lead_budget = getArguments().getString("lead_budget");
       // int lead_matched = getArguments().getInt("lead_matched");
        final String  lead_prop_type = getArguments().getString("lead_prop_type");
        final String lead_posting_type = getArguments().getString("lead_posting_type");
        int lead_matched =matchList(lead_posting_type,lead_prop_type);
        String lead_site = getArguments().getString("lead_site");
        String lead_prop_status = getArguments().getString("lead_prop_status");
        final String lead_mobile = getArguments().getString("lead_mobile");
        String lead_sub_prop_type = getArguments().getString("lead_sub_prop_type");
      float lead_rating = getArguments().getFloat("lead_rating");
        double lead_commission = getArguments().getDouble("lead_commission");
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((screenWidth), (screenHeight*2));
       // RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((screenWidth), ((screenHeight*2)));
       /* ScrollView linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_item1, container, false);
        CarouselLinearLayout root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container1);
        RelativeLayout relativeLayout = (RelativeLayout)linearLayout.findViewById(R.id.open_relative1);*/
        ScrollView linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_item1, container, false);
        relativeLayout = (RelativeLayout)linearLayout.findViewById(R.id.open_relative1);
        flowLayout = (FlowLayout)linearLayout.findViewById(R.id.main_deal_flowlayout);
        //relativeLayout.setLayoutParams(layoutParams);
        mLinearLayout = (LinearLayout)linearLayout.findViewById(R.id.lead_status_linear);
        addLayout(arrayList);
        /*if(Utils.isNetworkAvailable(context)) {
            getLead();
        }else{
            Utils.internetDialog(context);
        }*/

        TextView matching_properties = (TextView) linearLayout.findViewById(R.id.text_matching_properties);
        TextView open_deal_name = (TextView) linearLayout.findViewById(R.id.opendeal_client_name);
        TextView open_deal_commission = (TextView) linearLayout.findViewById(R.id.open_deal_commission);
        TextView open_deal_clienttype = (TextView) linearLayout.findViewById(R.id.open_deal_client_type);
        TextView view_all = (TextView)linearLayout.findViewById(R.id.landing_viewall);
        foo(view_all,bundle);
        //TextView open_deal_sitevisit = (TextView) linearLayout.findViewById(R.id.open_deal_visit);
        //TextView open_deal_address = (TextView) linearLayout.findViewById(R.id.open_deal_address);
        TextView open_deal_id = (TextView) linearLayout.findViewById(R.id.deal_id);
        //TextView open_deal_bhk = (TextView) linearLayout.findViewById(R.id.open_deal_bhk);
        //TextView open_deal_budget = (TextView) linearLayout.findViewById(R.id.open_deal_budget);
        //TextView open_deal_propstatus = (TextView) linearLayout.findViewById(R.id.open_deal_prop_status);
        //TextView open_deal_proptype = (TextView) linearLayout.findViewById(R.id.open_deal_prop_type);
        Button open_deal_del_btn = (Button) linearLayout.findViewById(R.id.client_drop);
        Button open_deal_chat_btn = (Button) linearLayout.findViewById(R.id.client_chat);
        ImageView open_deal_client_image = (ImageView) linearLayout.findViewById(R.id.client_deal_pic);
        LinearLayout noti_star_linear = (LinearLayout)linearLayout.findViewById(R.id.noti_star_linear);
        ratingBar = (RatingBar)linearLayout.findViewById(R.id.opendeal_ratingBar);
        Button clientCall = (Button)linearLayout.findViewById(R.id.client_call);
        final ProgressBar progressBar = (ProgressBar)linearLayout.findViewById(R.id.progress);
       /* Glide.with(context).load(lead_image).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(getActivity())).dontAnimate().listener(new RequestListener<String, GlideDrawable>() {
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
        }).into(open_deal_client_image);*/
        Glide.with(context)
                .load(lead_image)
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(open_deal_client_image);
      //  matching_properties1.setText(lead_matched + " matching properties");
        matching_properties.setText(lead_matched + " matching properties");
        open_deal_id.setText("DEAL ID : "+propertyId);
        open_deal_name.setText(lead_name);
        open_deal_commission.setText(lead_commission + "%");
        open_deal_clienttype.setText(lead_posting_type.toUpperCase()+"/"+lead_prop_type.toUpperCase());
       String color_back = Utils.getPostingColor(lead_posting_type);
            open_deal_clienttype.setBackgroundColor(Color.parseColor(color_back));
        //open_deal_sitevisit.setText(lead_site);
        //open_deal_address.setText(lead_address);
      /*  if(lead_bhk.equalsIgnoreCase("")){
            open_deal_bhk.setVisibility(View.GONE);
        }else {
            open_deal_bhk.setText(lead_bhk);
        }
        if(lead_budget.equalsIgnoreCase("")){
            open_deal_budget.setVisibility(View.GONE);
        }else {
            open_deal_budget.setText(lead_budget);
        }
        if(lead_prop_status.equalsIgnoreCase("")){
            open_deal_propstatus.setVisibility(View.GONE);
        }else {
            open_deal_propstatus.setText(lead_prop_status);
        }
        if(lead_sub_prop_type.equalsIgnoreCase("")){
            open_deal_proptype.setVisibility(View.GONE);
        }else {
            open_deal_proptype.setText(lead_sub_prop_type);
        }*/
        String budget = "";
      if(lead_budget != null && !lead_budget.isEmpty()) {
          budget = Utils.stringToInt(lead_budget);
      }
      addview(lead_address);
        addview(budget);
        addview(lead_bhk);
        addview(lead_prop_status);
        addview(lead_sub_prop_type);

        ratingBar.setRating(lead_rating);
       //setRating(rating);
        clientCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    callClient(lead_mobile,propertyId);
            }
        });
        noti_star_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating_dialog();
            }
        });
      open_deal_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setAlphaAnimation(v,context);
                viewListener1.clickBtn(position,bundle);
            }
        });
        matching_properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Menu_Activity.class);
               bundle.putString("frgToLoad","MatchingPropertyFragment");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        open_deal_chat_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.setAlphaAnimation(v,context);
                if (MobiComUserPreference.getInstance(context).isRegistered()) {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.USER_ID, lead_mobile);
                    intent.putExtra(ConversationUIService.DISPLAY_NAME, lead_name); //put it for displaying the title.
                    intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
                    context.startActivity(intent);
                }
            }
        });
        return linearLayout;
    }
    private void addLayout(final ArrayList<String> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
          try {
                View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.linear_status, mLinearLayout, false);
                TextView textView = (TextView) layout2.findViewById(R.id.status_text1);
                LinearLayout status_text_linear = (LinearLayout) layout2.findViewById(R.id.status_text_linear);
                TextView time_textview = (TextView) layout2.findViewById(R.id.status_time_text);
                //TextView reminder_text = (TextView) layout2.findViewById(R.id.status_reminder);
                View view1 = (View) layout2.findViewById(R.id.status_view1);
                View view2 = (View) layout2.findViewById(R.id.status_view2);
                View view3 = (View) layout2.findViewById(R.id.status_view3);
                ImageView tick_view = (ImageView) layout2.findViewById(R.id.tick_view);
                ImageView circle_view = (ImageView)layout2.findViewById(R.id.circle_view);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                textView.setText(arrayList.get(i));
                final int position1 = i;
                if (position1 == 0) {
                    view1.setVisibility(View.INVISIBLE);
                }
                if (position1 == arrayList.size() - 1) {
                    view2.setVisibility(View.INVISIBLE);
                }
                if (position1 < status) {
                    view1.setBackgroundColor(context.getResources().getColor(R.color.status_green));
                    view2.setBackgroundColor(context.getResources().getColor(R.color.status_green));
                    view3.setBackgroundColor(context.getResources().getColor(R.color.status_green));
                    textView.setTextColor(context.getResources().getColor(R.color.status_green));
                    time_textview.setText(timeList.get(position1));
                    tick_view.setVisibility(View.GONE);
                    circle_view.setVisibility(View.GONE);
                } else if (position1 > status) {
                    view1.setBackgroundColor(context.getResources().getColor(R.color.status_red));
                    view2.setBackgroundColor(context.getResources().getColor(R.color.status_red));
                    view3.setBackgroundColor(context.getResources().getColor(R.color.status_red));
                    textView.setTextColor(context.getResources().getColor(R.color.status_red));
                    time_textview.setText("");
                    tick_view.setVisibility(View.GONE);
                    circle_view.setVisibility(View.GONE);
                } else if (position1 == status) {
                    view1.setBackgroundColor(context.getResources().getColor(R.color.status_green));
                    view2.setBackgroundColor(context.getResources().getColor(R.color.status_red));
                    view3.setBackgroundColor(context.getResources().getColor(R.color.status_green));
                    textView.setTextColor(context.getResources().getColor(R.color.status_green));
                    time_textview.setText(timeList.get(position1));
                    tick_view.setVisibility(View.VISIBLE);
                    circle_view.setVisibility(View.VISIBLE);
                }
           /*if(position1 == 1){
                reminder_text.setVisibility(View.VISIBLE);
               reminder_image.setVisibility(View.VISIBLE);
            }else{
                reminder_text.setVisibility(View.GONE);
               reminder_image.setVisibility(View.GONE);
            }*/
                status_text_linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position1 <= status) {
                        } else if (position1 == status + 1) {
                            statusChangedDialog(position1, arrayList.get(status), arrayList.get(status + 1));
                        } else {
                            Utils.showToast(context, "First select status '" + arrayList.get(status + 1) + "'");
                        }
                    }
                });
               /* reminder_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ReminderActivity.class);
                        startActivity(intent);
                    }
                });*/
                mLinearLayout.addView(layout2);
            } catch (Exception e) {
                String error = e.toString();
            }
        }
    }
    private void addview(String text) {
        if(text != null) {
            if (!text.isEmpty()) {
                try {
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.deal_child, flowLayout, false);
                    TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                    deal_textview.setBackgroundResource(R.drawable.rounded_blue_btn);
                    deal_textview.setTextColor(context.getResources().getColor(R.color.white));
                    deal_textview.setText(text);
                    flowLayout.addView(layout2);
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
    }
    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }
    private void callClient(final String lead_mobile, final String propertyId) {
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            String client_no = lead_mobile;
            String brokerno = (pref.getString(AppConstants.MOBILE_NUMBER, ""));
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            ApiModel.KnowlarityModel knowlarityModel = new ApiModel.KnowlarityModel();
            knowlarityModel.setFrom(brokerno);
            knowlarityModel.setTo(client_no);
            knowlarityModel.setPropertyId(propertyId);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());

            Call<ResponseBody> call = retrofitAPIs.callKnowlarityApi(tokenaccess, "android", deviceId, knowlarityModel);
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
                                if (statusCode == 200 && message.equalsIgnoreCase("You Can Processed With Call")) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + "+919590224224"));
                                    startActivity(callIntent);
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
                                    new AllUtils().getTokenRefresh(context);
                                    callClient(lead_mobile, propertyId);
                                } else {
                                    Utils.showToast(context, message);
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Toast.makeText(context, "some error occured", Toast.LENGTH_SHORT);
                }
            });
        } else {
            Utils.internetDialog(context,this);
        }
    }

    @Override
    public void onTryReconnect() {

    }

    public interface ViewListener{
        void clickBtn(int position,Bundle bundle);
    }

    private void updateLeadStatus(final int position1){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ClientDetailsModel.LeadStatusModel leadStatusModel = new ClientDetailsModel.LeadStatusModel();
            leadStatusModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            leadStatusModel.setClientMobileNo(bundle.getString("lead_mobile"));
            leadStatusModel.setPropertyId(bundle.getString("propertyId"));
            leadStatusModel.setCommission(bundle.getDouble("lead_commission"));
            leadStatusModel.setRegDate("");
            leadStatusModel.setPostingType(bundle.getString("lead_posting_type").toUpperCase());
            leadStatusModel.setStatus(position1 + 1);
            leadStatusModel.setAcceptedProperty("");
            leadStatusModel.setAcceptedPropertyName("");
            leadStatusModel.setSiteVisitName("");
            leadStatusModel.setMeetAt("");
            leadStatusModel.setDateOfVisit("");
            leadStatusModel.setPropertyName("");
            leadStatusModel.setVisitingPropertyId("");
            leadStatusModel.setTimeOfVisit("");
            leadStatusModel.setRemainder("");
            leadStatusModel.setReminder("");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.updateLeadStatusApi(tokenaccess, "android", deviceId, leadStatusModel);
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
                                if (statusCode == 200 && message.equalsIgnoreCase("Lead Status Updated Successfully")) {
                                    Utils.showToast(context, message);
                                    status = position1;
                                    timeList.add("NOW");
                                    mLinearLayout.removeAllViews();
                                    addLayout(arrayList);
                                    //setView();
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
                                    new AllUtils().getTokenRefresh(context);
                                    updateLeadStatus(position1);
                                } else {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void rating_dialog(){
        rating1 = bundle.getFloat("lead_rating");
        final String posting = bundle.getString("lead_posting_type");
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> buy_sell_list1 = new ArrayList<String>(Arrays.asList("Delayed Commission", "Didn't pay my commission", "Difficult to reach Client","Always late for meeting","Takes time to make decision"));
        final ArrayList<String> buy_sell_list2 = new ArrayList<String>(Arrays.asList("Treated me profession","clear about Requirement","Always on time","Always reachable","paid commission on time"));
        final ArrayList<String> buy_sell_list3 = new ArrayList<String>(Arrays.asList("6 Star service", "Open for suggestions","Great Attitude","Organised","Clear about Requirements"));
        final ArrayList<String> rent_list1 = new ArrayList<String>(Arrays.asList("unorganised", "Not clear about the requirement", "Difficult to reach Client","Always late for meeting","Takes time to make decision"));
        final ArrayList<String> rent_list2 = new ArrayList<String>(Arrays.asList("Treated me profession","clear about Requirement","Always on time","Always reachable","organised"));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawer_background);
/*        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);*/
        dialog.setContentView(R.layout.dialog_client_rating);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ImageView rating_client_image = (ImageView) dialog.findViewById(R.id.client_image_rating);
        RatingBar dialog_ratingbar = (RatingBar)dialog.findViewById(R.id.rating_dialog_ratingBar);
        TextView ratimg_client_name = (TextView)dialog.findViewById(R.id.client_name_rating);
        TextView ratimg_client_address = (TextView)dialog.findViewById(R.id.client_address_rating);
      //  TextView rating_client_plan = (TextView)dialog.findViewById(R.id.client_rating_plan);
        Button dialog_submit = (Button)dialog.findViewById(R.id.client_rating_btn);
        final CheckBox rating_check1 = (CheckBox)dialog.findViewById(R.id.rating_check1);
        final CheckBox rating_check2 = (CheckBox)dialog.findViewById(R.id.rating_check2);
        final CheckBox rating_check3 = (CheckBox)dialog.findViewById(R.id.rating_check3);
        final CheckBox rating_check4 = (CheckBox)dialog.findViewById(R.id.rating_check4);
        final CheckBox rating_check5 = (CheckBox)dialog.findViewById(R.id.rating_check5);
        if(rating1 < 1.0f){
            dialog_ratingbar.setRating(1.0f);
        }else{
            dialog_ratingbar.setRating(rating1);
        }
        dialog_ratingbar.setRating(rating1);
        ratimg_client_name.setText(bundle.getString("lead_name"));
        //rating_client_plan.setText(bundle.getString("lead_plan"));
        ratimg_client_address.setText(bundle.getString("lead_address"));
       /* Glide.with(context).load(bundle.getString("lead_image")).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(getActivity())).dontAnimate().into(rating_client_image);*/
        Glide.with(context)
                .load(bundle.getString("lead_image"))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(rating_client_image);
        if(posting.equalsIgnoreCase("Sell") || posting.equalsIgnoreCase("Buy")){
            if(rating1<3.0f){
                rating_check1.setText(buy_sell_list1.get(0));
                rating_check2.setText(buy_sell_list1.get(1));
                rating_check3.setText(buy_sell_list1.get(2));
                rating_check4.setText(buy_sell_list1.get(3));
                rating_check5.setText(buy_sell_list1.get(4));
            }else if(rating1 >= 3.0f && rating1 <5.0f){
                rating_check1.setText(buy_sell_list2.get(0));
                rating_check2.setText(buy_sell_list2.get(1));
                rating_check3.setText(buy_sell_list2.get(2));
                rating_check4.setText(buy_sell_list2.get(3));
                rating_check5.setText(buy_sell_list2.get(4));
            } else if(rating1 == 5.0f){
                rating_check1.setText(buy_sell_list3.get(0));
                rating_check2.setText(buy_sell_list3.get(1));
                rating_check3.setText(buy_sell_list3.get(2));
                rating_check4.setText(buy_sell_list3.get(3));
                rating_check5.setText(buy_sell_list3.get(4));
            }
        }else if(posting.equalsIgnoreCase("rent") || posting.equalsIgnoreCase("rent_out")){
            if(rating1<3.0f){
                rating_check1.setText(rent_list1.get(0));
                rating_check2.setText(rent_list1.get(1));
                rating_check3.setText(rent_list1.get(2));
                rating_check4.setText(rent_list1.get(3));
                rating_check5.setText(rent_list1.get(4));
            }else if(rating1 >= 3.0f && rating1 <5.0f){
                rating_check1.setText(rent_list2.get(0));
                rating_check2.setText(rent_list2.get(1));
                rating_check3.setText(rent_list2.get(2));
                rating_check4.setText(rent_list2.get(3));
                rating_check5.setText(rent_list2.get(4));
            } else if(rating1 == 5.0f){
                rating_check1.setText(buy_sell_list3.get(0));
                rating_check2.setText(buy_sell_list3.get(1));
                rating_check3.setText(buy_sell_list3.get(2));
                rating_check4.setText(buy_sell_list3.get(3));
                rating_check5.setText(buy_sell_list3.get(4));
            }
        }
        dialog_ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating<1.0f){
                    ratingBar.setRating(1.0f);
            }
                arrayList.clear();
                rating1 = rating;
                if(posting.equalsIgnoreCase("Sell") || posting.equalsIgnoreCase("Buy")){
                    if(rating1<3.0){
                        rating_check1.setText(buy_sell_list1.get(0));
                        rating_check2.setText(buy_sell_list1.get(1));
                        rating_check3.setText(buy_sell_list1.get(2));
                        rating_check4.setText(buy_sell_list1.get(3));
                        rating_check5.setText(buy_sell_list1.get(4));
                    }else if(rating1 >= 3.0 && rating1 <5.0){
                        rating_check1.setText(buy_sell_list2.get(0));
                        rating_check2.setText(buy_sell_list2.get(1));
                        rating_check3.setText(buy_sell_list2.get(2));
                        rating_check4.setText(buy_sell_list2.get(3));
                        rating_check5.setText(buy_sell_list2.get(4));
                    } else if(rating1 == 5.0){
                        rating_check1.setText(buy_sell_list3.get(0));
                        rating_check2.setText(buy_sell_list3.get(1));
                        rating_check3.setText(buy_sell_list3.get(2));
                        rating_check4.setText(buy_sell_list3.get(3));
                        rating_check5.setText(buy_sell_list3.get(4));
                    }
                }else if(posting.equalsIgnoreCase("rent") || posting.equalsIgnoreCase("rent_out")){
                    if(rating1 == 1.0 || rating1 == 2.0){
                        rating_check1.setText(rent_list1.get(0));
                        rating_check2.setText(rent_list1.get(1));
                        rating_check3.setText(rent_list1.get(2));
                        rating_check4.setText(rent_list1.get(3));
                        rating_check5.setText(rent_list1.get(4));
                    }else if(rating1 == 3.0 || rating1 == 4.0){
                        rating_check1.setText(rent_list2.get(0));
                        rating_check2.setText(rent_list2.get(1));
                        rating_check3.setText(rent_list2.get(2));
                        rating_check4.setText(rent_list2.get(3));
                        rating_check5.setText(rent_list2.get(4));
                    } else if(rating1 == 5.0){
                        rating_check1.setText(buy_sell_list3.get(0));
                        rating_check2.setText(buy_sell_list3.get(1));
                        rating_check3.setText(buy_sell_list3.get(2));
                        rating_check4.setText(buy_sell_list3.get(3));
                        rating_check5.setText(buy_sell_list3.get(4));
                    }
                }
            }
        });
        dialog_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayList.size()>0) {
                        updateRating(rating1, arrayList, dialog);
                }else{
                    Utils.showToast(context,"Select atleast 1 comment");
                }

            }
        });

        rating_check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check4.getText().toString());
                }else{
                    if(arrayList.contains(rating_check4.getText().toString())){
                        arrayList.remove(rating_check4.getText().toString());
                    }
                }
            }
        });
        rating_check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check1.getText().toString());
                }else{
                    if(arrayList.contains(rating_check1.getText().toString())){
                        arrayList.remove(rating_check1.getText().toString());
                    }
                }
            }
        });
        rating_check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check2.getText().toString());
                }else{
                    if(arrayList.contains(rating_check2.getText().toString())){
                        arrayList.remove(rating_check2.getText().toString());
                    }
                }
            }
        });
        rating_check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check3.getText().toString());
                }else{
                    if(arrayList.contains(rating_check3.getText().toString())){
                        arrayList.remove(rating_check3.getText().toString());
                    }
                }
            }
        });
        rating_check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    arrayList.add(rating_check5.getText().toString());
                }else{
                    if(arrayList.contains(rating_check5.getText().toString())){
                        arrayList.remove(rating_check5.getText().toString());
                    }
                }
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.show();
    }
    private void updateRating(final float rating, final ArrayList<String> arrayList, final Dialog dialog){
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.RatingModel ratingModel = new ApiModel.RatingModel();
            ratingModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            ratingModel.setClientMobileNo(bundle.getString("lead_mobile"));
            ratingModel.setRating(rating);
            ratingModel.setReview(arrayList);
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            Call<ResponseBody> call = retrofitAPIs.updateRatingApi(tokenaccess, "android", deviceId, ratingModel);
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
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 200 && message.equalsIgnoreCase("Thanks For Your Valuable Feedback")) {
                                    ratingBar.setRating(rating);
                                    dialog.dismiss();
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                pd.dismiss();
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    updateRating(rating, arrayList, dialog);
                                } else {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
            }
    }

    private void statusChangedDialog(final int position1, String status1, String status2){
        SpannableStringBuilder span_status1 = Utils.convertToSpannableString(status1,0,status1.length(),"black");
        SpannableStringBuilder span_status2 = Utils.convertToSpannableString(status2,0,status2.length(),"black");
        SpannableString content = new SpannableString("I’ll do it later");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        String message = "Do you want to change your status from ‘";
        message = message + span_status1;
        message = message.concat("'to ‘");
        message = message + span_status2;
        message = message.concat("’?");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_status);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final Button yes_btn = (Button)dialog.findViewById(R.id.status_dialog_btn);
        TextView message_textview = (TextView)dialog.findViewById(R.id.status_dialog_message_text);
        TextView do_later_textview = (TextView)dialog.findViewById(R.id.status_dialog_do_later_text);
        do_later_textview.setText(content);
        message_textview.setText(message);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setAlphaAnimation(yes_btn,context);
                updateLeadStatus(position1);
                dialog.dismiss();
            }
        });
        do_later_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private int matchList(String posting_type,String property_type){
        int count = 0;
        DatabaseHandler db = new DatabaseHandler(context);
        Cursor cursor = db.getListItem();
        if (cursor != null) {
            cursor.moveToNext();
            for(int i =0;i<cursor.getCount();i++) {
                String post_type = cursor.getString(cursor.getColumnIndex("postingtype"));
                String prop_type = cursor.getString(cursor.getColumnIndex("propertytype"));
                if (Utils.match_deal(posting_type, post_type, property_type, prop_type)) {
                    count++;
                }
                cursor.moveToNext();
            }
        }
        return count;
    }
    private void foo(TextView landing_viewall, final Bundle bundle) {
        SpannableString link = makeLinkSpan("View all requirement", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Menu_Activity.class);
                intent.putExtra("dealId",bundle.getString("propertyId"));
                intent.putExtra("postingType",bundle.getString("lead_posting_type"));
                intent.putExtra("propertyType",bundle.getString("lead_prop_type"));
                intent.putExtra("clientMobile",bundle.getString("lead_mobile"));
                intent.putExtra("frgToLoad",("requirement_page"));
                startActivity(intent);
            }
        });
        // Set the TextView's text
        landing_viewall.setText(link);
        // This line makes the link clickable!
        makeLinksFocusable(landing_viewall);
    }

/*
 * Methods used above.
 */

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appColor)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        tv.setHighlightColor(Color.TRANSPARENT);
    }
/*
 * ClickableString class
 */

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;
        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.LoaderUtils.dismissLoader();
    }

}
