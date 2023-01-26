package com.example.pictopocketiv.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.catalogs.PictoCategoryInfo;
import com.example.pictopocketiv.localpersistence.ImagesPersistenceService;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class PictoSearchCategoryAdapter extends RecyclerView.Adapter<PictoSearchCategoryAdapter.CategoryViewHolder> {

    /** View holder class **/
    public class CategoryViewHolder extends RecyclerView.ViewHolder {



        public final PictoSearchCategoryAdapter mAdapter;
        public final ImageView mImage;
        public final TextView mText;
        public final View mLayout;


        public CategoryViewHolder(@NonNull View itemView, PictoSearchCategoryAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            this.mImage = itemView.findViewById(R.id.item_cat_dialog_img);
            this.mText = itemView.findViewById(R.id.item_cat_dialog_txt);
            this.mLayout = itemView.findViewById(R.id.item_cat_dialog_layout);
        }
    }

    /** Attrs **/
    private static final String TAG = PictoSearchCategoryAdapter.class.getSimpleName();
    private LinkedList<PictoCategoryInfo> mCategoriesInfo;
    private Context mContext;
    private LayoutInflater mInflater;
    private PictoSearchCategoryDialogFragment.OnDialogListener mDialogListener;


    /** C **/
    public PictoSearchCategoryAdapter(LinkedList<PictoCategoryInfo> categoriesInfo,
                                      Context context, LayoutInflater inflater,
                                      PictoSearchCategoryDialogFragment.OnDialogListener dialogListener) {
        this.mCategoriesInfo = categoriesInfo;
        this.mContext = context;
        this.mInflater = inflater;
        this.mDialogListener = dialogListener;
    }


    /** Adapter **/
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.item_categories_dialog_list, parent, false);

        return new CategoryViewHolder(itemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        setUI(holder, position);
        setListeners(holder, position);
    }

    private void setListeners(CategoryViewHolder holder, int position) {

        PictoCategoryInfo catInfo = mCategoriesInfo.get(position);

        holder.mLayout.setOnClickListener(view ->
                mDialogListener.onCategorySelected(catInfo.category)
        );
    }

    @Override
    public int getItemCount() {
        return mCategoriesInfo.size();
    }


    /** UI **/
    private void setUI(CategoryViewHolder holder, int position) {
        PictoCategoryInfo catInfo = mCategoriesInfo.get(position);
        setCatInfoUI(catInfo, holder);
    }

    private void setCatInfoUI(PictoCategoryInfo catInfo, CategoryViewHolder holder) {

        if(catInfo.drawable != null & !catInfo.drawable.isEmpty()) {    //if drawable
            int drawId = mContext.getResources().getIdentifier(
                    catInfo.drawable, "drawable", mContext.getPackageName());
            holder.mImage.setImageDrawable(mContext.getDrawable(drawId));   // set image drawable
            holder.mText.setText(catInfo.label); // set text
        } else {    // if stored

            // Get stored image task listener
            ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto onGetPictoImg =
                    new ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto() {
                        @Override
                        public void onSuccess(Bitmap bmp) {     // on success sets bitmap & text
                            holder.mImage.setImageBitmap(bmp);
                            holder.mText.setText(catInfo.label);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG,t.getMessage());
                        }
                    };

            // Get stored image task declaration
            ImagesPersistenceService.GetStoredPictoBmpAsync getStoredPictoBmpAsync =
                    new ImagesPersistenceService.GetStoredPictoBmpAsync(
                            mContext.getPackageName(),onGetPictoImg);

            try {
                // Get stored image task execution
                getStoredPictoBmpAsync.execute(catInfo.pictoId).get();  // sync
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
