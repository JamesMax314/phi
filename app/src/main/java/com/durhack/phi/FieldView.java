package com.durhack.phi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FieldView extends View {
    // TODO: 14/11/20 Create display canvas

    public FieldView(Context context) {
        super(context);
    }

    public FieldView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
    }



}
