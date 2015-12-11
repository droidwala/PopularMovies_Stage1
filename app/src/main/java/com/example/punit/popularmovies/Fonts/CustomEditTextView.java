package com.example.punit.popularmovies.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class CustomEditTextView extends EditText {

    public CustomEditTextView(Context context) {
        super(context);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(FontCache.getFont(getContext(),"fonts/ml.otf"));
    }
}
