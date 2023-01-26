package com.example.pictopocketiv.catalogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.PictosCatalogActivity;
import com.example.pictopocketiv.R;
import com.example.pictopocketiv.localpersistence.ImagesPersistenceService;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class PictosCategoriesAdapter extends
        RecyclerView.Adapter<PictosCategoriesAdapter.PictoCategoryViewHolder> {



    /** View holder class **/
    public class PictoCategoryViewHolder extends RecyclerView.ViewHolder {

        private final PictosCategoriesAdapter mAdapter;

        public final ImageView mImage;
        public final ImageView mArrow;
        public final View mLayout;

        public PictoCategoryViewHolder(@NonNull View itemView,
                                       PictosCategoriesAdapter pictosCategoriesAdapter) {
            super(itemView);
            this.mAdapter = pictosCategoriesAdapter;
            this.mImage = itemView.findViewById(R.id.catalog_categ_item_img);
            this.mArrow = itemView.findViewById(R.id.catalog_categ_item_arrow_img);
            this.mLayout = itemView.findViewById(R.id.catg_catl_item);
        }

    }

    /** Attrs **/
    private static final String TAG = PictosCategoriesAdapter.class.getSimpleName();
    private final LinkedList<PictoCategoryInfo> mCategoriesInfo;
    private final Context mContext;
    private final LayoutInflater mInflater;


    /** C **/
    public PictosCategoriesAdapter(LinkedList<PictoCategoryInfo> categoriesInfo,
                                   Context context, LayoutInflater inflater) {
        this.mCategoriesInfo = categoriesInfo;
        this.mContext = context;
        this.mInflater = inflater;
    }


    /** Adapter **/
    @NonNull
    @Override
    public PictoCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflates the item view
        View itemView = mInflater.inflate(R.layout.item_categories_catalog,
                parent, false);

        // Create & return the ViewHolder
        return new PictoCategoryViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PictoCategoryViewHolder holder, int position) {
        setUI(holder,position);
        setListeners(holder,position);
    }

    @Override
    public int getItemCount() {
        return mCategoriesInfo.size();
    }


    /** UI **/
    private void setUI(@NonNull PictoCategoryViewHolder holder, int position) {
        // Dump the data in the item:
        PictoCategoryInfo catInfo = mCategoriesInfo.get(position);   // extracts the category info
        setArrowImg(catInfo, holder);
        setCatImg(catInfo, holder);
    }

    private void setListeners(@NonNull PictoCategoryViewHolder holder, int position) {
        holder.mLayout.setOnClickListener(view -> {
            PictoCategoryInfo catInfo = mCategoriesInfo.get(position);

            if(catInfo.count > 0) {
                Intent intent = new Intent(view.getContext(), PictosCatalogActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(PictosCatalogActivity.EXTRA_CAT_ID, catInfo.category);
                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });
    }

    private void setCatImg(@NonNull PictoCategoryInfo catInfo, PictoCategoryViewHolder holder) {

        // Image
        if(catInfo.drawable != null & !catInfo.drawable.isEmpty()) {
            int drawId = mContext.getResources().getIdentifier(
                    catInfo.drawable, "drawable", mContext.getPackageName());
            holder.mImage.setImageDrawable(mContext.getDrawable(drawId));
        } else {
            ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto onGetPictoImg =
                    new ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto() {
                        @Override
                        public void onSuccess(Bitmap bmp) {
                            holder.mImage.setImageBitmap(bmp);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG,t.getMessage());
                        }
                    };

            ImagesPersistenceService.GetStoredPictoBmpAsync getStoredPictoBmpAsync =
                    new ImagesPersistenceService.GetStoredPictoBmpAsync(
                            mContext.getPackageName(),onGetPictoImg);

            try {
                getStoredPictoBmpAsync.execute(catInfo.pictoId).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setArrowImg(@NonNull PictoCategoryInfo catInfo, PictoCategoryViewHolder holder) {
        // Arrow
        if(catInfo.count > 0) {
            // Set the arrow image
            holder.mArrow.setImageDrawable(mContext.getDrawable(R.drawable.right_arrow_w50h50));
        } else {
            holder.mArrow.setImageDrawable(mContext.getDrawable(R.drawable.transparent_w50h50));
        }
    }

}
