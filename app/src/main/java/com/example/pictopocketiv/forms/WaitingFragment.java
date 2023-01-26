package com.example.pictopocketiv.forms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pictopocketiv.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitingFragment extends Fragment {

    private static final String ARG_MSG = "msg";

    private String mMsgTxt;
    private TextView mMsg;

    public WaitingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msg Parameter 1.
     * @return A new instance of fragment WaitingFragment.
     */
    public static WaitingFragment newInstance(String msg) {
        WaitingFragment fragment = new WaitingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMsgTxt = getArguments().getString(ARG_MSG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMsg = view.findViewById(R.id.wait_msg);
        mMsg.setText(mMsgTxt);
    }


    public void setMsg(String s) {
        //mMsg.setText(s);
    }
}