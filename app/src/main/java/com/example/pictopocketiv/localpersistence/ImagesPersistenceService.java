package com.example.pictopocketiv.localpersistence;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagesPersistenceService {

    /** Attrs **/
    private static final String TAG = ImagesPersistenceService.class.getSimpleName();


    /** Sync storage **/
    @NonNull
    private static String storePictoImage(int id, Bitmap bmp, String appName ) throws IOException {

        String fileName = getPictoFileName(id + "");
        String path = getPictoImagesPath(appName);
        return storeImage(fileName,path,bmp);
    }

    @NonNull
    private static String storeImage(String fileName, String path, Bitmap bmp ) throws IOException {

        File picsDir = new File(path);

        if(!picsDir.exists())  // create dir if doesn't exists
            picsDir.mkdirs();

        File imgFile = new File(picsDir, fileName);

        /*if(imgFile.exists()) {
            imgFile.delete();
            imgFile = new File(picsDir,fileName);
        }*/

        if(!imgFile.exists()) {
            FileOutputStream fout = new FileOutputStream(imgFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.close();
        }



        return imgFile.getAbsolutePath();

    }

    private static Bitmap getStoredPictoBMP(int pictoId, String packageName) throws IOException {

        String imgFile = getPictoImagesPath(packageName) + getPictoFileName(String.valueOf(pictoId));

        return Picasso.get().load(new File(imgFile)).get();

    }


    /** Async storage **/
    public static  class StorePictoImageAsync extends AsyncTask<Void, Void, String> {


        public interface OnStored {
            void onSuccess(String url);
            void onFailure(Throwable t);
        }

        private OnStored mOnStored;
        private Bitmap mBmp;
        private int mId;
        private String mAppName;


        public StorePictoImageAsync(OnStored onStored, Bitmap bm, int id, String appName) {
            this.mOnStored = onStored;
            this.mBmp = bm;
            this.mId = id;
            this.mAppName = appName;
        }

        @Override
        protected String doInBackground(Void... voids) {

            if(mBmp != null ) {

                try {
                    String imgUrl = storePictoImage(mId, mBmp, mAppName);
                    return imgUrl;
                } catch (IOException e) {
                    e.printStackTrace();
                    if(mOnStored != null)
                        mOnStored.onFailure(e);
                }
            } else {

                Exception e = new Exception("Bitmap = NULL");
                Log.e(TAG, e.getMessage());
                if(mOnStored != null ) {
                    mOnStored.onFailure(e);
                }
            }

            return null;



        }

        @Override
        protected void onPostExecute(String imgUrl) {
            super.onPostExecute(imgUrl);
            if(mOnStored != null)
                mOnStored.onSuccess(imgUrl);
        }
    }

    public static class GetStoredPictoBmpAsync extends AsyncTask<Integer,Void,Bitmap> {

        private static final String TAG = GetStoredPictoBmpAsync.class.getSimpleName();

        public interface OnGetStoredPicto {
            void onSuccess(Bitmap bmp);
            void onFailure(Throwable t);
        }

        private final String mPackageName;
        private final OnGetStoredPicto mOnGet;

        public GetStoredPictoBmpAsync(String mPackageName, OnGetStoredPicto mOnGet) {
            this.mPackageName = mPackageName;
            this.mOnGet = mOnGet;
        }

        @Override
        protected Bitmap doInBackground(Integer... pictoIds) {

            int pictoId = pictoIds[0];

            try {
                return getStoredPictoBMP(pictoId,mPackageName);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,e.getMessage());
            }

            return null;

        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(mOnGet != null )
                if(bitmap != null)
                    mOnGet.onSuccess(bitmap);
                else
                    mOnGet.onFailure(new Exception("No bmp"));
        }
    }


    /** Utils **/
    @NonNull
    public static String getPictoFileName(String pictoId) {
        return String.format("%s.png",pictoId);
    }

    @NonNull
    public static String getPictoImagesPath(String appName ) {
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
                + String.format("/%s/",appName);

    }


}
