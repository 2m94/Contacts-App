package com.example.mycontactlistchp8;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;


public class DatePickerDialog extends DialogFragment {
    Calendar selectedDate;
    public interface SaveDateListener { //The listener handles the users actions. Communicates the users actions with the dialog back to the activity displaying the dialog
        void didFinishDatePickerDialog(Calendar selectedTime);
    }

    public DatePickerDialog(){
        //Empty constructor rwquired for datepicker
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, //creates View with resources from layout file associated with inflate
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.select_date, container);

        getDialog().setTitle("Select Date");
        final DatePicker dp = (DatePicker)view.findViewById(R.id.birthdayPicker);

        Button saveButton = (Button) view.findViewById(R.id.buttonSelect);
        saveButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v){
                getDialog().dismiss();
            }
        });
        return view;
    }
    private void saveItem(Calendar selectedTime){
        SaveDateListener activity = (SaveDateListener) getActivity();
        activity.didFinishDatePickerDialog(selectedTime);
        getDialog().dismiss();
    }
}

