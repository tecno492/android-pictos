package com.example.pictopocketiv.addCat;




import android.content.Context;
import android.content.res.AssetManager;

import androidx.room.Query;
import androidx.room.Room;

import com.example.pictopocketiv.arasaac.ArasaacModel;
import com.example.pictopocketiv.localpersistence.LocalPersistenceService;
import com.example.pictopocketiv.localpersistence.PictosPersistenceModel;
import com.example.pictopocketiv.localpersistence.Populator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;


public class JsonManipulation {
    public Populator.CategorizedPictosList pictos;

    public void crearCat(String filePath, Context context, ArasaacModel.Pictogram p, String locale, int resolution,
                          String packageName)throws IOException, JSONException, ExecutionException, InterruptedException {

        AssetManager assman = context.getAssets();
        InputStream istream = null;
        int cat = -1;

        // Crea una nueva instancia de File para el archivo JSON
        istream = assman.open(filePath);

        BufferedReader buffReader = new BufferedReader(new InputStreamReader(istream));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = buffReader.readLine()) != null) {
            strBuilder.append(line);
        }
        buffReader.close();

        String jsonStr = strBuilder.toString();

        System.out.println(jsonStr);

        // Convierte la cadena vacía en un objeto JSONArray
        Gson gson = new Gson();
        Populator.CategorizedPictosList pictos = gson.fromJson(jsonStr, Populator.CategorizedPictosList.class);

        System.out.println(pictos.pictoCats.get(pictos.pictoCats.size() - 1).cat);

        Populator.CategorizedPictos new_cat = new Populator.CategorizedPictos(pictos.pictoCats.get(pictos.pictoCats.size() - 1).cat + 1, new int[]{(p.id)}, p.keywords.get(0).keyword, p.id, "");
        System.out.println(new_cat);

        pictos.pictoCats.add(new_cat);

        int last_cat;

        if (cat == -1){
            last_cat = new_cat.cat;
        }else {
            last_cat = cat;
        }


        while (pictos.pictoCats.size() != 1) {
            pictos.pictoCats.remove(0);
        }
        System.out.println(pictos.pictoCats.get(pictos.pictoCats.size() - 1));

        for (Populator.CategorizedPictos pictoCat : pictos.pictoCats) {
            boolean done = false;
            PictosPersistenceModel.PictoCategoryInfo categoryInfo = null;

            while (!done) {

                categoryInfo = new PictosPersistenceModel.PictoCategoryInfo(
                        last_cat + 1, pictoCat.label, pictoCat.pictoId, pictoCat.drawable);
                LocalPersistenceService.AddCategoryInfoAsync addCategoryInfoAsync =
                        new LocalPersistenceService.AddCategoryInfoAsync();

                addCategoryInfoAsync.execute(categoryInfo).get();

                done = true;

            }
            last_cat ++;
            cat = last_cat;


        }
    }
}
