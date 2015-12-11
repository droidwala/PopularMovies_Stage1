package com.example.punit.popularmovies.Fonts;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontCache {
    private static HashMap<String,Typeface> fontMap = new HashMap<String,Typeface>();

    public static Typeface getFont(Context context,String fontname){
        if(fontMap.containsKey(fontname)){
            return fontMap.get(fontname);
        }
        else{
            Typeface tf = Typeface.createFromAsset(context.getAssets(),fontname);
            fontMap.put(fontname,tf);
            return tf;
        }
    }
}
