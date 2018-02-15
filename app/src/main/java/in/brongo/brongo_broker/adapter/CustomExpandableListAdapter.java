package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import in.brongo.brongo_broker.R;

/**
 * Created by Rohit Kumar on 11/27/2017.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LinearLayout linearLayout;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<String>> expandableListDetail;
    private int btn_number;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       LinkedHashMap<String, List<String>> expandableListDetail,int btn_number) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.btn_number = btn_number;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        int i = expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
        final String title = (String)getChild(listPosition,expandedListPosition);
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (listPosition == 9 && btn_number==1) {
                    convertView = layoutInflater.inflate(R.layout.faq_table, null);
                    TextView expandedListTextView = (TextView) convertView
                            .findViewById(R.id.table_text);
                    expandedListTextView.setText(title);
                } else if(i == 1) {
                    convertView = layoutInflater.inflate(R.layout.expand_child, null);
                    TextView expandedListTextView = (TextView) convertView
                            .findViewById(R.id.expandedListItem);
                    if (title.equalsIgnoreCase("")) {
                        expandedListTextView.setVisibility(View.GONE);
                    }
                    expandedListTextView.setText(title);
                }else{
                    convertView = layoutInflater.inflate(R.layout.expand_child_two, null);
                    TextView expandedListTextView = (TextView) convertView
                            .findViewById(R.id.child_text);
                    View view = (View)convertView.findViewById(R.id.dot_image);
                    RelativeLayout relativeLayout = (RelativeLayout)convertView.findViewById(R.id.expand_child_relative);
                    if(expandedListPosition == 0){
                        view.setVisibility(View.GONE);
                    }if(isLastChild){
                        relativeLayout.setBackgroundResource(R.drawable.expand_group_child);
                    }
                    if (title.equalsIgnoreCase("")) {
                        expandedListTextView.setVisibility(View.GONE);
                    }
                    expandedListTextView.setText(title);
                }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_header, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        ImageView icon = (ImageView)convertView.findViewById(R.id.group_icon);
        LinearLayout group_linear = (LinearLayout)convertView.findViewById(R.id.group_linear);
        if(isExpanded){
            icon.setImageResource(R.drawable.ic_minus_icon);
            group_linear.setBackgroundResource(R.drawable.expand_group);
        }else{
            icon.setImageResource(R.drawable.ic_plus_icon);
            group_linear.setBackgroundResource(R.drawable.edit_border);
        }
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
    private void addLayout(List<String> arrayList){
            for (int i = 1; i < arrayList.size(); i++) {
                final View layout2 = LayoutInflater.from(context).inflate(R.layout.expand_child_two, linearLayout, false);
                TextView textView = (TextView) layout2.findViewById(R.id.child_text);
                textView.setText(arrayList.get(i));
        }
    }
}
