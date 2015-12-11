package com.example.punit.popularmovies.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomBoldTextView extends TextView {

    public CustomBoldTextView(Context context) {
        super(context);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(FontCache.getFont(getContext(),"fonts/mb.otf"));
    }
}
