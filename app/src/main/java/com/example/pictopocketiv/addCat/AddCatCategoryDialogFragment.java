package com.example.pictopocketiv.addCat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.catalogs.PictoCategoryInfo;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddCatCategoryDialogFragment extends DialogFragment {

    private LinkedList<PictoCategoryInfo> mCategoriesInfo;
    private RecyclerView mCategoriesRV;
    private AddCatCategoryAdapter mCategoriesAdapter;
    private ImageButton mCancel;


    /** Listener interface **/
    public interface OnDialogListener {
        void onCategorySelected(int category);
        void onCancel();
    }

    /** Attrs **/
    private OnDialogListener mDialogListener;

    /** C **/
    public AddCatCategoryDialogFragment(OnDialogListener mDialogListener) {
        this.mDialogListener = mDialogListener;
    }



    /** Lifecycle **/
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_category_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
        setUI(view);
    }

    /** UI **/
    private void setUI(View view) {
        setWidgets(view);

    }

    private void setWidgets(View view) {
        mCategoriesRV = view.findViewById(R.id.search_categories_recv);
        mCategoriesAdapter = new AddCatCategoryAdapter(
                mCategoriesInfo,this.getContext(),getLayoutInflater(), mDialogListener);
        mCategoriesRV.setAdapter(mCategoriesAdapter);
        mCategoriesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mCancel = view.findViewById(R.id.search_cat_dialog_cancel);
        mCancel.setOnClickListener(onCancel);
    }


    /** Data **/
    private void setData() {
        LocalPersistenceService.GetCategoriesInfoAsync getCategoriesInfoAsync =
                new LocalPersistenceService.GetCategoriesInfoAsync();

        try {
            List<PictosPersistenceModel.PictoCategoryInfo> catsInfo =
                    getCategoriesInfoAsync.execute().get();

            List<PictoCategoryInfo> aCatsInfo = new ArrayList<>();

            for (PictosPersistenceModel.PictoCategoryInfo categoryInfo : catsInfo) {

                LocalPersistenceService.GetPictosByCatCountAsync
                        getPictosByCategoryCountAsync =
                        new LocalPersistenceService.GetPictosByCatCountAsync();

                int catCount = getPictosByCategoryCountAsync.execute(categoryInfo.getId()).get();

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


    /** Actions **/
    private View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDialogListener.onCancel();
        }
    };

}
