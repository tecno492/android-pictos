package com.example.pictopocketiv.forms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.firebase.FirebaseAuthService;
import com.example.pictopocketiv.states.MainActivityStateMV;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccessFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccessFormFragment extends Fragment {

    /** Attrs **/
    private static final String TAG = AccessFormFragment.class.getSimpleName();
    private MainActivityStateMV mActivityMV;
    private EditText mEmail;
    private EditText mPass;
    private Button mSubmit;
    private Button mSignup;


    /** C **/
    public AccessFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessFormFragment.
     */
    public static AccessFormFragment newInstance() {
        AccessFormFragment fragment = new AccessFormFragment();
        return fragment;
    }

    /** Lifecycle **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_access_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAccessModel(view);
        setUI(view);
    }
    
    
    /** UI **/
    private void setUI(View view) {
        mEmail = (EditText) view.findViewById(R.id.acces_form_email_fld);
        mPass = (EditText) view.findViewById(R.id.access_form_pass_fld);

        mSubmit = (Button) view.findViewById(R.id.access_form_submit_btn);
        mSubmit.setOnClickListener(onSubmit);

        mSignup = (Button) view.findViewById(R.id.signup_access_form_submit_btn);
        mSignup.setOnClickListener(onSignup);
    }


    /** Main Activity Status Model */
    private void setAccessModel(View view) {
        mActivityMV = new ViewModelProvider(getActivity())
                .get(MainActivityStateMV.class);
    }


    /** User Actions **/
    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = mEmail.getText().toString();
            String pass = mPass.getText().toString();

            // --- Listener
            FirebaseAuthService.OnOpenFirebaseSession onOpenSession =
                    new FirebaseAuthService.OnOpenFirebaseSession() {
                @Override
                public void onSuccess() {
                    //mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
                }

                @Override
                public void onFailure(Exception e) {
                    if(e instanceof FirebaseAuthInvalidUserException) {
                        mActivityMV.setStatus(MainActivityStateMV.Signal.KO2);
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException){
                        mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
                    } else {
                        mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
                    }
                    Log.e(TAG,e.getMessage());
                }
            };

            // --- Try to open session
            try {
                mActivityMV.setState(MainActivityStateMV.ActivityState.W_USER_CREDENTIALS_RSP);
                FirebaseAuthService.openSession(email,pass,onOpenSession);

            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
                mActivityMV.setState(MainActivityStateMV.ActivityState.E_INVALID_SIGNUP);
            }
        }
    };

    private View.OnClickListener onSignup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
        }
    };

}