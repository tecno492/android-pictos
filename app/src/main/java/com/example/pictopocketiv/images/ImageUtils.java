package com.example.pictopocketiv.images;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ImageUtils {

    public static Bitmap downloadImageSync(String imgUrl ) throws IOException {

        Bitmap bmp = Picasso.get().load(imgUrl).get();

        return bmp;

    }
}
