package com.example.pictopocketiv.forms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.states.MainActivityStateMV;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MsgFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MsgFormFragment extends Fragment {


    private static final String TAG = MsgFormFragment.class.getSimpleName();
    private MainActivityStateMV mActivityMV;
    private Button mSubmit;
    private TextView mMsgTitle;
    private TextView mMsg;
    private String mTitleTxt;
    private String mMsgTxt;


    public MsgFormFragment() {
    }

    public MsgFormFragment(String mTitleTxt, String mMsgTxt) {
        // Required empty public constructor
        this.mTitleTxt = mTitleTxt;
        this.mMsgTxt = mMsgTxt;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MsgFormFragment newInstance(String title, String msg) {
        MsgFormFragment fragment = new MsgFormFragment(title, msg);
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
        return inflater.inflate(R.layout.fragment_msg_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAccessModel(view);
        setUI(view);
    }
    
    
    // ==== UI ==== //
    private void setUI(View view) {
        mMsgTitle = (TextView) view.findViewById(R.id.msg_form_title);
        mMsg = (TextView) view.findViewById(R.id.wait_msg);

        mMsgTitle.setText(mTitleTxt);
        mMsg.setText(mMsgTxt);

        mSubmit = (Button) view.findViewById(R.id.msg_form_submit_btn);
        mSubmit.setOnClickListener(onSubmit);
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

            mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
        }
    };

    public void setMsg(String s) {
        mMsg.setText(s);
    }
}