package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 10/11/2017.
 */

public class SubmenuAdapter extends RecyclerView.Adapter<SubmenuAdapter.EmployeeViewHolder> {
    private Context context;
    private ArrayList<String> arrayList;
    private LayoutInflater inflater;

    public SubmenuAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public SubmenuAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.legal_child, parent, false);
        SubmenuAdapter.EmployeeViewHolder holder = new SubmenuAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SubmenuAdapter.EmployeeViewHolder holder, final int position) {
        try {
            holder.title.setText(arrayList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        Button title;
        public EmployeeViewHolder(View itemView) {
            super(itemView);
            title = (Button) itemView.findViewById(R.id.legal_item_btn);

        }
    }
}
