package com.example.skywish.imtest001.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;


public class SlidableButton extends Button{
    private boolean isDragging = false;
    private boolean isAnimating = false;

    public SlidableButton(Context context) {
        super(context);
    }

    public SlidableButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            isDragging = true;
        }
        return false;
    }

    public void setAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }
    public boolean isDragging() {
        return isDragging;
    }
    public void setX(int x){
        if(isDragging && !isAnimating){
            RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams)getLayoutParams();
            lp.leftMargin = x;
            setLayoutParams(lp);
        }
    }

}
