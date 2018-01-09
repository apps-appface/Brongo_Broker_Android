package appface.brongo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.util.AppConstants;

/**
 * Created by Rohit Kumar on 9/26/2017.
 */

public class ClientProfileAdapter extends RecyclerView.Adapter<ClientProfileAdapter.EmployeeViewHolder> {
    private Context context;
    private ArrayList<String> arrayList;
    private LayoutInflater inflater;
    private ClickListener clickListener;
    private ProgressDialog pd;
    private SharedPreferences pref;
    private int currentstatus;


    public ClientProfileAdapter(Context context,int currentstatus, ArrayList<String> arrayList,ClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.currentstatus = currentstatus;
        this.clickListener = clickListener;
        inflater = LayoutInflater.from(context);
        pd = new ProgressDialog(context, R.style.MyDialogTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pref = context.getSharedPreferences(AppConstants.PREF_NAME,0);
    }

    @Override
    public ClientProfileAdapter.EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.items, parent, false);
        ClientProfileAdapter.EmployeeViewHolder holder = new ClientProfileAdapter.EmployeeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClientProfileAdapter.EmployeeViewHolder holder, final int position) {
        holder.title.setText(arrayList.get(position));
        holder.line_view1.setVisibility(View.VISIBLE);
        holder.line_view2.setVisibility(View.VISIBLE);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clickText(position);
            }
        });
        if(position == 0){
            holder.line_view1.setVisibility(View.INVISIBLE);
        }
        if(position == arrayList.size()-1){
            holder.line_view2.setVisibility(View.INVISIBLE);
        }
        if(position < currentstatus){
            holder.line_view1.setBackgroundColor(context.getResources().getColor(R.color.status_green));
            holder.line_view2.setBackgroundColor(context.getResources().getColor(R.color.status_green));
            holder.dot_view.setImageResource(R.drawable.dot_green);
            holder.title.setTextColor(context.getResources().getColor(R.color.round_empty_gray));
            holder.title.setBackgroundResource(0);
        }else if(position > currentstatus){
            holder.line_view1.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.line_view2.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.dot_view.setImageResource(R.drawable.bulletin_dot);
            holder.title.setTextColor(context.getResources().getColor(R.color.round_empty_gray));
            holder.title.setBackgroundResource(0);
        }else if(position == currentstatus){
            holder.line_view1.setBackgroundColor(context.getResources().getColor(R.color.status_green));
            holder.line_view2.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.dot_view.setImageResource(R.drawable.big_dot_green);
            holder.title.setTextColor(context.getResources().getColor(R.color.white));
            holder.title.setBackgroundResource(R.drawable.rounded_green_text);
        }
        if(position == 1){
            holder.reminder_text.setVisibility(View.VISIBLE);
        }else{
            holder.reminder_text.setVisibility(View.GONE);
        }
        holder.reminder_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.reminderClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView title,reminder_text;
        ImageView line_view1,dot_view,line_view2;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
           title = (TextView)itemView.findViewById(R.id.client_status_title);
            reminder_text = (TextView)itemView.findViewById(R.id.client_status_reminder);
            line_view2 = (ImageView) itemView.findViewById(R.id.client_status_view2);
            line_view1 = (ImageView)itemView.findViewById(R.id.client_status_view3);
            dot_view = (ImageView)itemView.findViewById(R.id.client_status_view1);
        }
    }

    public interface ClickListener{
        void clickText(int position);
        void reminderClick();
    }
}
