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

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FinancesFragment.OnFinancesInteraction} interface
 * to handle interaction events.
 * Use the {@link FinancesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinancesFragment extends Fragment {

    private View mFragmentRoot;

    private OnFinancesInteraction mListener;

    private ListView mTransactionListView;
    private TransactiontListAdapter mTransactionAdapter;

    public FinancesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FinancesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinancesFragment newInstance() {
        FinancesFragment fragment = new FinancesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTransactionAdapter = new TransactiontListAdapter(getContext(), new Vector<Transaction>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentRoot = inflater.inflate(R.layout.fragment_finances, container, false);
        mTransactionListView = (ListView) mFragmentRoot.findViewById(R.id.transaction_list_view);
        mTransactionListView.setAdapter(mTransactionAdapter);

        Band currentBand = CurrentProfile.getInstance().getSelectedBand();
        if(currentBand == null){
            mFragmentRoot.findViewById(R.id.financesTextViewNoContent).setVisibility(View.VISIBLE);
        }
        else{
            refreshList(currentBand.getTransactions());
        }

        return mFragmentRoot;
    }

    public void refreshList(Transaction[] newTransactions){
        if(newTransactions == null || mTransactionAdapter == null){
            return;
        }
        else if(newTransactions.length == 0){
            TextView t = (TextView) mFragmentRoot.findViewById(R.id.financesTextViewNoContent);
            t.setVisibility(View.VISIBLE);
            t.setText(R.string.text_finances_no_content);
        }

        ListIterator<Transaction> it = Arrays.asList(newTransactions).listIterator();

        int lastUpdateNumber = 0;
        if(!mTransactionAdapter.isEmpty())
            lastUpdateNumber = mTransactionAdapter.getItem(mTransactionAdapter.getCount()-1).getNotficationCounter();

        while(it.hasNext()){
            Transaction t = it.next();
            if(t.getNotficationCounter() > lastUpdateNumber)
                mTransactionAdapter.add(t);
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
        if (context instanceof OnFinancesInteraction) {
            mListener = (OnFinancesInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFinancesInteraction");
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
    public interface OnFinancesInteraction {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class TransactiontListAdapter extends ArrayAdapter<Transaction> {
        public TransactiontListAdapter(Context context, List<Transaction> elements){
            super(context, R.layout.transaction_list_item, elements);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Transaction t = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_list_item, parent);
            }

            TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
            TextView content = (TextView) convertView.findViewById(R.id.list_item_content);
            title.setText("" + t.getValue());
            title.setText(t.getTitle());

            return convertView;
        }
    }
}
