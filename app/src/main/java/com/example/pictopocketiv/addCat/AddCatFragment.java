package com.example.pictopocketiv.addCat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.arasaac.ArasaacService;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCatFragment extends Fragment {


    private static final String TAG = AddCatFragment.class.getSimpleName();
    private final LinkedList<ArasaacModel.Pictogram> mResults = new LinkedList<>();
    private EditText mTermTxt;
    private ImageButton mSearchBtn;
    private ImageButton mCancelBtn;
    private AddCatResultAdapter mAdapter;
    private AppCompatActivity mActivity;

    public AddCatFragment() {
        // Required empty public constructor
    }




    /**
     * New Instance
     * @return
     */
    public static AddCatFragment newInstance(AppCompatActivity activity) {
        AddCatFragment fragment = new AddCatFragment();
        fragment.mActivity = activity;
        Bundle args = new Bundle();
        //args.putString(ARG_ACTIVITY, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mTerm = getArguments().getString(ARG_SEARCH_TERM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picto_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUI(view);
    }


    /** UI **/
    private void setUI(View view) {
        setWidgetsUI(view);
        setListenersUI();
    }

    private void setWidgetsUI(@NonNull View view) {
        RecyclerView mResultsRecView = view.findViewById(R.id.result_list_recv);
        // TODO: Get all locale & res from properties file
        mAdapter = new AddCatResultAdapter(mResults, mActivity,
                getLayoutInflater(), "es", 500);
        mResultsRecView.setAdapter(mAdapter);
        mResultsRecView.setLayoutManager(new GridLayoutManager(
                this.getContext(), 1,
                RecyclerView.VERTICAL, false));
        mTermTxt = view.findViewById(R.id.term_pictos_search);
        // btn
        mSearchBtn = view.findViewById(R.id.submit_pictos_search);
        ImageButton mSaveBtn = view.findViewById(R.id.submit_pictos_save);
        mCancelBtn = view.findViewById(R.id.submit_pictos_cancel);
    }

    private void setListenersUI() {
        mSearchBtn.setOnClickListener(view -> {
            try {
                onSearch();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        mCancelBtn.setOnClickListener(view -> onCancelActivity());
    }

    private void hideKeyboardUI() {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getView();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /** Actions **/
    private void onCancelActivity() {
        hideKeyboardUI();
        getActivity().finish(); // finish the activity
    }

    private void onSearch() throws ExecutionException, InterruptedException {

        hideKeyboardUI();

        String term = mTermTxt.getText().toString();

        if(term.length() > 0)
            setData(term);
    }


    /** Data **/
    private void setData(String term) {

        // ==== Searching pictograms by term
        // Task declaration
        ArasaacService.GetPictogramsBySearchTerm getPictogramsBySearchTerm =
                new ArasaacService.GetPictogramsBySearchTerm("es");

        try {

            // Task execution (async)
            List<ArasaacModel.Pictogram> pictograms = getPictogramsBySearchTerm.execute(term).get();
            mResults.clear();   // clear old results

            // dump results
            for (ArasaacModel.Pictogram pictogram : pictograms) {
                mResults.add(pictogram);
                Log.d(TAG,pictogram.toString());
            }
            // notify data change to adapter (refresh the UI)
            mAdapter.notifyDataSetChanged();
        } catch(Exception e ) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }
}