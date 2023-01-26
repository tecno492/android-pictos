package com.example.pictopocketiv.addCat;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.arasaac.ArasaacService;
import com.example.pictopocketiv.catalogs.PictosCategoriesAdapter;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        View itemView = mInflater.inflate(R.layout.item_search_result,
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

        // ==== On Save
        holder.mSave.setOnClickListener(view -> {

            // ==== Opens category selection dialog

            // Dialog Listener
            AddCatCategoryDialogFragment.OnDialogListener onDialogListener =
                    new AddCatCategoryDialogFragment.OnDialogListener() {

                        // On category selected
                        @Override
                        public void onCategorySelected(int category) {

                            // Obtains the selected pictogram
                            ArasaacModel.Pictogram p = mResults.get(position);

                            // Downloads the pictogram
                            LocalPersistenceService.downloadAddPicto(p.id,
                                    mLocale, mResolution, category,
                                    mActivity.getPackageName());

                            // Hide the dowload button
                            holder.mSaveLayout.setVisibility(View.GONE);

                            // Close the dialog
                            mDialogFragment.dismiss();
                        }

                        @Override
                        public void onCancel() {
                            mDialogFragment.dismiss();
                        }
                    };

            // Creates dialog
            mDialogFragment = new AddCatCategoryDialogFragment(onDialogListener);
            // Sets the dialog style
            mDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.customDialogStyle);
            // Shows the dialog
            mDialogFragment.show(mActivity.getSupportFragmentManager(),"");
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
