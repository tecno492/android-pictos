package com.example.pictopocketiv.actions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.pictopocketiv.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionsToolsMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionsToolsMenuFragment extends Fragment {

    /** Params **/
    private static final String ADD_ENABLED = "add enabled";
    private static final String EDIT_ENABLED = "edition enabled";


    /** Atrrs **/
    private Boolean mAddEnabled = false;
    private Boolean mEditEnabled = false;
    private View mSearchActions;
    private View mEditActions;
    private ImageButton mSearch;
    private ImageButton mCamera;
    private ImageButton mEdit;
    private ImageButton mTrash;
    private ImageButton mDeny;
    private ImageButton mLock1;
    private ImageButton mLock2;
    private ImageButton mAdd;

    private ActionsMV mActionsMenuMV;


    /** C **/
    public ActionsToolsMenuFragment() {
        // Required empty public constructor
    }

    public static ActionsToolsMenuFragment newInstance(boolean addEnabled,
                                                       boolean editEnabled) {

        ActionsToolsMenuFragment fragment = new ActionsToolsMenuFragment();
        fragment.mAddEnabled = addEnabled;
        fragment.mEditEnabled = editEnabled;
        Bundle args = new Bundle();
        args.putBoolean(ADD_ENABLED, addEnabled);
        args.putBoolean(EDIT_ENABLED, editEnabled);
        fragment.setArguments(args);
        return fragment;
    }


    /** Lifecycle **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAddEnabled = getArguments().getBoolean(ADD_ENABLED);
            mEditEnabled = getArguments().getBoolean(EDIT_ENABLED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_actions_menu_tools, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActionsMenuMV = new ViewModelProvider(getActivity())
                .get(ActionsMV.class);

        setUI(view);
    }

    /** UI **/
    private void setUI(View view) {
        setWidgets(view);
        setListeners();
    }

    private void setListeners() {
        mSearch.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.SEARCH);
        });
        mCamera.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.ADD_FROM_CAMERA);
        });
        mEdit.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.EDIT);
        });
        mTrash.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.REMOVE);
        });
        mDeny.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.DENY);
        });
        mLock1.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.BLOCK_TOOLS);
        });
        mLock2.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.BLOCK_TOOLS);
        });
        mAdd.setOnClickListener(view -> {
            mActionsMenuMV.setAction(ActionsMV.ActionMVType.ADD_CAT);
        });
    }

    private void setWidgets(View view) {
        mSearchActions = view.findViewById(R.id.search_actions_layout);
        mEditActions = view.findViewById(R.id.edit_actions_layout);
        setActionsVisibility();

        mSearch = (ImageButton) view.findViewById(R.id.actions_menu_search_btn);
        mCamera = (ImageButton) view.findViewById(R.id.actions_menu_camera_btn);
        mEdit = (ImageButton) view.findViewById(R.id.actions_menu_edit_btn);
        mTrash = (ImageButton) view.findViewById(R.id.actions_menu_trash_btn);
        mDeny = (ImageButton) view.findViewById(R.id.actions_menu_deny_btn);
        mLock1 = (ImageButton) view.findViewById(R.id.actions_menu_lock_1_btn);
        mLock2 = (ImageButton) view.findViewById(R.id.actions_menu_lock_2_btn);
        mAdd = (ImageButton) view.findViewById(R.id.actions_menu_add_1_btn);

    }


    /** Menu type */
    private void setActionsVisibility() {

        // Check params

        if(mAddEnabled) // show Add tools (search, camera...)
            mSearchActions.setVisibility(View.VISIBLE);
        else
            mSearchActions.setVisibility(View.GONE);

        if(mEditEnabled)    // show Edit tools (edit, remove, deny...)
            mEditActions.setVisibility(View.VISIBLE);
        else
            mEditActions.setVisibility(View.GONE);
    }


}