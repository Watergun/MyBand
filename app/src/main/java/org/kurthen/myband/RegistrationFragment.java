package org.kurthen.myband;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;


/**
 * Activities that contain this fragment must implement the
 * {@link RegistrationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button regButton;

    private View mRegistrationForm;
    private EditText mNameEntry;
    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    private EditText mPasswordEntry2;

    private TextView mSwitchToLogin;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance() {
        RegistrationFragment fragment = new RegistrationFragment();
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
        View root = inflater.inflate(R.layout.fragment_registration, container, false);
        mSwitchToLogin = (TextView) root.findViewById(R.id.buttonSwitch2Login);
        mSwitchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onSwitchToLogin();
                }
            }
        });

        mNameEntry = (EditText) root.findViewById(R.id.nameEntryRegister);
        mEmailEntry = (EditText) root.findViewById(R.id.emailEntryRegister);
        mPasswordEntry = (EditText) root.findViewById(R.id.passwordEntryRegister);
        mPasswordEntry2 = (EditText) root.findViewById(R.id.passwordEntryRegister2);

        Button register = (Button) root.findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               attemptRegistration();
            }
        });
        return root;
    }

    private void attemptRegistration(){
        mNameEntry.setError(null);
        mEmailEntry.setError(null);
        mPasswordEntry.setError(null);
        mPasswordEntry2.setError(null);

        String name = mNameEntry.getText().toString();
        String email = mEmailEntry.getText().toString();
        String password1 = mPasswordEntry.getText().toString();
        String password2 = mPasswordEntry2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name)){
            mNameEntry.setError("Pflichtfeld");
            focusView = mNameEntry;
            cancel = true;
        }
        if(TextUtils.isEmpty(email)){
            mEmailEntry.setError("Pflichtfeld");
            focusView = mEmailEntry;
            cancel = true;
        }
        if(!checkPasswordValidity(password1)){
            mPasswordEntry.setError("Mind. 5 Zeichen und muss Zahlen & Buchstaben enthalten");
            focusView = mPasswordEntry;
            cancel = true;
        }
        if(!TextUtils.equals(password1, password2)){
            mPasswordEntry2.setError("Passwörter müssen übereinstimmen");
            focusView = mPasswordEntry2;
            cancel = true;
        }

        //Some input was erroneous
        if(cancel){
            focusView.requestFocus();
        }
        //Execute callback registration method in AuthorizationActivity
        else if(mListener != null){
            mListener.onRegistration(name, email, password1);
        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean checkPasswordValidity(String pw){
        if(pw.length() < 5)
            return false;
        if(!pw.matches("[A-Z,a-z,0-9]+"))
            return false;
        return true;
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
     */
    public interface OnFragmentInteractionListener {
        void onRegistration(String name, String email, String password);
        void onSwitchToLogin();
    }
}
