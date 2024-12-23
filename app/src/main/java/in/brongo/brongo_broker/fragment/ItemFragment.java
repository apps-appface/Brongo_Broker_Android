package in.brongo.brongo_broker.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.activity.Menu_Activity;
import in.brongo.brongo_broker.activity.ReminderActivity;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.other.AllUtils;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements NoInternetTryConnectListener{

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private int status = 50;
    private int apiCode=0;
    private String lead_mobile,propertyId;
    private static ViewListener viewListener1;
    private int screenWidth,screenHeight,remaining=10;
    private ArrayList<String> arrayList,timeList,remaininglist;
    private SharedPreferences pref;
    private LinearLayout mLinearLayout,open_deal_comm_linear2,open_deal_comm_linear1;
    private RatingBar ratingBar;
    private Bundle bundle;
    private String prop_type,commission1,sub_property_type;
    private Button feedBackBtn;
    private boolean isClientRated = false;
    private FlowLayout flowLayout;
    private Context context;
    private TextView comm1,comm2,comm3,comm4,comm5,comm6;


    public static Fragment newInstance(int position, float scale, ArrayList<ApiModel.BuyAndRentModel> arrayList,ViewListener viewListener) {
        ItemFragment itemFragment=new ItemFragment();
        Bundle b = new Bundle();
        b.putInt(POSITON, position);
        b.putFloat(SCALE, scale);
        b.putString("propertyId",arrayList.get(position).getPropertyId());
        b.putInt("lead_matched",arrayList.get(position).getMatchedProperty());
        b.putInt("personal_matched",arrayList.get(position).getMatchedPer());
        b.putInt("builder_matched",arrayList.get(position).getMatchedBui());
        b.putString("lead_name",arrayList.get(position).getClientName());
        b.putFloat("lead_rating",arrayList.get(position).getRating());
        b.putString("lead_plan",arrayList.get(position).getPlanType());
        b.putDouble("lead_commission",arrayList.get(position).getCommission());
        b.putString("lead_mobile",arrayList.get(position).getClientMobileNo());
        b.putString("lead_image",arrayList.get(position).getClientImage());
        b.putString("lead_prop_type",arrayList.get(position).getPropertyType());
        b.putString("lead_posting_type",arrayList.get(position).getPostingType());
        b.putString("lead_address",arrayList.get(position).getMicroMarketName());
        b.putString("lead_sub_prop_type",arrayList.get(position).getSubPropertyType());
        b.putStringArrayList("remainglist",arrayList.get(position).getRemainingStatus());
        b.putStringArrayList("completedlist",arrayList.get(position).getCompletedStatus());
        b.putStringArrayList("timelist",arrayList.get(position).getStatusUpdatedTimes());
        b.putStringArrayList("property_list",arrayList.get(position).getProperty());
        b.putString("meeting_date",arrayList.get(position).getDateOfVisit());
        b.putString("meeting_location",arrayList.get(position).getMeetAt());
        b.putString("meeting_time",arrayList.get(position).getTimeOfVisit());
        b.putString("meeting_notes",arrayList.get(position).getNote());
        b.putBoolean("isBrokerRated",arrayList.get(position).isBrokerRated());
        if(arrayList.get(position).getLatLong().size()>1) {
            b.putDouble("meeting_lat", arrayList.get(position).getLatLong().get(0));
            b.putDouble("meeting_long", arrayList.get(position).getLatLong().get(1));
        }

        //b.putStringArrayList("sitevisitlist",arrayList.get(position).getRemainingstatus());
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
        arrayList = new ArrayList<>();
        timeList = getArguments().getStringArrayList("timelist");
        ArrayList<String> completedlist = getArguments().getStringArrayList("completedlist");
       remaininglist = getArguments().getStringArrayList("remainglist");
        ArrayList<String> keylist = getArguments().getStringArrayList("property_list");
        isClientRated = getArguments().getBoolean("isBrokerRated",false);
        if(completedlist.size() != 0) {
            arrayList.addAll(completedlist);
        }if(remaininglist.size() != 0) {
            arrayList.addAll(remaininglist);
        }
        if(completedlist.size()==0){
            status = 0;
        }else if(remaininglist.size() == 0){
            status = completedlist.size()-1;
        }else if(completedlist.size()>0 && remaininglist.size()>0){
            status = completedlist.size();
        }
        final int position = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);
      propertyId = getArguments().getString("propertyId","");
        final String lead_name = getArguments().getString("lead_name","");
        String lead_plan = getArguments().getString("lead_plan","");
        String lead_image = getArguments().getString("lead_image","");
        int lead_matched = getArguments().getInt("lead_matched",0);
        int personal_matched = getArguments().getInt("personal_matched",0);
        int builder_matched = getArguments().getInt("builder_matched",0);
        final String  lead_prop_type = getArguments().getString("lead_prop_type","");
        prop_type = getArguments().getString("lead_prop_type","").toUpperCase();
        final String lead_posting_type = getArguments().getString("lead_posting_type","");
        //String lead_site = getArguments().getString("lead_site");
     lead_mobile = getArguments().getString("lead_mobile","");
      float lead_rating = getArguments().getFloat("lead_rating",0.0f);
        double lead_commission = getArguments().getDouble("lead_commission",0.0d);
        commission1 = lead_commission+"";
        sub_property_type = getArguments().getString("lead_sub_prop_type","").toUpperCase();
        ScrollView linearLayout = (ScrollView) inflater.inflate(R.layout.fragment_item1, container, false);
        flowLayout = linearLayout.findViewById(R.id.main_deal_flowlayout);
        feedBackBtn = linearLayout.findViewById(R.id.feedBack_btn);
        mLinearLayout = linearLayout.findViewById(R.id.lead_status_linear);
        addLayout(arrayList);
        TextView matching_properties = linearLayout.findViewById(R.id.text_matching_properties);
        TextView matching_properties2 = linearLayout.findViewById(R.id.text_matching_properties2);
        TextView open_deal_name = linearLayout.findViewById(R.id.opendeal_client_name);
        TextView open_deal_commission = linearLayout.findViewById(R.id.open_deal_commission);
        TextView open_deal_clienttype = linearLayout.findViewById(R.id.open_deal_client_type);
        TextView open_deal_plan = linearLayout.findViewById(R.id.lead_plan);
        comm1 = linearLayout.findViewById(R.id.open_deal_comm1);
        comm2 = linearLayout.findViewById(R.id.open_deal_comm2);
        comm3 = linearLayout.findViewById(R.id.open_deal_comm3);
        comm4 = linearLayout.findViewById(R.id.open_deal_comm4);
        comm5 = linearLayout.findViewById(R.id.open_deal_comm5);
        comm6 = linearLayout.findViewById(R.id.open_deal_comm6);
        open_deal_comm_linear1 = linearLayout.findViewById(R.id.open_deal_comm_linear1);
        open_deal_comm_linear2 = linearLayout.findViewById(R.id.open_deal_comm_linear2);
        TextView view_all = linearLayout.findViewById(R.id.landing_viewall);
        foo(view_all,bundle);
        TextView open_deal_id = linearLayout.findViewById(R.id.deal_id);
        LinearLayout open_deal_del_btn = linearLayout.findViewById(R.id.client_drop);
        LinearLayout open_deal_chat_btn = linearLayout.findViewById(R.id.client_chat);
        ImageView open_deal_client_image = linearLayout.findViewById(R.id.client_deal_pic);
        LinearLayout noti_star_linear = linearLayout.findViewById(R.id.noti_star_linear);
        ratingBar = linearLayout.findViewById(R.id.opendeal_ratingBar);
        LinearLayout clientCall = linearLayout.findViewById(R.id.client_call);
        final ProgressBar progressBar = linearLayout.findViewById(R.id.progress);
        Glide.with(context)
                .load(lead_image)
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(open_deal_client_image);
        matching_properties.setText(personal_matched + " Personal matching");
        matching_properties2.setText(builder_matched + " Builder matching");
        setCommissionText(lead_posting_type.toUpperCase());
        open_deal_id.setText("DEAL ID : "+propertyId);
        open_deal_name.setText(lead_name);
        open_deal_plan.setText(lead_plan.toUpperCase());
   //     open_deal_commission.setText(lead_commission + "%");
        open_deal_commission.setVisibility(View.GONE);
        open_deal_clienttype.setText(lead_posting_type.toUpperCase()+"/"+lead_prop_type.toUpperCase());
       String color_back = Utils.getPostingColor(lead_posting_type);
            open_deal_clienttype.setBackgroundColor(Color.parseColor(color_back));
       addview(keylist);

        ratingBar.setRating(lead_rating);
       //setRating(rating);
        clientCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    callClient(lead_mobile,propertyId);
            }
        });
      open_deal_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewListener1 != null && bundle != null ) {
                    viewListener1.clickBtn(position, bundle);
                }
            }
        });
      feedBackBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(viewListener1 != null) {
                  viewListener1.feedBackClick(position, bundle);
              }
          }
      });
        matching_properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Menu_Activity.class);
               bundle.putString("frgToLoad","MatchingPropertyFragment");
                bundle.putString("invenType","personal");
                intent.putExtras(bundle);
               startActivity(intent);
            }
        });
        matching_properties2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Menu_Activity.class);
                bundle.putString("frgToLoad","MatchingPropertyFragment");
                bundle.putString("invenType","builder");
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
        try {
            if(!isClientRated) {
                if(arrayList.size()>0){
                    remaining = arrayList.size()-1-status;
                }
                if (remaining < 3) {
                    feedBackBtn.setVisibility(View.VISIBLE);
                } else {
                    feedBackBtn.setVisibility(View.GONE);
                }
            }
            for (int i = 0; i < arrayList.size(); i++) {
              try {
                    View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.linear_status, mLinearLayout, false);
                    TextView textView = layout2.findViewById(R.id.status_text1);
                    LinearLayout status_text_linear = layout2.findViewById(R.id.status_text_linear);
                    TextView time_textview = layout2.findViewById(R.id.status_time_text);
                    TextView meeting_text = layout2.findViewById(R.id.status_reminder);
                  ImageView meeting_image= layout2.findViewById(R.id.status_reminder_imageview);
                    View view1 = layout2.findViewById(R.id.status_view1);
                    View view2 = layout2.findViewById(R.id.status_view2);
                    View view3 = layout2.findViewById(R.id.status_view3);
                    ImageView tick_view = layout2.findViewById(R.id.tick_view);
                    ImageView circle_view = layout2.findViewById(R.id.circle_view);
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
               if(position1 == 1 && status ==1){
                   meeting_text.setVisibility(View.VISIBLE);
                   meeting_image.setVisibility(View.VISIBLE);
                }else{
                   meeting_text.setVisibility(View.GONE);
                   meeting_image.setVisibility(View.GONE);
                }
                    status_text_linear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position1 <= status) {
                            } else if (position1 == status + 1) {
                                if(position1 == arrayList.size()-1){
                                    if (!isClientRated) {
                                        //Utils.showAlert("","Please give feedback first !",context);
                                        if(viewListener1 != null) {
                                            viewListener1.alert("Please give feedback first !");
                                        }
                                    } else {
                                        feedBackBtn.setVisibility(View.GONE);
                                        statusChangedDialog(position1, arrayList.get(status), arrayList.get(status + 1));
                                    }
                                }else{
                                    statusChangedDialog(position1, arrayList.get(status), arrayList.get(status + 1));
                                }
                            } else {
                                if(viewListener1 != null) {
                                    viewListener1.alert("First select status '" + arrayList.get(status + 1) + "'");
                                }
                            }
                        }
                    });
                  meeting_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ReminderActivity.class);
                            intent.putExtra("prop_id",bundle.getString("propertyId"));
                            intent.putExtra("client_mobile",bundle.getString("lead_mobile"));
                            intent.putExtra("meeting_date",bundle.getString("meeting_date"));
                            intent.putExtra("meeting_time",bundle.getString("meeting_time"));
                            intent.putExtra("meeting_notes",bundle.getString("meeting_notes"));
                            intent.putExtra("meeting_lat",bundle.getDouble("meeting_lat"));
                            intent.putExtra("meeting_long",bundle.getDouble("meeting_long"));
                            intent.putExtra("meeting_location",bundle.getString("meeting_location"));
                            startActivity(intent);
                        }
                    });
                    mLinearLayout.addView(layout2);
                } catch (Exception e) {
                  e.printStackTrace();
                    String error = e.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addview(ArrayList<String> keyList) {
        if(keyList != null && keyList.size()> 0 ) {
            for (int j = 0; j < keyList.size(); j++) {
                try {
                    if(!keyList.get(j).isEmpty()) {
                        View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.deal_child, flowLayout, false);
                        TextView deal_textview = layout2.findViewById(R.id.deal_text);
                        deal_textview.setBackgroundResource(R.drawable.rounded_blue_btn);
                        deal_textview.setTextColor(context.getResources().getColor(R.color.white));
                        deal_textview.setText(keyList.get(j));
                        flowLayout.addView(layout2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String error = e.toString();
                }
            }
        }
        }
    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            screenHeight = displaymetrics.heightPixels;
            screenWidth = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callClient(final String lead_mobile, final String propertyId) {
        try {
            if (Utils.isNetworkAvailable(context)) {
                Utils.LoaderUtils.showLoader(context);
                String client_no = lead_mobile;
                String brokerno = (pref.getString(AppConstants.MOBILE_NUMBER, ""));
                String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
                String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
                ApiModel.KnowlarityModel knowlarityModel = new ApiModel.KnowlarityModel();
                knowlarityModel.setFrom(brokerno);
                knowlarityModel.setTo(client_no);
                knowlarityModel.setDealId(propertyId);
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
                                        try {
                                            startActivity(callIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
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
                                        if(viewListener1 != null) {
                                            viewListener1.alert("please try again");
                                        }
                                    } else {
                                        if(viewListener1 != null) {
                                            viewListener1.alert(message);
                                        }
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
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            if(viewListener1 != null) {
                                viewListener1.alert("please try again");
                            }
                        }else {
                            Utils.showToast(context, t.getLocalizedMessage().toString(), "Failure");
                        }
                    }
                });
            } else {
                Utils.internetDialog(context,this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTryReconnect() {

    }

    public interface ViewListener{
        void clickBtn(int position,Bundle bundle);
        void feedBackClick(int position,Bundle bundle);
        void alert(String message);
        void refreshData();
    }

    private void updateLeadStatus(final int position1){
        try {
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
                                        if(viewListener1 != null) {
                                            viewListener1.alert(message);
                                            viewListener1.refreshData();
                                        }
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
                                        if(viewListener1 != null) {
                                            viewListener1.alert("please try again");
                                        }
                                    } else {
                                        if(viewListener1 != null) {
                                            viewListener1.alert(message);
                                        }
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
                        if (t.getMessage().equals("Too many follow-up requests: 21")) {
                            new AllUtils().getTokenRefresh(context);
                            if(viewListener1 != null) {
                                viewListener1.alert("please try again");
                            }
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


    private void statusChangedDialog(final int position1, String status1, String status2){
        try {
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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_status);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            final Button yes_btn = dialog.findViewById(R.id.status_dialog_btn);
            TextView message_textview = dialog.findViewById(R.id.status_dialog_message_text);
            TextView do_later_textview = dialog.findViewById(R.id.status_dialog_do_later_text);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void foo(TextView landing_viewall, final Bundle bundle) {
        try {
            SpannableString link = makeLinkSpan("View all requirement", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),Menu_Activity.class);
                    intent.putExtra("dealId",bundle.getString("propertyId"));
                    intent.putExtra("postingType",bundle.getString("lead_posting_type"));
                    intent.putExtra("propertyType",bundle.getString("lead_prop_type"));
                    intent.putExtra("clientMobile",bundle.getString("lead_mobile"));
                    intent.putExtra("sub_propertyType",bundle.getString("lead_sub_prop_type"));
                    intent.putExtra("frgToLoad",("requirement_page"));
                    startActivity(intent);
                }
            });
            // Set the TextView's text
            landing_viewall.setText(link);
            // This line makes the link clickable!
            makeLinksFocusable(landing_viewall);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            MovementMethod m = tv.getMovementMethod();
            if ((m == null) || !(m instanceof LinkMovementMethod)) {
                if (tv.getLinksClickable()) {
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
            tv.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            Utils.LoaderUtils.dismissLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setCommissionText(String posting_type){
        try {
            switch(posting_type){
                case "BUY":
                    setBuyCommission();
                    open_deal_comm_linear2.setVisibility(View.VISIBLE);
                    open_deal_comm_linear1.setVisibility(View.GONE);
                    break;
                case "SELL":
                    setSellCommission();
                    open_deal_comm_linear2.setVisibility(View.GONE);
                    open_deal_comm_linear1.setVisibility(View.VISIBLE);
                    break;
                case "RENT":
                    setRentCommission();
                    open_deal_comm_linear2.setVisibility(View.GONE);
                    open_deal_comm_linear1.setVisibility(View.VISIBLE);
                    break;
                case "RENT_OUT":
                    setRentOutCommission();
                    open_deal_comm_linear2.setVisibility(View.GONE);
                    open_deal_comm_linear1.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setBuyCommission(){
        try {
            if(prop_type.equalsIgnoreCase("RESIDENTIAL") || prop_type.equalsIgnoreCase("COMMERCIAL")){
                comm3.setText("New Property");
                comm4.setText("0%");
                comm5.setText("Resale Property");
                comm6.setText(commission1 + "%");
            }else if(prop_type.equalsIgnoreCase("LAND")){
                if(sub_property_type != null){
                    if(sub_property_type.equalsIgnoreCase("RESIDENTIAL_ZONE")){
                        comm3.setText("New Gated property");
                        comm4.setText("0%");
                        comm5.setText("For Land ");
                        comm6.setText(commission1 + "%");
                    }else if(sub_property_type.equalsIgnoreCase("COMMERCIAL_ZONE")){
                        comm1.setText(commission1 + "% Commission");
                        comm2.setVisibility(View.GONE);
                        open_deal_comm_linear2.setVisibility(View.GONE);
                        open_deal_comm_linear1.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setSellCommission(){
        try {
            comm1.setText(commission1 + "% Commission");
            comm2.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setRentCommission(){
        try {
            Float comm = Float.valueOf(commission1);
            if(comm == 100.0f) {
                comm1.setText("1 Month Commission");
                comm2.setVisibility(View.GONE);
            }else{
                int number = (int) ((30*comm)/100);
                comm1.setText(number+" days Commission");
                comm2.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    private void setRentOutCommission(){
        try {
            Float comm = Float.valueOf(commission1);
            if(comm == 100.0f) {
                comm1.setText("1 Month Commission");
                comm2.setVisibility(View.GONE);
            }else{
                int number = (int) ((30*comm)/100);
                comm1.setText(number+" days Commission");
                comm2.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
