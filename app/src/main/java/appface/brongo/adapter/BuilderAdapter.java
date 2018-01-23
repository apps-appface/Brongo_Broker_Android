package appface.brongo.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.uiwidget.FlowLayout;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rohit Kumar on 12/28/2017.
 */

public class BuilderAdapter extends RecyclerView.Adapter<BuilderAdapter.EmployeeViewHolder> implements NoInternetTryConnectListener{
    private Context context;
    private ArrayList<BuilderModel.BuilderObject> arrayList;
    private LayoutInflater inflater;
    private OnClick onClick;
    private FragmentManager fragmentManager;
    private SharedPreferences pref;


    public BuilderAdapter(Context context, ArrayList<BuilderModel.BuilderObject> arrayList, FragmentManager fragmentManager,OnClick onClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        this.onClick = onClick;
        inflater = LayoutInflater.from(context);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public BuilderAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.inventory_builder_child, parent, false);
        BuilderAdapter.EmployeeViewHolder holder = new BuilderAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BuilderAdapter.EmployeeViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final BuilderModel.BuilderObject builderObject = arrayList.get(position);
      holder.invent_builder_name.setText(arrayList.get(position).getProjectName());
      String url = arrayList.get(position).getUrl();
      if(url != null && url.isEmpty()){
          holder.build_linear.setVisibility(View.VISIBLE);
          holder.invent_TC_relative.setVisibility(View.GONE);
      }else if(url != null && !url.isEmpty()) {
          holder.build_linear.setVisibility(View.GONE);
          holder.invent_TC_relative.setVisibility(View.VISIBLE);
      }
        holder.invent_builder_address.setText(arrayList.get(position).getSubLocation());
        holder.invent_builder_commission.setText(arrayList.get(position).getCommission()+"% Commission");
        String imageUrl = "";
        if(arrayList.get(position).getImageFiles().size()>0){
            imageUrl = arrayList.get(position).getImageFiles().get(0);
           imageUrl = Utils.getImageUrl(imageUrl,pref);
            }
       /* Glide.with(context).load(arrayList.get(position).getImageFiles()).placeholder(R.drawable.no_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.invent_builder_image);*/
      Glide.with(context).load(imageUrl).apply(CustomApplicationClass.getPropertyImage(true)).into(holder.invent_builder_image);

        holder.invent_builder_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.registerClient(position,builderObject);
            }
        });
        holder.invent_builder_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.rejectProject(position,builderObject);
            }
        });
        holder.invent_builder_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.acceptTc(position,builderObject,true);
            }
        });
        holder.invent_builder_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.acceptTc(position,builderObject,false);
            }
        });
        holder.builder_web_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.proceedToWeb(position,builderObject);
            }
        });
        int budget = (arrayList.get(position).getLandArea())* (arrayList.get(position).getPricePerSQFT());
        String project_budget = Utils.numToWord(budget);
        holder.builder_flowLayout.removeAllViews();
        addView(project_budget,holder.builder_flowLayout);
        addView((arrayList.get(position).getNoOfBedRooms()+"BHK"),holder.builder_flowLayout);
        addView(arrayList.get(position).getLandArea()+" "+arrayList.get(position).getLandAreaUnits(),holder.builder_flowLayout);
        addView(arrayList.get(position).getProjectStatus(),holder.builder_flowLayout);
        addView(arrayList.get(position).getProjectType(),holder.builder_flowLayout);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onTryReconnect() {

    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView invent_builder_name,invent_builder_address,invent_builder_commission;
        ImageView invent_builder_image;
        LinearLayout recycle_item_linear,build_linear,builder_web_linear;
        RelativeLayout invent_TC_relative;
        FlowLayout builder_flowLayout;
        Button invent_builder_tc,invent_builder_register,invent_builder_proceed,invent_builder_reject;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            builder_flowLayout = (FlowLayout)itemView.findViewById(R.id.builder_flowLayout);
            invent_builder_name = (TextView) itemView.findViewById(R.id.invent_builder_project);
            invent_builder_address = (TextView) itemView.findViewById(R.id.invent_builder_addresss);
            invent_builder_commission = (TextView) itemView.findViewById(R.id.invent_builder_commission);;
            invent_builder_image = (ImageView) itemView.findViewById(R.id.invent_builder_image);
            invent_builder_tc = (Button) itemView.findViewById(R.id.invent_builder_tc);
            invent_TC_relative = (RelativeLayout)itemView.findViewById(R.id.invent_TC_relative);
            invent_builder_register = (Button) itemView.findViewById(R.id.invent_builder_register);
            build_linear = (LinearLayout)itemView.findViewById(R.id.invent_builder_linear);
            builder_web_linear = (LinearLayout)itemView.findViewById(R.id.builder_web_linear);
            invent_builder_proceed = (Button) itemView.findViewById(R.id.invent_builder_proceed);
            invent_builder_reject = (Button) itemView.findViewById(R.id.invent_builder_reject);
        }
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

   /* public void setOnClick(BuilderAdapter.OnClick onClick) {
        this.onClick = onClick;
    }*/
    public interface OnClick {
        void acceptTc(int position,BuilderModel.BuilderObject builderObject,boolean accept);
        void proceedToWeb(int position,BuilderModel.BuilderObject builderObject);
        void registerClient(int position,BuilderModel.BuilderObject builderObject);
         void rejectProject(int position,BuilderModel.BuilderObject builderObject);
    }
}
