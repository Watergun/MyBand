package org.kurthen.myband;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.kurthen.myband.R;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnContactsInteraction} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private View mFragmentRoot;

    private OnContactsInteraction mListener;

    private ListView mSongListView;
    private ContactListAdapter mContactAdapter;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactAdapter = new ContactListAdapter(getContext(), new Vector<Contact>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentRoot = inflater.inflate(R.layout.fragment_music, container, false);

        mSongListView = (ListView) mFragmentRoot.findViewById(R.id.song_list_view);
        mSongListView.setAdapter(mContactAdapter);

        Band currentBand = CurrentProfile.getInstance().getSelectedBand();
        if(currentBand == null){
            mFragmentRoot.findViewById(R.id.musicTextViewNoContent).setVisibility(View.VISIBLE);
        }
        else{
            refreshList(currentBand.getSongs());
        }

        return mFragmentRoot;
    }

    public void refreshList(Contact[] newSongs){
        if(newSongs == null ||mContactAdapter == null){
            return;
        }
        else if(newSongs.length == 0){
            TextView t = (TextView) mFragmentRoot.findViewById(R.id.musicTextViewNoContent);
            t.setVisibility(View.VISIBLE);
            t.setText(R.string.text_music_no_content);
        }

        ListIterator<Contact> it = Arrays.asList(newSongs).listIterator();

        while(it.hasNext()){
            Contact c = it.next();
            mContactAdapter.add(c);
        }
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
        if (context instanceof OnMusicInteraction) {
            mListener = (OnMusicInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMusicInteraction");
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
    public interface OnContactsInteraction {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter(Context context, List<Contact> elements){
            super(context, R.layout.song_list_item, elements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Contact c = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_list_item, parent);
            }

            TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
            TextView content = (TextView) convertView.findViewById(R.id.list_item_content);
            title.setText(s.getTitle());
            title.setText(s.getAlbum());

            return convertView;
        }
    }
}
