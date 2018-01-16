package appface.brongo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import appface.brongo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TandC_Fragment extends Fragment {
    private int position;
    private TextView toolbar_title;
    private Toolbar toolbar;
private Context context;
    private ImageView edit_icon,delete_icon,add_icon;
    public TandC_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            position = bundle.getInt("position", 0);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tand_c_, container, false);
        context = getActivity();
        edit_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_edit);
        delete_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_delete);
        add_icon = (ImageView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.toolbar_inventory_add);
        edit_icon.setVisibility(View.GONE);
        delete_icon.setVisibility(View.GONE);
        add_icon.setVisibility(View.GONE);
        toolbar_title = (TextView)getActivity().findViewById(R.id.inventory_toolbar).findViewById(R.id.inventory_toolbar_title);
        toolbar = (Toolbar)getActivity().findViewById(R.id.inventory_toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar_title.setText("Terms & Conditions");
        TextView tc_text = (TextView)view.findViewById(R.id.terms_condition_text);
        String[] TermsList = context.getResources().getStringArray(R.array.tc_array);
        if(position<=TermsList.length){
            tc_text.setText(TermsList[position]);
        }
        return view;
    }

}
