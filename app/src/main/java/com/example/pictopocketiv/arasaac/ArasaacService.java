package com.example.pictopocketiv.arasaac;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pictopocketiv.images.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

public class ArasaacService {

    /** ATTRS **/
    private static final String TAG = ArasaacService.class.getSimpleName();
    private static ArasaacApiService apiService;
    private static String imgsUrl;


    /** Init service **/
    public static void initService(String apiURL, String imgsURL) {
        apiService = ArasaacApiClient.getClient(apiURL).create(ArasaacApiService.class);
        imgsUrl = imgsURL;
    }


    /** Pictorams **/
    private static ArasaacModel.Pictogram getPictogram(int pictoID, String locale) throws Exception {

        Call<ArasaacModel.Pictogram> pictogramCall = apiService.getPicto(locale,pictoID);

        Response<ArasaacModel.Pictogram> pictogramResponse = pictogramCall.execute(); // sync

        if(pictogramResponse.isSuccessful()) {
            return pictogramResponse.body();
        } else {
            throw new Exception(
                    String.format("Get pictogram error: response code: %s",
                            pictogramResponse.code()));
        }
    }

    private static List<ArasaacModel.Pictogram> getPictogramsBySearchTerm(
            String searchTerm, String locale) throws Exception {

        Call<List<ArasaacModel.Pictogram>> listCall = apiService.search(locale, searchTerm);

        Response<List<ArasaacModel.Pictogram>> pictogramsResponse = listCall.execute();

        if(pictogramsResponse.isSuccessful()) {
            return pictogramsResponse.body();
        } else {
            throw new Exception(
                    String.format("Get pictogram error: response code: %s",
                            pictogramsResponse.code()));
        }
    }


    /** Images **/
    private static Bitmap getPictogramImage(int pictoID, int resolution ) throws IOException {
        String imgUrl = getPictogramImageURL(pictoID, resolution);
        return ImageUtils.downloadImageSync(imgUrl);
    }


    /** Pictograms Async **/
    public static class GetPictogramAsync extends AsyncTask<Integer,Void, ArasaacModel.Pictogram> {

        // --- Interface for asyncronous responses --- //
        public interface OnArasaacResponse {
            void onSuccess(ArasaacModel.Pictogram pictogram) throws ExecutionException, InterruptedException;
            void onFailure(Throwable t);
        }

        private String mLocale; // locale for query
        private OnArasaacResponse mOnArasaacResponse;

        // --- C --- //
        public GetPictogramAsync(@NonNull String locale,
                                 GetPictogramAsync.OnArasaacResponse onArasaacResponse) {
            this.mLocale = locale;
            this.mOnArasaacResponse = onArasaacResponse;
        }

        // --- Do in backgroound --- //
        @Override
        protected ArasaacModel.Pictogram doInBackground(@NonNull Integer... ids) {

            int pictoId = ids[0];

            ArasaacModel.Pictogram pictogram = null;
            try {
                pictogram = getPictogram(pictoId, mLocale);
            } catch (Exception e) {
                e.printStackTrace();
                if(mOnArasaacResponse != null)
                    mOnArasaacResponse.onFailure(e);
            }

            return pictogram;
        }

        // --- On post execute --- //
        @Override
        protected void onPostExecute(ArasaacModel.Pictogram pictogram) {
            super.onPostExecute(pictogram);
            if(mOnArasaacResponse != null ) {
                try {
                    mOnArasaacResponse.onSuccess(pictogram);
                } catch (ExecutionException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.e(TAG,e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static class GetPictogramsBySearchTerm extends AsyncTask<String, Void, List<ArasaacModel.Pictogram>> {

        private String mLocale;

        public GetPictogramsBySearchTerm(String mLocale) {
            this.mLocale = mLocale;
        }

        @Override
        protected List<ArasaacModel.Pictogram> doInBackground(String... terms) {
            try {
                return getPictogramsBySearchTerm(terms[0],mLocale);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /** Image Async **/
    public static class GetPictogramImageAsync extends AsyncTask<Integer, Void, Bitmap> {


        // --- Response interface --- //
        public interface OnImageDownloaded {
            void onSuccess(Bitmap bm);
            void onFailure(Throwable t);
        }

        private OnImageDownloaded mOnImageDownloaded;   // for async
        private int mResolution;

        // ---- C ---- //
        public GetPictogramImageAsync(OnImageDownloaded onImageDownloaded, @NonNull int resolution) {
            this.mOnImageDownloaded = onImageDownloaded;
            this.mResolution = resolution;
        }

        // ---- Do in background --- //
        @Override
        protected Bitmap doInBackground(@NonNull Integer... ids) {

            int pictoId = ids[0];

            try {
                Bitmap bm = getPictogramImage(pictoId,mResolution);
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                if(mOnImageDownloaded != null )
                    mOnImageDownloaded.onFailure(e);
            }
            return null;
        }

        // ---- Post execute ---- //
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(mOnImageDownloaded != null)
                mOnImageDownloaded.onSuccess(bitmap);
        }
    }


    /** Utils **/
    @NonNull
    private static String getPictogramImageURL(int pictoID, int resolution) {

        return String.format("%s/%s/%s_%s.png",
                imgsUrl, pictoID, pictoID, resolution);
    }

}