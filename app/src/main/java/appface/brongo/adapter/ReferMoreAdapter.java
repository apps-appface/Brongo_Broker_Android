package appface.brongo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.model.ApiModel;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rohit Kumar on 12/26/2017.
 */

public class ReferMoreAdapter extends RecyclerView.Adapter<ReferMoreAdapter.EmployeeViewHolder> {
    private Context context;
    private ArrayList<ApiModel.referredBrokerObject> arrayList;
    private LayoutInflater inflater;

    public ReferMoreAdapter(Context context, ArrayList<ApiModel.referredBrokerObject> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ReferMoreAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.refer_more_child, parent, false);
        ReferMoreAdapter.EmployeeViewHolder holder = new ReferMoreAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ReferMoreAdapter.EmployeeViewHolder holder, final int position) {
        holder.refree_name.setText(arrayList.get(position).getName());
        holder.refree_phone.setText(arrayList.get(position).getMobileNo());
       /* Glide.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.placeholder1)
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(context)).dontAnimate().into(holder.refree_image);*/
        Glide.with(context)
                .load(arrayList.get(position).getImage())
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(holder.refree_image);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
       private TextView refree_name,refree_phone;
       private CircleImageView refree_image;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            refree_name = (TextView) itemView.findViewById(R.id.refree_name);
            refree_phone = (TextView) itemView.findViewById(R.id.refree_phone);
            refree_image = (CircleImageView) itemView.findViewById(R.id.refer_more_image);
        }
    }
}

