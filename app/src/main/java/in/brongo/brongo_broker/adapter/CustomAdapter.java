package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 12/1/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private ArrayList<String> arraylist;
    Context context;
    public CustomAdapter(ArrayList<String> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView conditon_text;
        View plan_view;
        public MyViewHolder(View view) {
            super(view);
            conditon_text=(TextView) view.findViewById(R.id.plan_child_text);
            plan_view =(View)view.findViewById(R.id.plan_child_view);
        }
    }
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_list_child, parent, false);
        return new CustomAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int position) {
        holder.conditon_text.setText(arraylist.get(position));
      if(position == arraylist.size()-1){
          holder.plan_view.setVisibility(View.INVISIBLE);
      }else{
          holder.plan_view.setVisibility(View.VISIBLE);
      }
    }
    @Override
    public int getItemCount()
    {
        return arraylist.size();
    }
}
