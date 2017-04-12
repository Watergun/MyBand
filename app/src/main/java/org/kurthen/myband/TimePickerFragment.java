package org.kurthen.myband;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener{

    private OnTimeSetListener mCallback;

    public TimePickerFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        int fragId = getArguments().getInt("FRAG_ID");
        mCallback = (OnTimeSetListener) getFragmentManager().findFragmentById(fragId);

        final java.util.Calendar c = java.util.Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hour, int minute){
        if(mCallback != null)
            mCallback.onTimeSet(hour, minute);
    }

    public static interface OnTimeSetListener{
        public void onTimeSet(int hour, int minute);
    }
}
