package org.kurthen.myband;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private View mLoginForm;
    private EditText mEmailEntry;
    private EditText mPasswordEntry;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        TextView switchToRegistration = (TextView) root.findViewById(R.id.buttonSwitch2Registration);
        switchToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onSwitchToRegistration();
                }
            }
        });

        mEmailEntry = (EditText) root.findViewById(R.id.emailEntryLogin);
        mPasswordEntry = (EditText) root.findViewById(R.id.passwordEntryLogin);

        Button login = (Button) root.findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        return root;
    }

    private void attemptLogin(){

        mEmailEntry.setError(null);
        mPasswordEntry.setError(null);

        String email = mEmailEntry.getText().toString();
        String password = mPasswordEntry.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(email)){
            mEmailEntry.setError("Darf nicht leer sein");
            focusView = mEmailEntry;
            cancel = true;
        }
        if(TextUtils.isEmpty(password)) {
            mPasswordEntry.setError("Darf nicht leer sein");
            focusView = mPasswordEntry;
            cancel = true;
        }

        //Some input was erroneous
        if(cancel){
            focusView.requestFocus();
        }

        //Execute callback login method in AuthorizationActivity
        else if(mListener != null){
            mListener.onLogin(email, password);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onLogin(String email, String password);
        void onSwitchToRegistration();
    }
}
