package com.cs180.simplenote.simplenoteapp;

import android.content.Context;
import android.graphics.Path;
import android.view.View;

public class PaintView extends View {
    public Path path = new Path();
    public PaintView(Context context) {
        super(context);
    }
}
