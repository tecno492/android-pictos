package com.example.pictopocketiv.localpersistence;

import static com.example.pictopocketiv.localpersistence.PictosPersistenceModel.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.arasaac.ArasaacService;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocalPersistenceService {

    /** Attrs **/
    private static final String TAG = LocalPersistenceService.class.getSimpleName();
    private static PictosDB pictosDB;
    private static Dao pictosDAO;


    /** Init **/
    public static void init(Context context, String dbName) {
        pictosDB = Room.databaseBuilder(context,
                PictosDB.class,
                dbName).build();

        pictosDAO = pictosDB.dao();
    }


    /** Insert Ops **/
    private static Picto addArasaacPictogram(ArasaacModel.Pictogram pictogram, int category ) {

        Picto picto = null;

        if (pictogram != null) {
            picto =
                    ArasaacAdapter.adapt(pictogram, category);
            List<Keyword> keywords =
                    ArasaacAdapter.adaptKeywords(pictogram);

            List<PictoKeyword> pictosKws =
                    ArasaacAdapter.adaptPictosKws(picto,keywords);

            addPicto(picto);
            addKeywords(keywords);
            addpictosKws(pictosKws);
        }

        return picto;

    }

    private static void addpictosKws(List<PictoKeyword> pictosKws) {
        for (PictoKeyword pictoKw : pictosKws) {
            try {
                pictosDAO.addPictoKeyword(pictoKw);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private static void addKeywords(List<Keyword> keywords) {
        for (Keyword keyword : keywords) {
            try {
                pictosDAO.addKeyword(keyword);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private static void addPicto(Picto picto) {
        try {
            pictosDAO.addPicto(picto);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private static void addUpdate() {
        Date now = new Date();

        pictosDAO.addUpdate(new DBUpdate(now.getTime()));
    }

    private static void addPictoCategoryInfo(PictoCategoryInfo categoryInfo) {
        try {
            pictosDAO.addCategoryInfo(categoryInfo);
        } catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }

    }

    public static boolean downloadAddPicto(int pictoId, String locale, int resolution,
                                         int category, String packageName) {

        boolean downloaded = false;

        // Get pictogram Task listener //
        ArasaacService.GetPictogramAsync.OnArasaacResponse onArasaacResponse =
                new ArasaacService.GetPictogramAsync.OnArasaacResponse() {
            @Override
            public void onSuccess(ArasaacModel.Pictogram pictogram)
                    throws ExecutionException, InterruptedException {
                    // try to download the image
                    // Task declaration
                    ArasaacService.GetPictogramImageAsync getPictogramImageAsync =
                            new ArasaacService.GetPictogramImageAsync(null, resolution);
                    Bitmap pictoImg = getPictogramImageAsync.execute(pictoId).get();    // Task execution (Async)

                    // try to store pictogram image //
                    // Task declaration
                    ImagesPersistenceService.StorePictoImageAsync storePictoImageAsync =
                            new ImagesPersistenceService.StorePictoImageAsync(
                                    null,pictoImg,pictoId, packageName);
                    String imgPath = storePictoImageAsync.execute().get();      // Task execution (Async)

                    if(imgPath != null) {   // if img persisted
                        // try to persists pictogram //
                        // Task declaration
                        LocalPersistenceService.AddPictoAsync addPictoAsync =
                                new LocalPersistenceService.AddPictoAsync(null,category);
                        addPictoAsync.execute(pictogram).get();  // Task execution (Async)
                    }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        };

        ArasaacService.GetPictogramAsync getPictogramAsync =
                new ArasaacService.GetPictogramAsync(locale,onArasaacResponse);

        try {
            getPictogramAsync.execute(pictoId).get(); // Sync
            downloaded = true;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return downloaded;

    }

    /** Get Ops **/
    private static boolean isPopulated() {
        List<DBUpdate> updates = pictosDAO.getUpdates();

        if(updates != null && updates.size() > 0)
            return true;
        else
            return false;
    }

    private static List<PictoCategoryInfo> getPictoCategoriesInfo() {
        try {
            return pictosDAO.getCategoriesInfo();
        } catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }

        return null;
    }

    private static List<Picto> getPictosByCategory(int categoryId ) {
        return pictosDAO.getPictosByCategory(categoryId);
    }

    private static int getPictosByCategoryCount(int categoryId) {
        return pictosDAO.getPictosByCategoryCount(categoryId);
    }

    private static List<PictoKeyword> getPictoKeywords(int pictoId) {
        return pictosDAO.getPictosKewords(pictoId);
    }


    /** Async Insert Ops **/
    public static class AddPictoAsync extends AsyncTask<ArasaacModel.Pictogram, Void, Picto> {

        public interface OnAddPicto {
            void onSuccess(Picto picto);
            void onFailure(Throwable t);
        }

        private OnAddPicto mOnAddPicto;


        private int mCategory;

        public AddPictoAsync(OnAddPicto onAddPicto, int category) {
            this.mOnAddPicto = onAddPicto;
            this.mCategory = category;
        }

        @Override
        protected Picto doInBackground(ArasaacModel.Pictogram... pictograms) {

            Picto picto = addArasaacPictogram(pictograms[0],mCategory);

            return picto;
        }

        @Override
        protected void onPostExecute(Picto picto) {
            super.onPostExecute(picto);
            if(mOnAddPicto != null )
                mOnAddPicto.onSuccess(picto);
        }
    }

    public static class IsPopulatedAsync extends AsyncTask<Void,Void,Boolean> {

        public interface OnResponse {
            void onSuccess(boolean populated);
        }

        private OnResponse mOnResponse;

        public IsPopulatedAsync(OnResponse onResponse) {
            this.mOnResponse = onResponse;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return isPopulated();
        }

        @Override
        protected void onPostExecute(Boolean populated) {
            super.onPostExecute(populated);
            if(mOnResponse != null)
                mOnResponse.onSuccess(populated);
        }
    }

    public static class AddUpdateAsync extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {

            addUpdate();
            return true;
        }
    }

    public static class AddCategoryInfoAsync extends AsyncTask<PictoCategoryInfo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(PictoCategoryInfo... categoryInfos) {

            PictoCategoryInfo categoryInfo = categoryInfos[0];

            addPictoCategoryInfo(categoryInfo);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }



    /** Async Get Ops **/
    public static class GetCategoriesInfoAsync extends AsyncTask<Void, Void, List<PictoCategoryInfo>> {

        @Override
        protected List<PictoCategoryInfo> doInBackground(Void... voids) {
            return getPictoCategoriesInfo();
        }
    }

    public static class GetPictosByCategoryAsync extends AsyncTask<Integer, Void, List<Picto>> {

        @Override
        protected List<Picto> doInBackground(Integer... pictoIds) {

            int pictoId = pictoIds[0];

            return getPictosByCategory(pictoId);

        }
    }

    public static class GetPictosByCatCountAsync extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... categories) {

            return getPictosByCategoryCount(categories[0]);
        }
    }

    public static class GetPictoKeywordsAsync extends AsyncTask<Integer, Void, List<PictoKeyword>> {

        @Override
        protected List<PictoKeyword> doInBackground(Integer... pictoIds) {
            return getPictoKeywords(pictoIds[0]);
        }
    }

    /** Populate **/
    public interface OnPopulateDB {
        void onSuccess(boolean updated);
        void onFailure(Throwable t);
    }

    public static void populateDB(
            Context context, String packageName, String locale,
            int resolution, OnPopulateDB onPopulateDB ) {

        // On check if DB is already populated.
        // If false, launch DB population
        IsPopulatedAsync.OnResponse onPopulatedResponse =
                populated -> {

                    if(!populated) {    // if not populated
                        try {
                            // populate
                            Populator.welcomePopulate(
                                    context,
                                    locale,
                                    resolution,
                                    packageName);

                            AddUpdateAsync addUpdateSync =
                                    new AddUpdateAsync();
                            addUpdateSync.execute();    // async

                            if(onPopulateDB != null) {
                                onPopulateDB.onSuccess(true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            if(onPopulateDB != null) {
                                onPopulateDB.onFailure(e);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            if(onPopulateDB != null) {
                                onPopulateDB.onFailure(e);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            if(onPopulateDB != null) {
                                onPopulateDB.onFailure(e);
                            }
                        }
                    } else {
                        if(onPopulateDB != null) {
                            onPopulateDB.onSuccess(false);
                        }
                    }
                };

        // Task
        IsPopulatedAsync isPopulatedSync =
                new IsPopulatedAsync(onPopulatedResponse);

        // Exec Task:
        try {
            isPopulatedSync.execute().get();  // async
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

