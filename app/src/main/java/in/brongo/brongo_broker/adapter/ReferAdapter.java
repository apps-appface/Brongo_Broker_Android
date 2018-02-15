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
 * Created by Rohit Kumar on 10/18/2017.
 */

public class ReferAdapter extends RecyclerView.Adapter<ReferAdapter.EmployeeViewHolder> {
    private Context context;
    private ArrayList<String> arrayList1;
    private ArrayList<String> arrayList2;
    private LayoutInflater inflater;

    public ReferAdapter(Context context, ArrayList<String> arrayList1,ArrayList<String> arrayList2) {
        this.context = context;
        this.arrayList1 = arrayList1;
        this.arrayList2 = arrayList2;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ReferAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.refer_plan_child, parent, false);
        ReferAdapter.EmployeeViewHolder holder = new ReferAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ReferAdapter.EmployeeViewHolder holder, int position) {
        holder.broker_rate.setText(arrayList2.get(position));
        holder.broker_no.setText(arrayList1.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList1.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView broker_no,broker_rate;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            broker_no = (TextView)itemView.findViewById(R.id.refer_broker_no);
            broker_rate = (TextView)itemView.findViewById(R.id.refer_rate);
        }
    }
}
