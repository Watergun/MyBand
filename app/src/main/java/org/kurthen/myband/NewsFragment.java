package org.kurthen.myband;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnNewsInteraction} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends ListFragment {

    private View mFragmentRoot;
    private Vector<Update> mUpdateList;
    private ArrayAdapter<Update> mUpdateAdapter;

    private ListView mUpdateListView;
    private OnNewsInteraction mListener;

    private int mLastShownBand = -1;

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
  */
        mUpdateList = new Vector<Update>(0);
        mUpdateAdapter = new UpdateListAdapter(getContext(), mUpdateList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentRoot = inflater.inflate(R.layout.fragment_news, container, false);
        mUpdateListView = (ListView) mFragmentRoot.findViewById(android.R.id.list);
        mUpdateListView.setAdapter(mUpdateAdapter);

        Band currentBand = CurrentProfile.getInstance().getSelectedBand();
        //if(currentBand == null){
        //   mFragmentRoot.findViewById(R.id.newsTextViewNoContent).setVisibility(View.VISIBLE);
        //}
        if(currentBand != null){
            refreshList(currentBand.getUpdates());
        }
        return mFragmentRoot;
    }

    public void refreshList(Update[] newUpdates){
        if(newUpdates == null || mUpdateAdapter == null){
            return;
        }

        // Display a text message when no update is existent
        if(newUpdates.length == 0){
            //TextView t = (TextView) mFragmentRoot.findViewById(android.R.id.empty);
            //t.setText(R.string.text_news_no_content);
            return;
        }
        //mFragmentRoot.findViewById(R.id.newsTextViewNoContent).setVisibility(View.INVISIBLE);

        // Convert the array to a dynamic list
        ListIterator<Update> it = Arrays.asList(newUpdates).listIterator();

        // There are several performance related issues we have to face here:
        // 1. An UI Update if no update happened is just useless and leads to flickering
        // 2. If a new update appears, the Array Adapter has to be informed
        // 3. If a different band got selected by the user, the Array Adapter has to be informed
        //
        // Problem 1 + 2 resulted in the implementation of the NotificationCounter
        //              that tracks the degree of recentness [it's just an incrementing counter]
        // Problem 3 is solved by the implementation of the mLastShownBand attribute

        if(mLastShownBand == CurrentProfile.getInstance().getSelectedBandIndex()) { // Band didn't change
            // Essential part here is to find out the local notification counter
            int lastUpdateNumber = 0;
            if (!mUpdateAdapter.isEmpty())
                lastUpdateNumber = mUpdateAdapter.getItem(mUpdateAdapter.getCount() - 1).getNotficationCounter();

            // Now loop through every locally known update and compare the notification number
            // which indicates whether the update is fresh (e.g. not received yet) or already seen
            while (it.hasNext()) {
                Update u = it.next();
                if (u.getNotficationCounter() > lastUpdateNumber)
                    mUpdateAdapter.add(u);
            }
        }
        else{   // Band changed and therefore the array adapter has to be cleared and build up again
                // with the correct updates
            mUpdateAdapter.clear();
            while(it.hasNext()){
                mUpdateAdapter.add(it.next());
            }
        }
        mUpdateAdapter.notifyDataSetChanged();

        mLastShownBand = CurrentProfile.getInstance().getSelectedBandIndex();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){

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
        if (context instanceof OnNewsInteraction) {
            mListener = (OnNewsInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNewsInteraction");
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
    public interface OnNewsInteraction {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class UpdateListAdapter extends ArrayAdapter<Update>{
        public UpdateListAdapter(Context context, List<Update> elements){
            super(context, R.layout.news_list_item, elements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Update u = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.news_list_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
            TextView content = (TextView) convertView.findViewById(R.id.list_item_content);

            title.setText(u.getTitle());
            content.setText("Content of update number: " + position);

            return convertView;
        }
    }
}
