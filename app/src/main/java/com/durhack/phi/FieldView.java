package com.durhack.phi;

import android.content.Context;
import android.util.AttributeSet;

public class FieldView extends View {
    // TODO: 14/11/20 Create display canvas 

    public EditImageView(Context context) {
        super(context);
    }

    public EditImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        dpi = context.getResources().getDisplayMetrics().density;
//        detector = new ScaleGestureDetector(context, new scaleListener());
        setFocusable(true);
    }

}
