package com.neos.trackandroll.view.adapter.sensors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.neos.trackandroll.R;

import java.util.List;

public class SpinnerArrayAdapter extends ArrayAdapter<String> {

    public Context context;

    /**
     * the constructor of the class
     * @param context : the context
     * @param resource : the resource
     * @param objects : the list of objects
     */
    public SpinnerArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    /**
     * method that says when it is enabled
     * @param position : the position
     * @return : boolean
     */
    @Override
    public boolean isEnabled(int position){
        if(position == 0){
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        } else {
            return true;
        }
    }

    /**
     * method that gets the view
     * @param position : the position
     * @param convertView : the view to convert
     * @param parent : the view group
     * @return the view
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(context.getResources().getColor(R.color.colorWhite));
            return super.getView(position, tv, parent);
        }else{
            return super.getView(position, convertView, parent);
        }
    }

    /**
     * method that gets the drop dow view
     * @param position : the position
     * @param convertView : the view
     * @param parent : the view group
     * @return : the drop down view
     */
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;

        // For disable first item
        /*
        if(position == 0){
            // Set the hint text color gray
            tv.setTextColor(context.getResources().getColor(R.color.colorWhite));
            tv.setVisibility(View.INVISIBLE);
        }
        else {
            tv.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }
        */
        tv.setTextColor(context.getResources().getColor(R.color.colorWhite));
        if(position%2==0){
            // Set the item background color
            tv.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }else {
            // Set the alternate item background color
            tv.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        return view;
    }
}
