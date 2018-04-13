package in.brongo.brongo_broker.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by Rohit Kumar on 12/11/2017.
 */

public class MatchingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
private Context context;
private ArrayList<ApiModel.MatchingModel> arrayList;
private LayoutInflater inflater;
private static final int TYPE_PERSONAL = 0;
private static final int TYPE_BUILDER = 1;
private ProgressDialog pd;
private String invenType;
private FragmentManager fragmentManager;
private SharedPreferences pref;


public MatchingAdapter(Context context, ArrayList<ApiModel.MatchingModel> arrayList,FragmentManager fragmentManager) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
        pd = new ProgressDialog(context, R.style.CustomProgressDialog);
        pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_loader));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
        }

@Override
public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PERSONAL) {
        //Inflating header view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_player_child, parent, false);
        return new PersonalViewHolder(itemView);
        } else if (viewType == TYPE_BUILDER) {
        //Inflating footer view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_builder_child, parent, false);
        return new BuilderViewHolder(itemView);
        } else return null;
        }

@Override
public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    try {
        if (viewHolder instanceof PersonalViewHolder) {
            PersonalViewHolder holder = (PersonalViewHolder) viewHolder;
            holder.invent_tag.setVisibility(View.VISIBLE);
            String budget = arrayList.get(position).getBudget() + "";
           // budget = Utils.stringToInt(budget);
            String image = arrayList.get(position).getPropertyImage1();
            Glide.with(context)
                    .load(arrayList.get(position).getPropertyImage1())
                    .apply(CustomApplicationClass.getPropertyImage(true))
                    .into(holder.prop_image);
            holder.invent_child_clientName.setText(arrayList.get(position).getClientName());
            holder.invent_child_client.setText(arrayList.get(position).getPostingType().toUpperCase() + "/" + arrayList.get(position).getPropertyType().toUpperCase());
            holder.invent_child_mobile.setText(arrayList.get(position).getClientMobileNo());
            addView(arrayList.get(position).getMicroMarketName(), holder.flowLayout);
            addView(arrayList.get(position).getBedRoomType(), holder.flowLayout);
            addView(arrayList.get(position).getPropertyStatus(), holder.flowLayout);
            addView(budget, holder.flowLayout);
            addView(arrayList.get(position).getSubPropertyType(), holder.flowLayout);
            String backColor = Utils.getPostingColor(arrayList.get(position).getPostingType());
            holder.invent_child_client.setBackgroundColor(Color.parseColor(backColor));
            holder.invent_child_editBtn.setVisibility(View.GONE);
            holder.invent_child_deleteBtn.setVisibility(View.GONE);
        }else if (viewHolder instanceof BuilderViewHolder) {
            BuilderViewHolder holder = (BuilderViewHolder)viewHolder;
            holder.invent_builder_name.setText(arrayList.get(position).getClientName());
            holder.invent_builder_address.setText(arrayList.get(position).getMicroMarketName());
            String budget = arrayList.get(position).getBudget() + "";
           // budget = Utils.stringToInt(budget);
            holder.invent_builder_tag.setVisibility(View.VISIBLE);
            holder.invent_builder_commission.setText(arrayList.get(position).getCommission()+"% Commission");
            if(arrayList.get(position).getPropertyImage1() != null) {
                Glide.with(context).load(arrayList.get(position).getPropertyImage1()).apply(CustomApplicationClass.getPropertyImage(true)).into(holder.invent_builder_image);
            }
            holder.invent_builder_register.setVisibility(View.GONE);
            holder.invent_builder_reject.setVisibility(View.GONE);
            holder.invent_builder_proceed.setVisibility(View.GONE);
            holder.invent_builder_tc.setVisibility(View.GONE);
            holder.builder_web_linear.setVisibility(View.VISIBLE);
            holder.builder_flowLayout.removeAllViews();
            addView(budget,holder.builder_flowLayout);
            addView((arrayList.get(position).getBedRoomType()),holder.builder_flowLayout);
            addView(arrayList.get(position).getLandArea(),holder.builder_flowLayout);
            addView(arrayList.get(position).getPropertyStatus(),holder.builder_flowLayout);
            addView(arrayList.get(position).getSubPropertyType(),holder.builder_flowLayout);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

}


@Override
public int getItemCount() {
        return arrayList.size();
        }
private class BuilderViewHolder extends RecyclerView.ViewHolder {

    TextView invent_builder_name,invent_builder_address,invent_builder_commission,invent_builder_tag;
    ImageView invent_builder_image;
    LinearLayout recycle_item_linear,build_linear,builder_web_linear;
    RelativeLayout invent_builder_tc,invent_builder_register,invent_TC_relative;
    FlowLayout builder_flowLayout;
    Button invent_builder_proceed,invent_builder_reject;

    public BuilderViewHolder(View itemView) {
        super(itemView);
        builder_flowLayout =itemView.findViewById(R.id.builder_flowLayout);
        invent_builder_name = itemView.findViewById(R.id.invent_builder_project);
        invent_builder_address = itemView.findViewById(R.id.invent_builder_addresss);
        invent_builder_commission = itemView.findViewById(R.id.invent_builder_commission);;
        invent_builder_image = itemView.findViewById(R.id.invent_builder_image);
        invent_builder_tc = itemView.findViewById(R.id.invent_builder_tc);
        invent_TC_relative = itemView.findViewById(R.id.invent_TC_relative);
        invent_builder_register =  itemView.findViewById(R.id.invent_builder_register);
        build_linear = itemView.findViewById(R.id.invent_builder_linear);
        builder_web_linear = itemView.findViewById(R.id.builder_web_linear);
        invent_builder_proceed = itemView.findViewById(R.id.invent_builder_proceed);
        invent_builder_reject = itemView.findViewById(R.id.invent_builder_reject);
        invent_builder_tag = itemView.findViewById(R.id.builder_tag);
    }
}

private class PersonalViewHolder extends RecyclerView.ViewHolder {
    TextView invent_child_client,invent_child_clientName,invent_child_bhk,invent_child_mobile,invent_child_prop_status,invent_child_location,invent_child_budget,invent_child_prop_type,invent_tag;
    ImageView prop_image;
    FlowLayout flowLayout;
    LinearLayout recycle_item_linear,invent_child_editBtn,invent_child_deleteBtn;

    public PersonalViewHolder(View itemView) {
        super(itemView);
        flowLayout = itemView.findViewById(R.id.invent_personal_flowlayout);
        invent_child_clientName =itemView.findViewById(R.id.invent_personal_name);
        invent_child_client = itemView.findViewById(R.id.invent_pesonal_postingtype);
        invent_child_mobile = itemView.findViewById(R.id.invent_pesonal_mobile);
        prop_image = itemView.findViewById(R.id.invent_personal_image);
        invent_tag = itemView.findViewById(R.id.personal_tag);
        invent_child_editBtn = itemView.findViewById(R.id.edit_image);
        invent_child_deleteBtn = itemView.findViewById(R.id.delete_image);
        recycle_item_linear = itemView.findViewById(R.id.linear_item);
    }
}
    @Override
    public int getItemViewType(int position) {
        int viewtype =100;
        if (arrayList.get(position).getType().equalsIgnoreCase("personal")) {
            viewtype = TYPE_PERSONAL;
        } else if (arrayList.get(position).getType().equalsIgnoreCase("builder")) {
            viewtype = TYPE_BUILDER;
        }
        return viewtype;
    }
    private void addView(String text, FlowLayout flowLayout) {
        if(text != null && (!text.equalsIgnoreCase("null"))) {
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
