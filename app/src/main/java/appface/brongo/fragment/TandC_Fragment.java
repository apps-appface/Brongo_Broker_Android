package appface.brongo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import appface.brongo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TandC_Fragment extends Fragment {
    private int position;
private Context context;
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
        TextView tc_text = (TextView)view.findViewById(R.id.terms_condition_text);
        String[] TermsList = context.getResources().getStringArray(R.array.tc_array);
        if(position<=TermsList.length){
            tc_text.setText(TermsList[position]);
        }
        return view;
    }

}
