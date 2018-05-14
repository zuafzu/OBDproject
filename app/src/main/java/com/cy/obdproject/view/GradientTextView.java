package com.cy.obdproject.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cy.obdproject.R;

/**
 * Created by caoyingfu on 2017/7/11.
 */

@SuppressLint("AppCompatCustomView")
public class GradientTextView extends TextView {

    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context,
                            AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context,
                            AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed,
                            int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(
                    0, 0, 0, getHeight(),
                    ContextCompat.getColor(getContext(), R.color.colorPrimary1),
                    ContextCompat.getColor(getContext(), R.color.colorPrimary2),
                    Shader.TileMode.CLAMP));
        }
    }
}