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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pictopocketiv.PictoVisorActivity;
import com.example.pictopocketiv.R;
import com.example.pictopocketiv.localpersistence.ImagesPersistenceService;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PictosCategoryAdapter extends RecyclerView.Adapter<PictosCategoryAdapter.PictoViewHolder> {

    /** VIEW HOLDER CLASS **/
    public class PictoViewHolder extends RecyclerView.ViewHolder {

        private final PictosCategoryAdapter mAdapter;

        public final ImageView mImage;
        public final TextView mLabel;
        private final View mItemLayout;

        public PictoViewHolder(@NonNull View itemView, PictosCategoryAdapter adapter) {
            super(itemView);
            this.mAdapter = adapter;
            this.mItemLayout = itemView.findViewById(R.id.category_item_layout);
            this.mImage = itemView.findViewById(R.id.catalog_picto_item_img);
            this.mLabel = itemView.findViewById(R.id.catalog_picto_item_label);
        }
    }



    /** PICTO ADAPTER CLASS **/
    public static class AdapterPicto {
        private final int id;

        public AdapterPicto(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }


    /** ATTRS **/
    private static final String TAG = PictosCategoriesAdapter.class.getSimpleName();
    private final LinkedList<AdapterPicto> mPictos;
    private final Context mContext;
    private final LayoutInflater mInflater;


    /** CONSTRUCTOR ++/
    /**
     * Constructor
     * @param mPictos
     * @param mContext
     * @param mInflater
     */
    public PictosCategoryAdapter(LinkedList<AdapterPicto> mPictos,
                                 Context mContext,
                                 LayoutInflater mInflater) {
        this.mPictos = mPictos;
        this.mContext = mContext;
        this.mInflater = mInflater;
    }


    /** LIFECYCLE **/
    @NonNull
    @Override
    public PictoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflates the item view
        View itemView = mInflater.inflate(R.layout.item_pictos_catalog,
                parent, false);

        // Create & return the ViewHolder
        return new PictosCategoryAdapter.PictoViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull PictoViewHolder holder, int position) {
        setUI(holder, position);
    }

    @Override
    public int getItemCount() {
        return mPictos.size();
    }


    /** UI **/
    private void setUI(PictoViewHolder holder, int position) {
        setPictoUI(holder, position);
        setListeners(holder, position);
    }

    private void setListeners(PictoViewHolder holder, int position) {
        holder.mItemLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PictoVisorActivity.class);
            Bundle extras = new Bundle();
            extras.putInt(PictoVisorActivity.EXTRA_PICTO_ID, mPictos.get(position).getId());
            intent.putExtras(extras);
            mContext.startActivity(intent);
        });
    }

    private void setPictoUI(PictoViewHolder holder, int position) {
        AdapterPicto picto = mPictos.get(position);

        /**
         * Get img from storage.
         */
        // Listener
        ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto onGetPictoImg =
                new ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto() {
                    @Override
                    public void onSuccess(Bitmap bmp) {
                        holder.mImage.setImageBitmap(bmp);
                        setLabelUI(holder,position);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG,t.getMessage());
                    }
                };
        // Task
        ImagesPersistenceService.GetStoredPictoBmpAsync getStoredPictoBmpAsync =
                new ImagesPersistenceService.GetStoredPictoBmpAsync(
                        mContext.getPackageName(),onGetPictoImg);
        try {
            // Esec task
            getStoredPictoBmpAsync.execute(picto.id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setLabelUI(PictoViewHolder holder, int position) {
        AdapterPicto picto = mPictos.get(position);

        /** Get label from storage **/
        // Listener
        // Task
        LocalPersistenceService.GetPictoKeywordsAsync getPictoKeywordsAsync =
                new LocalPersistenceService.GetPictoKeywordsAsync();

        try {
            List<PictosPersistenceModel.PictoKeyword> kws =
                    getPictoKeywordsAsync.execute(picto.id).get();

            String label = "---";
            if(kws != null && !kws.isEmpty())
                label = kws.get(0).keyword;

            holder.mLabel.setText(label);
            holder.mLabel.invalidate(); // refresh

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
