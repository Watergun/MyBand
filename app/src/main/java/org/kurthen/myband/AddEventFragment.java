package org.kurthen.myband;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddEventFragment extends Fragment
    implements DatePickerFragment.OnDateSetListener,
                TimePickerFragment.OnTimeSetListener{

    private EditText mTitleEdit;
    private EditText mDescriptionEdit;

    private EditText mDateEdit;
    private EditText mTimeEdit;
    private EditText mDurationEdit;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_event, container, false);

        // Save the widget
        mTitleEdit = (EditText) root.findViewById(R.id.add_event_fragment_title_edit);
        mDescriptionEdit = (EditText) root.findViewById(R.id.add_event_fragment_description_edit);
        mDateEdit = (EditText) root.findViewById(R.id.add_event_fragment_date_edit);
        mTimeEdit = (EditText) root.findViewById(R.id.add_event_fragment_time_edit);
        mDurationEdit = (EditText) root.findViewById(R.id.add_event_fragment_duration_edit);

        final Bundle args = new Bundle();
        args.putInt("FRAG_ID", getId());

        mDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateDlg = new DatePickerFragment();
                dateDlg.setArguments(args);
                dateDlg.show(getFragmentManager(), "datePicker");
            }
        });

        mTimeEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment timeDlg = new TimePickerFragment();
                timeDlg.setArguments(args);
                timeDlg.show(getFragmentManager(), "timePicker");
            }
        });

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(int year, int month, int day){
        mDateEdit.setText("" + day + "." + month + "." + year);
    }

    @Override
    public void onTimeSet(int hour, int minute){
        mTimeEdit.setText("" + hour + ":" + minute);
    }
}
