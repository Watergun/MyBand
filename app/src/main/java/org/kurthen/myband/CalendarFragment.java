package org.kurthen.myband;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnCalendarInteraction} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends ListFragment {

    private View mFragmentRoot;

    private OnCalendarInteraction mListener;

    private ListView mEventListView;
    private EventListAdapter mEventAdapter;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventAdapter = new EventListAdapter(getContext(), new Vector<Event>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentRoot = inflater.inflate(R.layout.fragment_calendar, container, false);
        mEventListView = (ListView) mFragmentRoot.findViewById(android.R.id.list);
        mEventListView.setAdapter(mEventAdapter);

        Band currentBand = CurrentProfile.getInstance().getSelectedBand();
        //if(currentBand == null){
        //    mFragmentRoot.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        //}
        if(currentBand != null){
            refreshList(currentBand.getEvents());
        }

        initializeCalendar(mFragmentRoot);
        return mFragmentRoot;
    }

    public void initializeCalendar(View root){
        CalendarView calendar = (CalendarView) root.findViewById(R.id.calendar_view);

        calendar.setFirstDayOfWeek(2);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectDay(year, month, dayOfMonth);
            }
        });
    }

    public void refreshList(Event[] newEvents){
        if(newEvents == null || mEventAdapter == null){
            return;
        }
        else if(newEvents.length == 0){
            //TextView t = (TextView) mFragmentRoot.findViewById(R.id.calendarTextViewNoContent);
            //t.setVisibility(View.VISIBLE);
            //t.setText(R.string.text_calendar_no_content);
            return;
        }
        //mFragmentRoot.findViewById(R.id.calendarTextViewNoContent).setVisibility(View.INVISIBLE);

        // Convert the array into a dynamic list
        ListIterator<Event> it = Arrays.asList(newEvents).listIterator();

        int lastUpdateNumber = 0;
        if(!mEventAdapter.isEmpty())
            mEventAdapter.getItem(mEventAdapter.getCount()-1).getNotficationCounter();

        while(it.hasNext()){
            Event e = it.next();
            if(e.getNotficationCounter() > lastUpdateNumber)
                mEventAdapter.add(e);
        }
    }

    /* Selects a day by scrolling the event list to the specified date.
     */
    public void selectDay(int year, int month, int day){

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCalendarInteraction) {
            mListener = (OnCalendarInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCalendarInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCalendarInteraction {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class EventListAdapter extends ArrayAdapter<Event>{
        public EventListAdapter(Context context, List<Event> elements){
            super(context, R.layout.event_list_item, elements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Event e = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent);
            }

            TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
            TextView content = (TextView) convertView.findViewById(R.id.list_item_content);
            title.setText(e.getTitle());
            title.setText(e.getLocation());

            return convertView;
        }
    }
}
