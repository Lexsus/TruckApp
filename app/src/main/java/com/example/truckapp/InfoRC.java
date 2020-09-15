package com.example.truckapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoRC {
    private ImageView imageViewRed;
    private ImageView imageViewGreen;
    private TextView textViewVoltA;

    public InfoRC(ImageView imageViewRed, ImageView imageViewGreen, TextView textViewVoltA) {
        this.imageViewRed = imageViewRed;
        this.imageViewGreen = imageViewGreen;
        this.textViewVoltA = textViewVoltA;
    }

    public void update(int busy,String voltA)
    {
        textViewVoltA.setText(""+voltA+" В");
        if (busy>0)
        {
            imageViewRed.setVisibility(View.VISIBLE);
            imageViewGreen.setVisibility(View.INVISIBLE);
        }
        else {
            imageViewRed.setVisibility(View.INVISIBLE);
            imageViewGreen.setVisibility(View.VISIBLE);
        }
    }
    private TextView Name;

}
