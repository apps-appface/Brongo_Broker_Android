package appface.brongo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import appface.brongo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequirementFragment extends Fragment {
    private LinearLayout req_linear;
    private TextView req_deal_id,req_post;
    private ArrayList<String> header_list,childlist;
    private String location,location_area,project_name,prop_type,bhk_type,budget,martial_status,avoid_project,orientation,furnishing,other_req;

    public RequirementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requirement, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        location=location_area=project_name=prop_type=bhk_type=budget=martial_status=avoid_project=orientation=furnishing=other_req="";
        header_list = new ArrayList<>();
        childlist = new ArrayList<>();
        req_deal_id = (TextView)view.findViewById(R.id.req_deal_id);
        req_post = (TextView)view.findViewById(R.id.req_post_type);
        req_linear = (LinearLayout)view.findViewById(R.id.requirement_linear);
       // populateList();
    }
    private void populateList(){
        if(!location.equalsIgnoreCase("")){
            String text1 = "Location Type/street name";
            String text2 = location;
            addLayout(text1,text2,1);
        }else if(!location_area.equalsIgnoreCase("")){
            String text1="Area in "+ location;
            String text2 = location_area;
            addLayout(text1,text2,1);
        }else if(!project_name.equalsIgnoreCase("")){
            String text1 ="Name of Project/Socienty";
            String text2 =project_name;
            addLayout(text1,text2,1);
        }else if(!prop_type.equalsIgnoreCase("")){
            String text1 ="Property Types";
            String text2 =prop_type;
            addLayout(text1,text2,2);
        }else if(!bhk_type.equalsIgnoreCase("")){
            String text1 ="Bedroom";
            String text2 =bhk_type;
            addLayout(text1,text2,2);
        }else if(!budget.equalsIgnoreCase("")){
            String text1 ="Budget";
            String text2 =budget;
            addLayout(text1,text2,1);
        }else if(!martial_status.equalsIgnoreCase("")){
            String text1 ="Martial Status";
            String text2 =martial_status;
            addLayout(text1,text2,2);
        }else if(!avoid_project.equalsIgnoreCase("")){
            String text1 ="Avoid Project";
            String text2 =avoid_project;
            addLayout(text1,text2,1);
        }else if(!orientation.equalsIgnoreCase("")){
            String text1 ="Orientation";
            String text2 =orientation;
            addLayout(text1,text2,2);
        }else if(!furnishing.equalsIgnoreCase("")){
            String text1 ="Furnishing";
            String text2 =furnishing;
            addLayout(text1,text2,2);
        }else if(!other_req.equalsIgnoreCase("")){
            String text1 ="Any other requirements";
            String text2 =other_req;
            addLayout(text1,text2,1);
        }
    }
    private void addLayout(String text1,String text2,int i){
        View layout2 = null;
        TextView text_header = null,text_child = null;
        try {
          switch (i) {
              case 1:
                  layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.requirement_child_one, req_linear, false);
                  text_header = (TextView) layout2.findViewById(R.id.require_hearder_one);
                  text_child = (TextView) layout2.findViewById(R.id.require_child_one);
                  break;
              case 2:
                  layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.requirement_child_two, req_linear, false);
                  text_header = (TextView) layout2.findViewById(R.id.require_hearder_two);
                  text_child = (TextView) layout2.findViewById(R.id.require_child_two);
                  break;
          }
          text_header.setText(text1);
          text_child.setText(text2);
            req_linear.addView(layout2);
        } catch (Exception e) {
            String error = e.toString();
        }
    }

}
