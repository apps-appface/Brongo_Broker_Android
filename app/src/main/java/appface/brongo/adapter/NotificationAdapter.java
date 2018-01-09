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
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
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
    private boolean loading;
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
            holder.builder_know_btn.setVisibility(View.VISIBLE);
        } else {
            if (arrayList.get(position).getAlertType().equalsIgnoreCase("CALL_BACK")) {
                holder.call_back_btn.setVisibility(View.VISIBLE);
            } else {
                holder.call_back_btn.setVisibility(View.GONE);
            }
            holder.builder_know_btn.setVisibility(View.GONE);
        }
        String string1 = arrayList.get(position).getClientName() + ":" + arrayList.get(position).getMessage();
        SpannableStringBuilder str = Utils.convertToSpannableString(string1, 0, arrayList.get(position).getClientName().length(), "black");
        holder.content_text.setText(str);
        holder.noti_time.setText(arrayList.get(position).getDays());
       /* Glide.with(context).load(arrayList.get(position).getClientProfile().toString()).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(context)).dontAnimate().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.noti_image);*/
        Glide.with(context)
                .load(arrayList.get(position).getClientProfile().toString())
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(holder.noti_image);
        holder.builder_know_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View)parentLinear;
                projectDialog(view,position);
            }
        });
        holder.call_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callListener.callBtnClick(arrayList.get(position).getMobileNo(),arrayList.get(position).getPropertyId());
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
    }

    public void setLoaded() {
        loading = false;
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
        Button call_back_btn;
        CircleImageView noti_image;
        ProgressBar progressBar;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            content_text = (TextView) itemView.findViewById(R.id.notification_content);
            noti_time = (TextView) itemView.findViewById(R.id.notification_time);
            noti_image = (CircleImageView) itemView.findViewById(R.id.notification_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.noti_progress);
            parentLinear = (RelativeLayout) itemView.findViewById(R.id.notification_parent_linear);
            builder_know_btn = (TextView) itemView.findViewById(R.id.know_more);
            call_back_btn = (Button) itemView.findViewById(R.id.call_back_btn);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private void projectDialog(final View view, final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.setContentView(R.layout.builder_noti_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        TextView project_name = (TextView) dialog.findViewById(R.id.dialog_project_name);
        TextView project_address = (TextView) dialog.findViewById(R.id.dialog_project_address);
        TextView project_budget = (TextView) dialog.findViewById(R.id.dialog_project_budget);
        TextView project_type = (TextView) dialog.findViewById(R.id.dialog_project_type);
        TextView project_status = (TextView) dialog.findViewById(R.id.dialog_project_status);
        project_name.setText(arrayList.get(position).getProjectName());
        project_address.setText(arrayList.get(position).getLocation());
        project_type.setText(arrayList.get(position).getProjectType());
        project_status.setText(arrayList.get(position).getProjectStatus());
        if(arrayList.get(position).getBudgetRange().equalsIgnoreCase("")){
            project_budget.setVisibility(View.GONE);
        }else{
            String budget = Utils.stringToInt(arrayList.get(position).getBudgetRange());
            project_budget.setText(budget);
        }
        project_budget.setText(arrayList.get(position).getProjectName());
        Button accept_btn = (Button) dialog.findViewById(R.id.builder_accept_btn);
        Button reject_btn = (Button) dialog.findViewById(R.id.builder_reject_btn);
        dialog.show();
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNotification(position,view);
                dialog.dismiss();
            }
        });
        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
                                notifyDataSetChanged();
                                if (arrayList.get(position).getAlertType().equalsIgnoreCase("BUILDER_POSTING")) {
                                    accept_builder(position);
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

}
