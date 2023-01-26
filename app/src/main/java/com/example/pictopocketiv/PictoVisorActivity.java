package com.example.pictopocketiv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.pictopocketiv.actions.ActionsToolsMenuFragment;
import com.example.pictopocketiv.actions.ActionsMV;
import com.example.pictopocketiv.visor.PictoVisorFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PictoVisorActivity extends AppCompatActivity {


    private ActionsMV mToolsModelView;
    private BottomSheetBehavior<ConstraintLayout> mToolsBh;
    private ConstraintLayout mToolsLayout;
    private FragmentContainerView mToolsButtons;
    private ImageButton mActionsToggleBtn;

    public static final String EXTRA_PICTO_ID = "EXTRA_PICTO_ID";
    private int mPictoId;
    private ImageButton mBack;
    private FrameLayout mFramesLayot;
    private FragmentManager mFramesManager;
    private PictoVisorFragment mVisorFragment;
    private ImageView mUnlock;
    private ActionBar mActionBar;

    /** Lifecycle **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picto_visor);

        recoverIntent();
        setUI();
    }


    /** UI **/
    private void setUI() {
        confActionBar();
        setWidgets();
        setUserToolsMenu();

    }

    private void confActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.hide();
    }


    private void setWidgets() {
        mBack = (ImageButton) findViewById(R.id.visor_back_btn);
        mBack.setOnClickListener(view -> {
            finish();
        });

        mFramesLayot = (FrameLayout) findViewById(R.id.visor_frame_layout);  // frames layout
        mFramesManager = getSupportFragmentManager();   // frames manager

        mVisorFragment = PictoVisorFragment.newInstance(mPictoId,false); // Fragment
        FragmentTransaction ft = mFramesManager.beginTransaction();
        ft.replace(R.id.visor_frame_layout, mVisorFragment);
        ft.commit();
    }


    /** Recover Intent **/
    private void recoverIntent() {
        Intent intent = getIntent();
        mPictoId = intent.getExtras().getInt(EXTRA_PICTO_ID, -1);
    }


    /** USER TOOLS MENU **/
    private void setUserToolsMenu() {
        setToolsModelView();
        setToolsWigets();
        setUsertToolsButtonsFragment();
        setUsertToolsListeners();
    }

    private void setToolsModelView() {
        mToolsModelView = new ViewModelProvider(this).get(ActionsMV.class);

        mToolsModelView.getAction().observe(this, actionType -> {
            switch (actionType) {
                case BLOCK_TOOLS:
                    lockActionsMenu();
                    mToolsBh.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        });

    }

    private void setToolsWigets() {
        // ==== Bottom menú sheet
        mActionsToggleBtn = findViewById(R.id.action_menu_toggle_button);
        mToolsLayout = (ConstraintLayout)findViewById(R.id.actions_menu_sheet_layout);
        mToolsBh = BottomSheetBehavior.from(mToolsLayout);
        mToolsButtons = (FragmentContainerView) findViewById(R.id.tools_fragment_buttons);
        mUnlock = findViewById(R.id.visor_unlock_actions_btn);
    }

    private void setUsertToolsButtonsFragment() {
        FragmentTransaction ft = mFramesManager.beginTransaction();
        ActionsToolsMenuFragment frag = ActionsToolsMenuFragment.newInstance(false,true);
        ft.add(R.id.tools_fragment_buttons,frag, String.valueOf(frag));
        ft.commit();
    }

    private void setUsertToolsListeners() {
        mUnlock.setOnLongClickListener(onUnlockActionsMenu);
        mActionsToggleBtn.setOnClickListener(onToggleActionsMenu);
        mToolsBh.addBottomSheetCallback(onActionsMenuAction);
    }

    private View.OnClickListener onToggleActionsMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mToolsBh.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                mToolsBh.setState(BottomSheetBehavior.STATE_EXPANDED);

            } else {
                mToolsBh.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    };
    private BottomSheetBehavior.BottomSheetCallback onActionsMenuAction =
            new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_EXPANDED:
                            mActionsToggleBtn.setImageDrawable(getDrawable(
                                    R.drawable.ic_menu_actions_arrow_down_24));
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            mActionsToggleBtn.setImageDrawable(getDrawable(
                                    R.drawable.ic_menu_actions_arrow_up_24));
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            };

    private View.OnLongClickListener onUnlockActionsMenu = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            unlockActionsMenu();

            return true;
        }
    };

    private void unlockActionsMenu() {
        mBack.setVisibility(View.GONE);
        mUnlock.setVisibility(View.GONE);
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void lockActionsMenu() {
        mBack.setVisibility(View.VISIBLE);
        mActionBar.hide();
        mUnlock.setVisibility(View.VISIBLE);
        mToolsLayout.setVisibility(View.GONE);
    }
}