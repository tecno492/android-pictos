package com.example.pictopocketiv;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.pictopocketiv.actions.ActionsToolsMenuFragment;
import com.example.pictopocketiv.actions.ActionsMV;
import com.example.pictopocketiv.appconf.AppProperties;
import com.example.pictopocketiv.arasaac.ArasaacService;
import com.example.pictopocketiv.catalogs.PictosCategoriesFragment;
import com.example.pictopocketiv.firebase.FirebaseAuthService;
import com.example.pictopocketiv.firebase.FirebaseService;
import com.example.pictopocketiv.forms.AccessFormFragment;
import com.example.pictopocketiv.forms.MsgFormFragment;
import com.example.pictopocketiv.forms.SignupFormFragment;
import com.example.pictopocketiv.forms.WaitingFragment;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.Populator;
import com.example.pictopocketiv.states.MainActivityStateMV;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /** ATTRS **/
    private static final String TAG = MainActivity.class.getSimpleName();

    /** Permissions **/
    private static final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSION_CODE = 100;

    /** Status **/
    public MainActivityStateMV mActivityMV;    // Activity status model view

    /** ActionBar **/
    private ActionBar mActionBar;

    /** Fragments **/
    private FragmentManager mFramesMan;
    private AccessFormFragment mAccessFormFF;
    private SignupFormFragment mSignupFormFF;
    private WaitingFragment mWaitingFormFF;

    /** Bottom Menu **/
    private BottomSheetBehavior<ConstraintLayout> mToolsBh;
    private ImageButton mActionsToggleBtn;
    private ImageButton mUnlockActionsBtn;
    private ConstraintLayout mToolsLayout;


    /** LIFECYCLE **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configApp();    // Config app (p.e. permissions)
        setFirebase();  // Init firebase
        confAuthListener();     // Config Auth Status Listener (from Firebase)
        confActivity();     // Config Activity: Firebase,
                            //   local persistence, arasaac
                            //   api and activity status
        confUI();   // Config UI: ActionBar, frames and user tools menú
    }


    /** APP **/
    private void configApp() {
        setAppPermissions();
    }


    /** UI **/
    private void confUI() {
        //confFakeWidgets();
        //confFakeWidgetsListeners();
        confActionBar();
        confFragments();
        setUserToolsMenu();
    }

    private void confFragments() {
        mFramesMan = getSupportFragmentManager();   // frames manager

        mAccessFormFF = AccessFormFragment.newInstance();
        mSignupFormFF = SignupFormFragment.newInstance();
        mWaitingFormFF = WaitingFragment.newInstance("Por favor, espera");
    }

    // Action Bar:
    private void confActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.hide();
    }


    /** ARASAAC **/
    private void setArasaacService() {

        // Getting properties from properties file
        String arasaacApiUrl = AppProperties.getArasaacAPIProperties(this,"ARASAAC_API");
        String arasaacImgsUrl = AppProperties.getArasaacAPIProperties(this,"ARASAAC_IMAGES_API");
        // Init Arasaac Service
        ArasaacService.initService(arasaacApiUrl,arasaacImgsUrl);     // arasaac service
    }


    /** FIREBASE **/
    private void setFirebase() {
        FirebaseService.init(this);
    }


    /** AUTH **/
    private void confAuthListener() {
        // Add an Firebase auth state change listener
         FirebaseService.fbAuth.addAuthStateListener(onAuthStateChange);
    }

    private FirebaseAuth.AuthStateListener onAuthStateChange =
            new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            Log.d(TAG, "Auth State changed");

            if(firebaseAuth.getCurrentUser() != null) {
                if(mActivityMV.getState().getValue() != MainActivityStateMV.ActivityState.W_DB_POPULATION) {
                    mActivityMV.setState(MainActivityStateMV.ActivityState.W_DB_POPULATION);
                    Log.d(TAG, "Logged");
                }
            } else {
                if(mActivityMV.getState().getValue() !=
                        MainActivityStateMV.ActivityState.W_USER_CREDENTIALS) {
                    mActivityMV.setState(MainActivityStateMV.ActivityState.W_USER_CREDENTIALS);
                    Log.d(TAG, "Unlogged");
                }
            }
        }
    };


    /** LOCAL PERSISTENCE **/
    private void setLocalPersistence() {
        LocalPersistenceService.init(this,"db_pictos_db");    // local persistence
    }


    /** ACTIVITY **/
    private void confActivity() {
        setLocalPersistence();
        setArasaacService();
        confActivityState();
    }

    private void confActivityState() {

        mActivityMV = new ViewModelProvider(this).get(MainActivityStateMV.class);
        mActivityMV.setState(MainActivityStateMV.ActivityState.CH_AUTH_STATE);

        // Add an a auth modelView observer (to listen status changes
        // in another components)
        mActivityMV.getState().observe(this, activityState -> {
            updateActivityState(activityState);
        });
    }

    private void updateActivityState(MainActivityStateMV.ActivityState activityState) {

        Log.d(TAG,String.format("NEW state %s",mActivityMV.getState().getValue()));

        switch (activityState) {
            case LOGGED_IN:
                goLoggedIn();
                break;
            case W_USER_CREDENTIALS:
                goLogin();
                break;
            case W_SIGNUP_CREDENTIALS:
                goSignup();
                break;
            case W_DB_POPULATION:
                goDBPopulation();
                break;
            case CH_AUTH_STATE:
                goCheckAuthState();
                break;
            case W_SIGNUP_RSP:
            case W_USER_CREDENTIALS_RSP:
                goWait();
                break;
            case E_INVALID_CREDENTIALS:
            case E_INVALID_SIGNUP:
            case E_INVALID_USER:
            case E_UNDEFINED:
                goError();
            default:
                break;
        }
    }


    /** ACTIVITY ACTIONS **/
    private void goCheckAuthState() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        ft.replace(R.id.main_frame_layout, mWaitingFormFF);
        mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
    }

    private void goDBPopulation() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        WaitingFragment waitingFragmentFF = WaitingFragment.newInstance("Cargando pictos...");
        ft.replace(R.id.main_frame_layout, waitingFragmentFF);
        //ft.addToBackStack(null);
        ft.commit();
        mFramesMan.executePendingTransactions();


        // If empty DB try to populate
        // Use this to check the response
        LocalPersistenceService.OnPopulateDB onDBPopulated =
                new LocalPersistenceService.OnPopulateDB() {
            @Override
            public void onSuccess(boolean updated) {
                mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
                if(updated)
                    Log.d(TAG,"DB Populated");
                else
                    Log.d(TAG,"DB NO Populted");
            }

            @Override
            public void onFailure(Throwable t) {
                mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
                Log.e(TAG,t.getMessage());
            }
        };
        // Wait to populate before go next state
        LocalPersistenceService.populateDB(
                this, getPackageName(),"es",500, onDBPopulated);
    }

    private void goError() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        setCustomizedError(ft);
        ft.addToBackStack(null);
        ft.commit();
    }



    public Context getContext () {
        return this;
    }

    private void goWait() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        setCustomizeWait(ft);
        ft.addToBackStack(null);
        ft.commit();
        mFramesMan.executePendingTransactions();
    }

    private void goSignup() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        ft.replace(R.id.main_frame_layout, mSignupFormFF);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goLogin() {

        // Load fragment
        FragmentTransaction ft = mFramesMan.beginTransaction();
        ft.replace(R.id.main_frame_layout, mAccessFormFF);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goLoggedIn() {

        // Load fragment
        FragmentTransaction ft = mFramesMan.beginTransaction();
        PictosCategoriesFragment pictosCatsFragment = PictosCategoriesFragment.newInstance();
        ft.replace(R.id.main_frame_layout, pictosCatsFragment);
        ft.addToBackStack(null);
        ft.commit();

        // show unlock actions tools button
        mUnlockActionsBtn.setVisibility(View.VISIBLE);

    }

    private void setCustomizedError(FragmentTransaction ft) {
        switch (mActivityMV.getState().getValue()) {
            case E_INVALID_CREDENTIALS:
                ft.replace(R.id.main_frame_layout, MsgFormFragment.newInstance(
                        "Acceso denegado",
                        "La contraseña no es válida"));
                break;
            case E_INVALID_USER:
                ft.replace(R.id.main_frame_layout, MsgFormFragment.newInstance(
                        "Acceso denegado",
                        "El usuario no existe"));
                break;
            case E_INVALID_SIGNUP:
                ft.replace(R.id.main_frame_layout, MsgFormFragment.newInstance(
                        "Registro no válido",
                        "Los datos de registro no son válidos"));
                break;
        }
    }

    private void setCustomizeWait(FragmentTransaction ft) {
        switch (mActivityMV.getState().getValue()) {
            case W_DB_POPULATION:
                ft.replace(R.id.main_frame_layout, WaitingFragment.newInstance(
                        "Poblando la BD"));
                break;
            case W_USER_CREDENTIALS_RSP:
                ft.replace(R.id.main_frame_layout, WaitingFragment.newInstance(
                        "Espera mientras se abre la sesión"));
                break;
            case W_SIGNUP_RSP:
                ft.replace(R.id.main_frame_layout, WaitingFragment.newInstance(
                        "Espera mientras se crea el nuevo usuario"));
                break;
        }

        mFramesMan.executePendingTransactions();
    }


    /** OPTIONS MENU **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.app_opts_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.app_opts_close_sesion:
                lockActionsMenu();
                mUnlockActionsBtn.setVisibility(View.GONE);
                FirebaseAuthService.logout();
                break;
            case R.id.app_opts_open_sesion:
                // reload
                goDBPopulation();
                break;
        }

        return true;
    }


    /** PERMISSIONS **/
    private void setAppPermissions() {
        checkPermissions(); // app permissions
    }

    private boolean checkPermissions() {

        boolean granted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // Request permissions required in Android 23<=

            for(String permission:permissions) {
                if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED ) {
                    granted = false;
                }
            }

            if(!granted) {   // if all permissions granted

                requestPermissions(permissions,REQUEST_PERMISSION_CODE);
            }
        }

        return granted;
    }

    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQUEST_PERMISSION_CODE) {

            boolean allGranted = true;

            for( int result: grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }

            if(!allGranted)
                Toast.makeText(this,"Permiso denegado", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"Permiso concedido", Toast.LENGTH_LONG).show();

        }
    }


    /** ACTIONS / USER TOOLS MENU **/
    private void setUserToolsMenu() {
        setToolsModelView();
        setToolsWigets();
        setUsertToolsButtonsFragment();
        setUserToolsListeners();
    }

    private void setToolsModelView() {
        ActionsMV toolsModelView = new ViewModelProvider(this).get(ActionsMV.class);

        toolsModelView.getAction().observe(this, actionType -> {
            switch (actionType) {
                case BLOCK_TOOLS:
                    lockActionsMenu();
                    mToolsBh.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    break;
                case SEARCH:
                    lockActionsMenu();
                    launchSearchActivity();
                    break;
                case ADD_CAT:
                    lockActionsMenu();
                    launchAddActivity();
                    break;
            }
        });

        // Add an a auth modelView observer (to listen status changes
        // in another components)
/*        mActivityMV.getStatus().observe(this, activityStatus -> {
            updateActivityState(activityStatus);
        });*/
    }

    private void setToolsWigets() {
        // ==== Bottom menú sheet
        mUnlockActionsBtn = findViewById(R.id.main_unlock_actions_btn);
        mActionsToggleBtn = findViewById(R.id.action_menu_toggle_button);
        mToolsLayout = (ConstraintLayout)findViewById(R.id.actions_menu_sheet_layout);
        mToolsBh = BottomSheetBehavior.from(mToolsLayout);
    }

    private void setUsertToolsButtonsFragment() {
        FragmentTransaction ft = mFramesMan.beginTransaction();
        ActionsToolsMenuFragment frag = ActionsToolsMenuFragment.newInstance(true,false);
        ft.add(R.id.tools_fragment_buttons,frag, String.valueOf(frag));
        ft.commit();
    }

    private void setUserToolsListeners() {
        mUnlockActionsBtn.setVisibility(View.GONE);
        mUnlockActionsBtn.setOnLongClickListener(onUnlockActionsMenu);
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


    /** ACTIONS (CALLBACKS) **/
    private void unlockActionsMenu() {
        mActionBar.show();
        mUnlockActionsBtn.setVisibility(View.GONE);
        mToolsLayout.setVisibility(View.VISIBLE);
    }

    private void lockActionsMenu() {
        mActionBar.hide();
        mUnlockActionsBtn.setVisibility(View.VISIBLE);
        mToolsLayout.setVisibility(View.GONE);
    }

    private void launchSearchActivity() {
        Intent intent = new Intent(this, PictoSearchActivity.class);
        startActivity(intent);
    }

    private void launchAddActivity() {
        Intent intent = new Intent(this, AddCatActivity.class);
        startActivity(intent);
    }

    /** FAKE **/
    /*private TextView mStateOkTxt;
    private Button mOkBtn;
    private Button mKoBtn;
    private Button mKo2Btn;
    private Button mLoginOkBtn;
    private Button mLoginKoBtn;
    private Button mLogoutOkBtn;
    private EditText mUserOkTxt;
    private EditText mPassOkTxt;
    private Button mSignupOkBtn;

    private void confFakeWidgets() {
        mStateOkTxt = (TextView) findViewById(R.id.current_activity_state);
        mOkBtn = (Button) findViewById(R.id.ok_btn);
        mKoBtn = (Button) findViewById(R.id.ko_btn);
        mKo2Btn = (Button) findViewById(R.id.ko2_btn);

        mLoginOkBtn = (Button) findViewById(R.id.login_ok_btn);
        mLoginKoBtn = (Button) findViewById(R.id.login_ko_btn);
        mLogoutOkBtn = (Button) findViewById(R.id.logout_ok_btn);

        mUserOkTxt = (EditText) findViewById(R.id.user_name_ok_txt);
        mPassOkTxt = (EditText) findViewById(R.id.password_ok_txt);
        mSignupOkBtn = (Button) findViewById(R.id.signup_ok_btn);
    }

    private void confFakeWidgetsListeners() {
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
            }
        });
        mKoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
            }
        });
        mKo2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityMV.setStatus(MainActivityStateMV.Signal.KO2);
            }
        });

        // Session
        FirebaseAuthService.OnOpenFirebaseSession onOpenSession = new FirebaseAuthService.OnOpenFirebaseSession() {
            @Override
            public void onSuccess() {
                mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
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

        mLoginOkBtn.setOnClickListener(view -> {

            mActivityMV.setStatus(MainActivityStateMV.ActivityState.W_USER_CREDENTIALS_RSP);

            String email = "qqq@qqqq.com";
            String pass = "123456AAA";


            FirebaseAuthService.openSession(email,pass,onOpenSession);
        });
        mLoginKoBtn.setOnClickListener(view -> {

            mActivityMV.setStatus(MainActivityStateMV.ActivityState.W_USER_CREDENTIALS_RSP);


            String email = "qqxq@qqqq.com";
            String pass = "123456AAA";

            FirebaseAuthService.openSession(email,pass,onOpenSession);
        });
        mLogoutOkBtn.setOnClickListener(view -> {
            mActivityMV.setStatus(MainActivityStateMV.ActivityState.LOGGED_IN);
            FirebaseAuthService.logout();
        });

        // Signup
        FirebaseAuthService.OnUserFirebaseCreation onUserCreation = new FirebaseAuthService.OnUserFirebaseCreation() {
            @Override
            public void onCreationSuccess() {
                mActivityMV.setStatus(MainActivityStateMV.Signal.OK);
            }

            @Override
            public void onCreationFailure(Exception e) {
                Log.d(TAG,e.getMessage());
                e.printStackTrace();
                mActivityMV.setStatus(MainActivityStateMV.Signal.KO);
            }
        };

        mSignupOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityMV.setStatus(MainActivityStateMV.ActivityState.W_SIGNUP_RSP);

                String email = mUserOkTxt.getText().toString();
                String pass = mPassOkTxt.getText().toString();


                FirebaseAuthService.createaAccount(email,pass,onUserCreation);
            }
        });
    }

    private void testPopvlator() throws IOException, ExecutionException, InterruptedException {
        Populator.welcomePopulate(
                this,
                "es",
                null,
                500,
                null,
                null,
                getPackageName()
        );
    }

    private void testArasaacApi() throws Exception {
        Log.d(TAG,"sync test started");
        //ArasaacModel.Pictogram pictogram = ArasaacService.getPictogram(35547, "es");
        //Bitmap pictogramBmp = ArasaacService.getPictogramImage(35547, 500);
        Log.d(TAG,"test finished");

        Log.d(TAG,"test sync started");
        ArasaacService.GetPictogramAsync getPictogramAsync =
                new ArasaacService.GetPictogramAsync("es", null);

        ArasaacModel.Pictogram pictogram = getPictogramAsync.execute(35547).get();


        ArasaacService.GetPictogramImageAsync getPictogramImageAsync =
                new ArasaacService.GetPictogramImageAsync(null, 500);

        Bitmap pictoImg = getPictogramImageAsync.execute(35547).get();

        LocalPersistenceService.AddPictoAsync addPictoAsync =
                new LocalPersistenceService.AddPictoAsync(null,0);

        PictosPersistenceModel.Picto picto = addPictoAsync.execute(pictogram).get();

        ImagesPersistenceService.StorePictoImageAsync storePictoImageAsync =
                new ImagesPersistenceService.StorePictoImageAsync(
                        null,pictoImg,picto.id, getPackageName());

        String imgUrl = storePictoImageAsync.execute().get();



        Log.d(TAG,"test finished");

    }

    private void openFakeSession() {
        mActivityMV.setStatus(MainActivityStateMV.ActivityState.W_USER_CREDENTIALS_RSP);

        String email = "qqq@qqqq.com";
        String pass = "123456AAA";

        FirebaseAuthService.openSession(email,pass,null);
    }*/

}