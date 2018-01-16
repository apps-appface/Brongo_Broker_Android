package appface.brongo.adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.activity.MainActivity;
import appface.brongo.activity.PushAlertActivity;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.uiwidget.FlowLayout;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 10/12/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.EmployeeViewHolder> implements NoInternetTryConnectListener{
    private Context context;
    private ArrayList<ApiModel.NotificationChildModel> arrayList;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 15;
    private boolean loading,isVisible;
    private RelativeLayout parentLinear;
    private CallListener callListener;
    private SharedPreferences pref;
    private int unread;
    private OnLoadMoreListener onLoadMoreListener;

    public NotificationAdapter(Context context, final ArrayList<ApiModel.NotificationChildModel> arrayList, RecyclerView recyclerView,CallListener callListener) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
        this.unread = unread;
        this.recyclerView = recyclerView;
        this.callListener = callListener;
        pref = context.getSharedPreferences(AppConstants.PREF_NAME, 0);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                                lastVisibleItem = arrayList.size();
                            }
                        }
                    });
        }
    }

    @Override
    public NotificationAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.notification_item, parent, false);
        NotificationAdapter.EmployeeViewHolder holder = new NotificationAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.EmployeeViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (arrayList.get(position).isRead()) {
            parentLinear.setBackgroundResource(R.color.white);
        } else {
            parentLinear.setBackgroundResource(R.color.notification_color);
        }
        if (arrayList.get(position).getAlertType().equalsIgnoreCase("BUILDER_POSTING")) {
            String budget = String.valueOf(arrayList.get(position).getBudgetRange());
            budget = Utils.stringToInt(budget);
            addView(arrayList.get(position).getProjectName(),holder.notification_flowlayout);
            addView(arrayList.get(position).getLocation(),holder.notification_flowlayout);
            addView(arrayList.get(position).getProjectType(),holder.notification_flowlayout);
            addView(arrayList.get(position).getProjectStatus(),holder.notification_flowlayout);
            addView((arrayList.get(position).getCommission()+"% Commission"),holder.notification_flowlayout);
            addView(budget,holder.notification_flowlayout);
            if(arrayList.get(position).getStatus().isEmpty()) {
                holder.noti_view_linear.setVisibility(View.VISIBLE);
            }else{
                holder.noti_view_linear.setVisibility(View.GONE);
            }
        } else {
            holder.noti_view_linear.setVisibility(View.GONE);
            holder.notification_flowlayout.setVisibility(View.GONE);
        }
        String string1 = arrayList.get(position).getClientName() + ":" + arrayList.get(position).getMessage();
        SpannableStringBuilder str = Utils.convertToSpannableString(string1, 0, arrayList.get(position).getClientName().length(), "black");
        holder.content_text.setText(str);
        holder.noti_time.setText(arrayList.get(position).getDays());
        Glide.with(context)
                .load(arrayList.get(position).getClientProfile().toString())
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(holder.noti_image);
      /*  holder.builder_know_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View)parentLinear;
                projectDialog(view,position);
            }
        });*/
        holder.view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinear.performClick();
                    holder.notification_flowlayout.setVisibility(View.VISIBLE);
                holder.view_btn.setVisibility(View.GONE);
                holder.proceed_btn.setVisibility(View.VISIBLE);

            }
        });
        holder.proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinear.performClick();
                tc_dialog(position);
            }
        });
        parentLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!arrayList.get(position).isRead()) {
                    readNotification(position, v);
                }
            }
        });
        holder.reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLinear.performClick();
                builderRejectApi(position);
            }
        });
    }

    public void setLoaded(boolean loaded) {
        loading = loaded;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onTryReconnect() {

    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView content_text, noti_time, noti_id,builder_know_btn;
        Button view_btn,proceed_btn,reject_btn;
        ImageView noti_image;
        FlowLayout notification_flowlayout;
        LinearLayout noti_view_linear;
        ProgressBar progressBar;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            content_text = (TextView) itemView.findViewById(R.id.notification_content);
            noti_time = (TextView) itemView.findViewById(R.id.notification_time);
            noti_image = (ImageView) itemView.findViewById(R.id.notification_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.noti_progress);
            notification_flowlayout = (FlowLayout)itemView.findViewById(R.id.noti_flowlayout);
            parentLinear = (RelativeLayout) itemView.findViewById(R.id.notification_parent_linear);
            builder_know_btn = (TextView) itemView.findViewById(R.id.know_more);
            view_btn = (Button) itemView.findViewById(R.id.notification_view);
            proceed_btn = (Button) itemView.findViewById(R.id.notification_proceed);
            reject_btn = (Button) itemView.findViewById(R.id.notification_reject);
            noti_view_linear = (LinearLayout) itemView.findViewById(R.id.builder_view_linear);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    private void readNotification(final int position, final View view) {
        if(Utils.isNetworkAvailable(context)) {
            //Utils.LoaderUtils.showLoader(context);
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            String mobileNo = pref.getString(AppConstants.MOBILE_NUMBER, "");
            String id = arrayList.get(position).getId();
            Call<ResponseBody> call = retrofitAPIs.readNotificationApi(tokenaccess, "android", deviceId, mobileNo, id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = null;
                            try {
                                responseString = response.body().string();
                                jsonObject = new JSONObject(responseString);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            int statusCode = jsonObject.optInt("statusCode");
                            String message = jsonObject.optString("message");
                            if (statusCode == 200 && message.equalsIgnoreCase("Updated Successfully")) {
                                arrayList.get(position).setRead(true);
                                view.setBackgroundResource(R.color.white);
                                if(!(arrayList.get(position).getAlertType().equalsIgnoreCase("BUILDER_POSTING"))){
                                    notifyDataSetChanged();
                                }

                            }
                            // referAdapter.notifyDataSetChanged();
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                int statusCode = jsonObject.optInt("statusCode");
                                String message = jsonObject.optString("message");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    readNotification(position, view);
                                } else {
                                    Utils.showToast(context, message);
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
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                /*if(pd.isShowing()) {
                    pd.dismiss();
                }*/
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }

    public interface CallListener{
        void callBtnClick(String phone,String propertyId);
    }

    private void accept_builder(final int position){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.BuilderAcceptModel builderAcceptModel = new ApiModel.BuilderAcceptModel();
            builderAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            builderAcceptModel.setPropertyId(arrayList.get(position).getPropertyId());
            builderAcceptModel.setUserId(arrayList.get(position).getUserId());
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ApiModel.ResponseModel> call = retrofitAPIs.acceptBuilderApi(tokenaccess, "android", deviceId, builderAcceptModel);
            call.enqueue(new Callback<ApiModel.ResponseModel>() {
                @Override
                public void onResponse(Call<ApiModel.ResponseModel> call, Response<ApiModel.ResponseModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.ResponseModel responseModel = response.body();
                            int statusCode = responseModel.getStatusCode();
                            String message = responseModel.getMessage();
                            if (statusCode == 200 && message.equalsIgnoreCase("Builder And Broker Connection Is Established")) {
                                arrayList.get(position).setStatus("accept");
                                notifyDataSetChanged();
                                Utils.showToast(context, message);
                            }
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    accept_builder(position);
                                }else{
                                    Utils.showToast(context, message);
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
                    Utils.showToast(context, t.getMessage().toString());
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }
    private void builderRejectApi(final int position){
        if (Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ClientAcceptModel clientAcceptModel = new ApiModel.ClientAcceptModel();
            clientAcceptModel.setClientMobileNo(arrayList.get(position).getUserId());
            clientAcceptModel.setBrokerMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            clientAcceptModel.setPostingType("");
            clientAcceptModel.setPropertyId(arrayList.get(position).getPropertyId());
            clientAcceptModel.setReason("");
            clientAcceptModel.setPostedUser("builder");
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ResponseBody> call = retrofitAPIs.rejectLeadApi(tokenaccess, "android", deviceId, clientAcceptModel);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response != null) {
                        Utils.LoaderUtils.dismissLoader();
                        String responseString = null;
                        if (response.isSuccessful()) {
                            try {
                                responseString = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 200 ) {
                                    arrayList.remove(position);
                                    notifyDataSetChanged();
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
                                    builderRejectApi(position);
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

    private void tc_dialog(final int position){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_tc);
            //dialog.setCanceledOnTouchOutside(false);
            // dialog.setCancelable(false);
            final ImageView cross_btn = (ImageView) dialog.findViewById(R.id.tc_close_btn);
            final Button accept_btn = (Button)dialog.findViewById(R.id.tcDialog_accept);
        TextView commission_text = (TextView)dialog.findViewById(R.id.tcDialog_commission);
        if(arrayList.get(position).getCommission() != null) {
            commission_text.setText(arrayList.get(position).getCommission() + "% Commission");
        }
            cross_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept_builder(position);
                    dialog.dismiss();
                }
            });
            dialog.show();
        dialog.show();
    }
    private void addView(String text, FlowLayout flowLayout) {
        if(text != null) {
            if (!text.isEmpty()) {
                try {
                    View layout2 = LayoutInflater.from(context).inflate(R.layout.deal_child, flowLayout, false);
                    TextView deal_textview = (TextView) layout2.findViewById(R.id.deal_text);
                    deal_textview.setText(text);
                    flowLayout.addView(layout2);
                } catch (Exception e) {
                    String error = e.toString();
                }
            }
        }
    }


}
