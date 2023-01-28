package com.example.pictopocketiv.localpersistence;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Populator {

    public static class CategorizedPictos {

        public int cat;
        public int[] ids;
        public String label;
        public int pictoId;
        public String drawable;

        public CategorizedPictos(int cat, int[] ids, String label, int pictoId, String drawable) {
            this.cat = cat;
            this.ids = ids;
            this.label = label;
            this.pictoId = pictoId;
            this.drawable = drawable;
        }

        @Override
        public String toString() {
            return "CategorizedPictos{" +
                    "cat=" + cat +
                    ", ids=" + Arrays.toString(ids) +
                    ", label='" + label + '\'' +
                    ", pictoId=" + pictoId +
                    ", drawable='" + drawable + '\'' +
                    '}';
        }
    }

    public static class CategorizedPictosList {
        public List<CategorizedPictos> pictoCats;

        @Override
        public String toString() {
            return "CategorizedPictosList{" +
                    "pictoCats=" + pictoCats +
                    '}';
        }
    }

    public static void welcomePopulate(
            Context context, String locale,
            int resolution,
            String packageName) throws IOException, ExecutionException, InterruptedException {

        CategorizedPictosList catList = readWelcomePictos(context);

        for (CategorizedPictos pictoCat : catList.pictoCats) {

            PictosPersistenceModel.PictoCategoryInfo categoryInfo =
                    new PictosPersistenceModel.PictoCategoryInfo(
                            pictoCat.cat,pictoCat.label,pictoCat.pictoId,pictoCat.drawable);

            LocalPersistenceService.AddCategoryInfoAsync addCategoryInfoAsync =
                    new LocalPersistenceService.AddCategoryInfoAsync();

            addCategoryInfoAsync.execute(categoryInfo).get();   // Sync

            for (int pictoId : pictoCat.ids) {
                LocalPersistenceService.downloadAddPicto(pictoId, locale, resolution, categoryInfo.id, packageName);
            }
        }
    }

    public static Populator.CategorizedPictosList readWelcomePictos(Context context)
            throws IOException {

        AssetManager assman = context.getAssets();
        InputStream istream = null;

        istream = assman.open("welcome_pictos_bundle.json");

        BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(istream));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = buffReader.readLine()) != null ) {
            strBuilder.append(line);
        }

        String jsonStr = strBuilder.toString();

        Gson gson = new Gson();
        CategorizedPictosList pictos = gson.fromJson(jsonStr,CategorizedPictosList.class);

        return pictos;
    }

}
