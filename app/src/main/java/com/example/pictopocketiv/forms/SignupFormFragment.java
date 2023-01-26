package com.example.pictopocketiv.forms;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.firebase.FirebaseAuthService;
import com.example.pictopocketiv.states.MainActivityStateMV;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFormFragment extends Fragment {


    private static final String TAG = SignupFormFragment.class.getSimpleName();
    private MainActivityStateMV mActivityMV;
    private EditText mEmail;
    private EditText mPass;
    private Button mSubmit;
    private Button mCancel;
    private TextView mError;



    public SignupFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFormFragment newInstance() {
        SignupFormFragment fragment = new SignupFormFragment();
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
        return inflater.inflate(R.layout.fragment_signup_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAccessModel(view);
        setUI(view);
    }
    
    
    // ==== UI ==== //
    private void setUI(View view) {
        mEmail = (EditText) view.findViewById(R.id.signup_form_email_fld);
        mPass = (EditText) view.findViewById(R.id.signup_form_pass_fld);
        mSubmit = (Button) view.findViewById(R.id.signup_form_submit_btn);
        mCancel = (Button) view.findViewById(R.id.signup_form_cancel_btn);
        mError = (TextView) view.findViewById(R.id.signup_form_error);

        mSubmit.setOnClickListener(onSubmit);
        mCancel.setOnClickListener(onCancel);
    }

    // ==== ACTIVITY STATUS MODEL ==== //
    private void setAccessModel(View view) {
        
        mActivityMV = new ViewModelProvider(requireActivity())
                .get(MainActivityStateMV.class);

    }


    // ==== OPS ==== //
    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String email = mEmail.getText().toString();
            String pass = mPass.getText().toString();
            if (!email.isEmpty() || !pass.isEmpty()){
                FirebaseAuthService.OnUserFirebaseCreation onUserCreation =
                        new FirebaseAuthService.OnUserFirebaseCreation() {
                            @Override
                            public void onCreationSuccess() {

                                //mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
                            }

                            @Override
                            public void onCreationFailure(Exception e) {
                                mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
                                Log.e(TAG,e.getMessage());
                            }
                        };

                // --- Try to create account
                try {
                    mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
                    FirebaseAuthService.createaAccount(email,pass,onUserCreation);

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }else{
                mError.setText("Rellene los campos");
            }


        };



    };

    private View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
        }
    };
}