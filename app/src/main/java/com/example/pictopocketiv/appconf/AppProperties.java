package com.example.pictopocketiv.appconf;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.pictopocketiv.arasaac.ArasaacService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    public static String getArasaacAPIProperties(Context context, String key) {
        String fileName = "arasaac_api.properties";

        return AppProperties.getProperty(context, key, fileName);
    }

    private static String getProperty(Context context, String key, String fileName) {

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            Properties p = new Properties();
            p.load(inputStream);
            inputStream.close();
            return p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
