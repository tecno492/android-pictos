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
import android.widget.ImageButton;

import com.example.pictopocketiv.actions.ActionsToolsMenuFragment;
import com.example.pictopocketiv.actions.ActionsMV;
import com.example.pictopocketiv.catalogs.PictosCategoryFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class PictosCatalogActivity extends AppCompatActivity {

    /** ATTRS **/
    public final static String EXTRA_CAT_ID = "EXTRA_CAT_ID";
    private int mCategoryId;
    private FragmentManager mFramesManager;
    private PictosCategoryFragment mCatalogFragment;
    private ImageButton mBack;
    private ActionBar mActionBar;
    private ImageButton mUnlock;
    private ActionsMV mToolsModelView;
    private BottomSheetBehavior<ConstraintLayout> mToolsBh;
    private ConstraintLayout mToolsLayout;
    private FragmentContainerView mToolsButtons;
    private ImageButton mActionsToggleBtn;


    /** LIFECYCLE **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictos_catalog);

        recoverIntent();
        setUI();
        setUserToolsMenu();

    }


    /** UI **/
    private void setUI() {
        confActionBar();
        setWidgets();
        setCatalog();

    }

    private void confActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.hide();
    }

    private void setWidgets() {
        mBack = findViewById(R.id.pictos_catalog_back_btn);
        mUnlock = findViewById(R.id.pictos_catalog_unlock_actions_btn);
        
        setWidgetsListeners();
    }

    private void setWidgetsListeners() {
        mBack.setOnClickListener(view -> {
            finish();
        });
    }


    /** CATALOG **/
    private void setCatalog() {
        mFramesManager = getSupportFragmentManager();   // frames manager
        mCatalogFragment = PictosCategoryFragment.newInstance(mCategoryId); // Fragment
        FragmentTransaction ft = mFramesManager.beginTransaction();
        ft.replace(R.id.pictos_catalog_frame_layout, mCatalogFragment);
        ft.commit();
    }


    /** INCOMING INTENT **/
    private void recoverIntent() {
        Intent intent = getIntent();
        mCategoryId = intent.getExtras().getInt(PictosCatalogActivity.EXTRA_CAT_ID, -1);
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