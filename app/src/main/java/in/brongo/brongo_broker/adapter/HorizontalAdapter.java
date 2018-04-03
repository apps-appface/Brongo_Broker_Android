package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 8/7/2017.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {


    List<String> horizontalList = Collections.emptyList();
    Context context;


    public HorizontalAdapter(List<String> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView micro_marketame;
        public MyViewHolder(View view) {
            super(view);
            micro_marketame=view.findViewById(R.id.hori_micromarket_name);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.micromarket_horizontal_object, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.micro_marketame.setText(horizontalList.get(position));
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}

