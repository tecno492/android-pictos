package com.example.pictopocketiv.addCat;


import com.example.pictopocketiv.MainActivity;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.arasaac.ArasaacService;
import com.example.pictopocketiv.catalogs.PictosCategoriesAdapter;
import com.example.pictopocketiv.localpersistence.Populator;

import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;


import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;


public class AddCatResultAdapter extends RecyclerView.Adapter<AddCatResultAdapter.ResultViewHolder> {

    /** View Holder class **/
    public class ResultViewHolder extends RecyclerView.ViewHolder {

        private final AddCatResultAdapter mAdapter;

        public final View mLayout;
        public final ImageView mImage;
        public final TextView mTitle;
        public final TextView mText;
        public final View mSaveLayout;
        public final ImageButton mSave;


        public ResultViewHolder(@NonNull View itemView, AddCatResultAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            mLayout = itemView.findViewById(R.id.search_result_layout);
            mImage = itemView.findViewById(R.id.search_result_img);
            mTitle = itemView.findViewById(R.id.search_result_title);
            mText = itemView.findViewById(R.id.search_result_text);
            mSave = itemView.findViewById(R.id.search_save_btn);
            mSaveLayout = itemView.findViewById(R.id.search_save_btn_container);
        }

    }


    /** Attrs **/
    private static final String TAG = PictosCategoriesAdapter.class.getSimpleName();
    private final LinkedList<ArasaacModel.Pictogram> mResults;
    private final AppCompatActivity mActivity;
    private final LayoutInflater mInflater;
    private AddCatCategoryDialogFragment mDialogFragment;
    private final String mLocale;
    private final int mResolution;



    /** C **/
    public AddCatResultAdapter(LinkedList<ArasaacModel.Pictogram> pictos,
                               AppCompatActivity activity, LayoutInflater inflater,
                               String locale, int resolution) {
        this.mResults = pictos;
        this.mActivity = activity;
        this.mInflater = inflater;
        this.mLocale = locale;
        this.mResolution = resolution;
    }

    /** Adapter Methods */
    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the item view
        View itemView = mInflater.inflate(R.layout.item_add_result,
                parent, false);

        // Create & return the ViewHolder
        return new ResultViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        // Pictogram
        setUI(holder, position);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    /** UI **/
    private void setUI(@NonNull ResultViewHolder holder, int position) {
        setPictoUI(holder, position);
        setListenersUI(holder, position);
    }

    private void setListenersUI(@NonNull ResultViewHolder holder, int position) {
        AtomicReference<Populator.CategorizedPictosList> a = new AtomicReference<>(new Populator.CategorizedPictosList());
        // ==== On Save
        holder.mSave.setOnClickListener(view -> {
            ArasaacModel.Pictogram p = mResults.get(position);
            System.out.println(p.id);
            mActivity.getPackageName();

            JsonManipulation Json = new JsonManipulation();
            try {
                Json.crearCat("welcome_pictos_bundle.json", mActivity, p, mLocale, 500, mActivity.getPackageName());
                //Json.crearCategoria(p);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mActivity.finish();

        });
    }

    private void setPictoUI(@NonNull ResultViewHolder holder, int position) {

        // Image download task
        ArasaacService.GetPictogramImageAsync getPictogramImageAsync =
                new ArasaacService.GetPictogramImageAsync(null, 500);

        try {
            Bitmap bmp = getPictogramImageAsync.execute(mResults.get(position).id).get(); // exec sync
            // Image
            holder.mImage.setImageBitmap(bmp);
            // Keyword
            setKeywordUI(mResults.get(position), holder);
            // Download Btn
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setKeywordUI(ArasaacModel.Pictogram pictogram, @NonNull ResultViewHolder holder) {
        List<PictosPersistenceModel.Keyword> keywords =
                PictosPersistenceModel.ArasaacAdapter.adaptKeywords(pictogram);
        PictosPersistenceModel.Keyword keyword = keywords.get(0);
        String keywordLabel = "---";
        String keywordText = "";
        if(!keywords.isEmpty()) {
            keywordLabel = keyword.keyword;
            keywordText = keyword.meaning;
        }

        // Set Title & text
        holder.mTitle.setText(keywordLabel);
        holder.mText.setText(keywordText);

    }

}
