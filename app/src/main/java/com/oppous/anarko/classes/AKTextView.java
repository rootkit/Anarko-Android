package com.oppous.anarko.classes;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oppous.anarko.R;

import java.util.HashMap;
import java.util.Map;

public class AKTextView extends TextView{

    private static Map<String, Typeface> mTypefaces;

    public AKTextView(final Context context) {
        this(context, null);
    }

    public AKTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AKTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AKTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.AKTextView_font_name);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    mTypefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }
    }
}
