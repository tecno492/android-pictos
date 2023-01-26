package com.example.pictopocketiv.visor;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pictopocketiv.R;
import com.example.pictopocketiv.localpersistence.ImagesPersistenceService;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictoVisorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictoVisorFragment extends Fragment {

    /** Attrs **/

    private static final String TAG = PictoVisorFragment.class.getSimpleName();
    private static final String PICTO_ID = "PICTO_ID";
    private static final String EDITION_ENABLED = "PICTO_2";

    private int mPictoId;
    private boolean mEditionEnabled;
    private ImageView mImg;
    private TextView mLabel;

    /**
     * Constructor
     */
    public PictoVisorFragment() {
        // Required empty public constructor
    }


    /**
     * New Instance
     * @param pictoId
     * @param editionEnabled
     * @return
     */
    public static PictoVisorFragment newInstance(int pictoId, boolean editionEnabled) {
        PictoVisorFragment fragment = new PictoVisorFragment();
        fragment.setPictoId(pictoId);
        fragment.setEditionEnabled(editionEnabled);
        Bundle args = new Bundle();
        args.putInt(PICTO_ID, pictoId);
        args.putBoolean(EDITION_ENABLED, editionEnabled);
        fragment.setArguments(args);
        return fragment;
    }


    /** Lifecycle +*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            mPictoId = b.getInt(PICTO_ID);
            mEditionEnabled = b.getBoolean(EDITION_ENABLED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picto_visor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUI(view);
    }


    /** UI **/
    private void setUI(View view) {
        setPictoUI(view);
    }

    private void setPictoUI(View view) {

        mImg = (ImageView) view.findViewById(R.id.visor_img);
        // Listener
        ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto onGetPictoImg =
                new ImagesPersistenceService.GetStoredPictoBmpAsync.OnGetStoredPicto() {
                    @Override
                    public void onSuccess(Bitmap bmp) {
                        mImg.setImageBitmap(bmp);
                        setLabelUI(view);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG,t.getMessage());
                    }
                };
        // Task
        ImagesPersistenceService.GetStoredPictoBmpAsync getStoredPictoBmpAsync =
                new ImagesPersistenceService.GetStoredPictoBmpAsync(
                        view.getContext().getPackageName(),onGetPictoImg);
        try {
            // Esec task
            getStoredPictoBmpAsync.execute(mPictoId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setLabelUI(View view) {
        mLabel = (TextView) view.findViewById(R.id.visor_label);

        LocalPersistenceService.GetPictoKeywordsAsync getPictoKeywordsAsync =
                new LocalPersistenceService.GetPictoKeywordsAsync();

        try {
            List<PictosPersistenceModel.PictoKeyword> kws =
                    getPictoKeywordsAsync.execute(mPictoId).get();

            String label = "---";
            if(kws != null && !kws.isEmpty())
                label = kws.get(0).keyword;

            mLabel.setText(label);
            mLabel.invalidate(); // refresh

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /** Getters & Setters **/
    public int getPictoId() {
        return mPictoId;
    }

    public void setPictoId(int pictoId) {
        this.mPictoId = pictoId;
    }

    public boolean isEditionEnabled() {
        return mEditionEnabled;
    }

    public void setEditionEnabled(boolean editionEnabled) {
        this.mEditionEnabled = editionEnabled;
    }
}