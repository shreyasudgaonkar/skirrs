package com.skirrs.widget;

import com.skirrs.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SkirrsTypeFacedTextView extends TextView {

	/*
	 * Default font
	 * This can be overridden by using skirrs:typeface="<fontname.ttf>" in the 
	 * layout xml
	 */
	private String mFontName = "Roboto-Regular.ttf";
	
	public SkirrsTypeFacedTextView( Context context,
								 	AttributeSet attrs ) {
        super( context, attrs );

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if ( isInEditMode() ) {
            return;
        }

        TypedArray styledAttrs = 
        		context.obtainStyledAttributes( attrs,
        										R.styleable.SkirrsTypeFacedTextView );
        
        String fontName =
        		styledAttrs.getString( R.styleable.SkirrsTypeFacedTextView_typeface );
        
        styledAttrs.recycle();

        if ( fontName == null ) {
        	
        	fontName = mFontName;
        	
        }
        	
        Typeface typeface =
        		Typeface.createFromAsset( context.getAssets(), fontName );
        
        setTypeface( typeface );
        
    }
	
}
