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
 * Use the {@link PictosCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictosCategoriesFragment extends Fragment {

    private LinkedList<PictoCategoryInfo> mCategoriesInfo = new LinkedList<>();
    private RecyclerView mCatalogRV;
    private PictosCategoriesAdapter mCatsAdapter;

    public PictosCategoriesFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PictosCategoriesFragment.
     */
    public static PictosCategoriesFragment newInstance() {
        PictosCategoriesFragment fragment = new PictosCategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    // ==== LIFECYCLE ==== //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictos_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
        setUI(view);
    }

    private void setUI(View view) {
        mCatalogRV = (RecyclerView) view.findViewById(R.id.picto_cats_recview);
        mCatsAdapter = new PictosCategoriesAdapter(
                mCategoriesInfo,this.getContext(),getLayoutInflater());
        mCatalogRV.setAdapter(mCatsAdapter);
        mCatalogRV.setLayoutManager(new GridLayoutManager(
                this.getContext(), 2,
                RecyclerView.VERTICAL, false));
    }


    // ==== DATA ==== //
    private void setData() {

        LocalPersistenceService.GetCategoriesInfoAsync getCategoriesInfoAsync =
                new LocalPersistenceService.GetCategoriesInfoAsync();

        try {
            List<PictosPersistenceModel.PictoCategoryInfo> catsInfo =
                    getCategoriesInfoAsync.execute().get(); // sync

            List<PictoCategoryInfo> aCatsInfo = new ArrayList<>();

            for (PictosPersistenceModel.PictoCategoryInfo categoryInfo : catsInfo) {

                LocalPersistenceService.GetPictosByCatCountAsync
                        getPictosByCategoryCountAsync =
                        new LocalPersistenceService.GetPictosByCatCountAsync();

                int catCount = getPictosByCategoryCountAsync.execute(categoryInfo.getId()).get();   // sync

                aCatsInfo.add(new PictoCategoryInfo(
                        categoryInfo.getId(),
                        catCount,categoryInfo.getLabel(),
                        categoryInfo.getPicto(),
                        categoryInfo.getDrawable()));
            }

            mCategoriesInfo = new LinkedList<>(aCatsInfo);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}