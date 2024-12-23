package in.brongo.brongo_broker.fragment;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.adapter.CustomExpandableListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaqFragment extends Fragment {
    private Context context;
    private ImageView edit_icon,delete_icon,add_icon;
    private TextView toolbar_title;
    private Toolbar toolbar;
    private RelativeLayout parentLayout;
   private ExpandableListView expandableListView,expandableListView2,expandableListView3;
    private ExpandableListAdapter expandableListAdapter,expandableListAdapter2,expandableListAdapter3;
    private List<String> expandableListTitle,expandableListTitle2,expandableListTitle3;
   private LinkedHashMap<String, List<String>> expandableListDetail,expandableListDetail2,expandableListDetail3;
   private Button faq_btn_one,faq_btn_two,faq_btn_three;
    public FaqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
       initialise(view);

        faq_btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonView(1);
            }
        });
        faq_btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonView(2);
            }
        });
        faq_btn_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeButtonView(3);
            }
        });
        return view;
    }
    private void initialise(View view){
        try {
            context = getActivity();
            expandableListView = view.findViewById(R.id.faq_list);
            expandableListView2 = view.findViewById(R.id.faq_list2);
            expandableListView3 = view.findViewById(R.id.faq_list3);
            faq_btn_one = view.findViewById(R.id.beforedeal_btn);
            faq_btn_two = view.findViewById(R.id.Whiledeal_btn);
            faq_btn_three = view.findViewById(R.id.afterdeal_btn);
            expandableListDetail = getData();
            expandableListDetail2 = getSecondData();
            expandableListDetail3 = getThirdData();
            parentLayout = getActivity().findViewById(R.id.menu_parent_relative);
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListTitle2 = new ArrayList<String>(expandableListDetail2.keySet());
            expandableListTitle3 = new ArrayList<String>(expandableListDetail3.keySet());
            expandableListView.setGroupIndicator(null);
            expandableListView2.setGroupIndicator(null);
            expandableListView3.setGroupIndicator(null);
            edit_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
            delete_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
            add_icon = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
            edit_icon.setVisibility(View.GONE);
            delete_icon.setVisibility(View.GONE);
            add_icon.setVisibility(View.GONE);
            toolbar_title = getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
            toolbar = getActivity().findViewById(R.id.inventory_toolbar);
            toolbar.setVisibility(View.VISIBLE);
            toolbar_title.setText("FAQ's");
            expandableListAdapter = new CustomExpandableListAdapter(context, expandableListTitle, expandableListDetail,1);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListAdapter3 = new CustomExpandableListAdapter(context, expandableListTitle3, expandableListDetail3,3);
            expandableListView3.setAdapter(expandableListAdapter3);
            expandableListAdapter2 = new CustomExpandableListAdapter(context, expandableListTitle2, expandableListDetail2,2);
            expandableListView2.setAdapter(expandableListAdapter2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private LinkedHashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        try {
            String[] child = getActivity().getResources().getStringArray(R.array.faq_child1);
            String[] header = getActivity().getResources().getStringArray(R.array.faq_header1);
            String[] lead_list = getActivity().getResources().getStringArray(R.array.getlead_item);
            String[] brongo_benefit_list = getActivity().getResources().getStringArray(R.array.brongo_benefit_list);
            String[] lead_limi_list = getActivity().getResources().getStringArray(R.array.lead_limi_list);

            for(int i=0;i<child.length;i++){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(child[i]);
               switch(i){
                   case 2:
                   for(int j=0;j<lead_list.length;j++){
                       arrayList.add(lead_list[j]);
                   }
                       break;
                   case 3:
                       for(int j=0;j<brongo_benefit_list.length;j++){
                           arrayList.add(brongo_benefit_list[j]);
                       }
                       break;
                   case 4:
                       for(int j=0;j<lead_limi_list.length;j++){
                           arrayList.add(lead_limi_list[j]);
                       }
                       break;
               }
                expandableListDetail.put(header[i], arrayList);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return expandableListDetail;
    }
    private LinkedHashMap<String, List<String>> getSecondData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        try {
            String[] child = getActivity().getResources().getStringArray(R.array.faq_two_child);
            String[] header = getActivity().getResources().getStringArray(R.array.faq_two);

            for(int i=0;i<child.length;i++){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(child[i]);
                expandableListDetail.put(header[i], arrayList);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return expandableListDetail;
    }
    private LinkedHashMap<String, List<String>> getThirdData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();
        try {
            String[] child = getActivity().getResources().getStringArray(R.array.faq_three_child);
            String[] header = getActivity().getResources().getStringArray(R.array.faq_three);
            String[] rate_list = getActivity().getResources().getStringArray(R.array.faq_rate_extra);
            String[] after_exeution_list = getActivity().getResources().getStringArray(R.array.faq_after_execution);

            for(int i=0;i<child.length;i++){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(child[i]);
                switch(i){
                    case 0:
                        for(int j=0;j<rate_list.length;j++){
                            arrayList.add(rate_list[j]);
                        }
                        break;
                    case 3:
                        for(int j=0;j<after_exeution_list.length;j++){
                            arrayList.add(after_exeution_list[j]);
                        }
                        break;
                }
                expandableListDetail.put(header[i], arrayList);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return expandableListDetail;
    }
    private void changeButtonView(int i){
        try {
            expandableListView.setVisibility(View.GONE);
            expandableListView2.setVisibility(View.GONE);
            expandableListView3.setVisibility(View.GONE);
            faq_btn_one.setBackgroundResource(R.drawable.button_change);
            faq_btn_two.setBackgroundResource(R.drawable.button_change);
            faq_btn_three.setBackgroundResource(R.drawable.button_change);
            faq_btn_one.setTextColor(context.getResources().getColor(R.color.appColor));
            faq_btn_two.setTextColor(context.getResources().getColor(R.color.appColor));
            faq_btn_three.setTextColor(context.getResources().getColor(R.color.appColor));
            switch(i){
                case 1:
                    expandableListView.setVisibility(View.VISIBLE);
                    faq_btn_one.setBackgroundResource(R.drawable.dialog_button);
                    faq_btn_one.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case 2:
                    expandableListView2.setVisibility(View.VISIBLE);
                    faq_btn_two.setBackgroundResource(R.drawable.dialog_button);
                    faq_btn_two.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case 3:
                    expandableListView3.setVisibility(View.VISIBLE);
                    faq_btn_three.setBackgroundResource(R.drawable.dialog_button);
                    faq_btn_three.setTextColor(context.getResources().getColor(R.color.white));
                    break;
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
