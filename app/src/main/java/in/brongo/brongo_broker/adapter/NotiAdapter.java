package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.uiwidget.FlowLayout;
import in.brongo.brongo_broker.util.AppConstants;
import in.brongo.brongo_broker.util.CustomApplicationClass;
import in.brongo.brongo_broker.util.Utils;

/**
 * Created by Rohit Kumar on 1/20/2018.
 */

public class NotiAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context context;
    private ArrayList<ApiModel.NotificationChildModel> arrayList;
    private LayoutInflater inflater;
    private RecyclerView recyclerView;
 /*   private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 15;
    private boolean loading,isVisible;*/
    private RelativeLayout parentLinear;
    private NotiAdapter.CallListener callListener;
    private SharedPreferences pref;
    private int unread;
    private NotiAdapter.OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,project_position;
    private boolean loading;


    public NotiAdapter(Context context, final ArrayList<ApiModel.NotificationChildModel> arrayList, RecyclerView recyclerView, NotiAdapter.CallListener callListener) {
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
    public int getItemViewType(int position) {
        return arrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.notification_item, parent, false);

            vh = new NotificationViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        try {
            if (holder instanceof NotificationViewHolder) {
                if (arrayList.get(position).isRead()) {
                    parentLinear.setBackgroundResource(R.color.white);
                } else {
                    parentLinear.setBackgroundResource(R.color.notification_color);
                }
                if (arrayList.get(position).getAlertType().equalsIgnoreCase("BUILDER_POSTING")) {
                    String budget = String.valueOf(arrayList.get(position).getBudgetRange());
                    budget = Utils.stringToInt(budget);
                    addView(arrayList.get(position).getProjectName(), ((NotificationViewHolder) holder).notification_flowlayout);
                    addView(arrayList.get(position).getLocation(), ((NotificationViewHolder) holder).notification_flowlayout);
                    addView(arrayList.get(position).getProjectType(), ((NotificationViewHolder) holder).notification_flowlayout);
                    addView(arrayList.get(position).getProjectStatus(), ((NotificationViewHolder) holder).notification_flowlayout);
                    addView((arrayList.get(position).getCommission() + "% Commission"), ((NotificationViewHolder) holder).notification_flowlayout);
                    addView(budget, ((NotificationViewHolder) holder).notification_flowlayout);
                    if (arrayList.get(position).getStatus().isEmpty()) {
                        ((NotificationViewHolder) holder).noti_view_linear.setVisibility(View.VISIBLE);
                    } else {
                        ((NotificationViewHolder) holder).noti_view_linear.setVisibility(View.GONE);
                    }
                } else {
                    ((NotificationViewHolder) holder).noti_view_linear.setVisibility(View.GONE);
                    ((NotificationViewHolder) holder).notification_flowlayout.setVisibility(View.GONE);
                }
                if (arrayList.get(position).getAlertType().equalsIgnoreCase("CALL_BACK")) {
                    ((NotificationViewHolder) holder).call_btn.setVisibility(View.VISIBLE);
                } else {
                    ((NotificationViewHolder) holder).call_btn.setVisibility(View.GONE);
                }
                String string1 = arrayList.get(position).getClientName() + ":" + arrayList.get(position).getMessage();
                SpannableStringBuilder str = Utils.convertToSpannableString(string1, 0, arrayList.get(position).getClientName().length(), "black");
                ((NotificationViewHolder) holder).content_text.setText(str);
                ((NotificationViewHolder) holder).noti_time.setText(arrayList.get(position).getDays());
                Glide.with(context)
                        .load(arrayList.get(position).getClientProfile().toString())
                        .apply(CustomApplicationClass.getRequestOption(true))
                        .into(((NotificationViewHolder) holder).noti_image);
                ((NotificationViewHolder) holder).view_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!arrayList.get(position).isRead()) {
                            callListener.readBtnClick(arrayList.get(position), position, false);
                        }
                        ((NotificationViewHolder) holder).notification_flowlayout.setVisibility(View.VISIBLE);
                        ((NotificationViewHolder) holder).view_btn.setVisibility(View.GONE);
                        ((NotificationViewHolder) holder).proceed_btn.setVisibility(View.VISIBLE);

                    }
                });
                ((NotificationViewHolder) holder).proceed_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callListener.proceedBtnClick(arrayList.get(position), position);
                    }
                });
                parentLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!arrayList.get(position).isRead()) {
                            if (!(arrayList.get(position).getAlertType().equalsIgnoreCase("BUILDER_POSTING") && arrayList.get(position).getStatus().equalsIgnoreCase(""))) {
                                callListener.readBtnClick(arrayList.get(position), position, true);
                            }
                        }
                    }
                });
                ((NotificationViewHolder) holder).reject_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callListener.rejectBtnClick(arrayList.get(position), position);
                    }
                });
                ((NotificationViewHolder) holder).call_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callListener.callBtnClick(arrayList.get(position).getMobileNo().toString(), arrayList.get(position).getPropertyId());
                    }
                });


            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }catch (Exception e){

        }
    }

    public void setLoaded(boolean loading) {
        this.loading = loading;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //
    public  class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView content_text, noti_time, noti_id,builder_know_btn;
        Button view_btn,proceed_btn,reject_btn,call_btn;
        ImageView noti_image;
        FlowLayout notification_flowlayout;
        LinearLayout noti_view_linear;

        public NotificationViewHolder(View v) {
            super(v);
            content_text = (TextView) itemView.findViewById(R.id.notification_content);
            noti_time = (TextView) itemView.findViewById(R.id.notification_time);
            noti_image = (ImageView) itemView.findViewById(R.id.notification_image);
            notification_flowlayout = (FlowLayout)itemView.findViewById(R.id.noti_flowlayout);
            parentLinear = (RelativeLayout) itemView.findViewById(R.id.notification_parent_linear);
            builder_know_btn = (TextView) itemView.findViewById(R.id.know_more);
            view_btn = (Button) itemView.findViewById(R.id.notification_view);
            proceed_btn = (Button) itemView.findViewById(R.id.notification_proceed);
            reject_btn = (Button) itemView.findViewById(R.id.notification_reject);
            call_btn = (Button) itemView.findViewById(R.id.notification_call);
            noti_view_linear = (LinearLayout) itemView.findViewById(R.id.builder_view_linear);
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
    public void setOnLoadMoreListener(NotiAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public interface CallListener{
        void callBtnClick(String phone,String propertyId);
        void readBtnClick(ApiModel.NotificationChildModel notificationChildModel,int position,boolean isDataset);
        void proceedBtnClick(ApiModel.NotificationChildModel notificationChildModel,int position);
        void rejectBtnClick(ApiModel.NotificationChildModel notificationChildModel,int position);
    }
    public interface OnLoadMoreListener {
        void onLoadMore();
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
                    e.printStackTrace();
                    String error = e.toString();
                }
            }
        }
    }
}
