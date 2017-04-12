package org.kurthen.myband;


import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.*;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment
        implements  DatePickerDialog.OnDateSetListener{

    private OnDateSetListener mCallback;

    public DatePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        int fragId = getArguments().getInt("FRAG_ID");
        mCallback = (OnDateSetListener) getFragmentManager().findFragmentById(fragId);

        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        if(mCallback != null)
            mCallback.onDateSet(year, month, day);
    }

    public static interface OnDateSetListener{
        public void onDateSet(int year, int month, int day);
    }
}
