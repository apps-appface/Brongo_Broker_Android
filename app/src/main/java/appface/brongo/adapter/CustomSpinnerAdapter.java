package appface.brongo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.activity.MapActivity;
import appface.brongo.model.SignUpModel;

/**
 * Created by Rohit Kumar on 1/15/2018.
 */

    public class CustomSpinnerAdapter extends ArrayAdapter<String>{

        private Activity activity;
        private ArrayList data;
        private ArrayList<Integer> countList;
        public Resources res;
        LayoutInflater inflater;

        /*************  CustomAdapter Constructor *****************/
        public CustomSpinnerAdapter(
                MapActivity activitySpinner,
                int textViewResourceId,
                ArrayList objects,ArrayList<Integer> countList
        )
        {
            super(activitySpinner, textViewResourceId, objects);

            /********** Take passed values **********/
            activity = activitySpinner;
            data     = objects;
            this.countList = countList;

            /***********  Layout inflator to call external xml layout () **********************/
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // This funtion called for each row ( Called data.size() times )
        public View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.spinner_rows, parent, false);

            /***** Get each Model object from Arraylist ********/
            TextView label   = (TextView)row.findViewById(R.id.spinner_header);
            TextView sub     = (TextView)row.findViewById(R.id.spinner_child);
           /* if(position==0){

                // Default selected Spinner item
                label.setText("Please select company");
                sub.setText("");
            }
            else
            {*/
                // Set values for spinner each row
                label.setText(data.get(position).toString());
                sub.setText(countList.get(position)+" available");


          /*  }*/

            return row;
        }
    }