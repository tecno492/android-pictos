package com.example.pictopocketiv.catalogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictosCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictosCategoryFragment extends Fragment {

    /** ATTRS **/
    private static final String CATEGORY = "category";

    private int mCategory;
    private LinkedList<PictosCategoryAdapter.AdapterPicto> mPictos = new LinkedList<>();
    private RecyclerView mRecycView;
    private PictosCategoryAdapter mAdapter;


    /** CONSTRUCTOR **/
    public PictosCategoryFragment() {
        // Required empty public constructor
    }


    /**
     * New instance
     * @param category
     * @return
     */
    public static PictosCategoryFragment newInstance(int category) {
        PictosCategoryFragment fragment = new PictosCategoryFragment();
        fragment.mCategory = category;
        Bundle args = new Bundle();
        args.putInt(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }


    /** LIFECYCLE **/
    /**
     * On create
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getInt(CATEGORY);
        }
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictos_category, container, false);
    }


    /**
     * On View Created
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
        setUI(view);
    }


    /** UI **/
    /**
     * SetUI
     * @param view
     */
    private void setUI(View view) {
        mRecycView = (RecyclerView) view.findViewById(R.id.picto_cats_recview);
        mAdapter = new PictosCategoryAdapter(mPictos,view.getContext(),getLayoutInflater());
        mRecycView.setAdapter(mAdapter);
        int span = mPictos.size() / 2;
        if(span < 1)
            span = 1;
        mRecycView.setLayoutManager(new GridLayoutManager(
                this.getContext(), 2,
                RecyclerView.HORIZONTAL, false));
    }

    /** DATA **/
    /**
     * Set data
     */
    private void setData() {

        LocalPersistenceService.GetPictosByCategoryAsync getPictosByCategoryAsync =
                new LocalPersistenceService.GetPictosByCategoryAsync();

        try {
            List<PictosPersistenceModel.Picto> persistedLs =
                    getPictosByCategoryAsync.execute(mCategory).get();

            List<PictosCategoryAdapter.AdapterPicto> adapterLs = new ArrayList<>();
            for(PictosPersistenceModel.Picto picto : persistedLs) {
                PictosCategoryAdapter.AdapterPicto aPicto =
                        new PictosCategoryAdapter.AdapterPicto(picto.id);
                adapterLs.add(aPicto);
            }

            mPictos = new LinkedList(adapterLs);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}