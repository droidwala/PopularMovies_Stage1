package com.example.punit.popularmovies.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomNormalTextView extends TextView {
    public CustomNormalTextView(Context context) {
        super(context);
    }

    public CustomNormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNormalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(FontCache.getFont(getContext(),"fonts/ml.otf"));
    }
}
