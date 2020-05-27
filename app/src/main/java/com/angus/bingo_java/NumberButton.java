package com.angus.bingo_java;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton {
    int number;
    Boolean picked;
    int position;

    public int getNumber() {
        return number;
    }

    public Boolean getPicked() {
        return picked;
    }

    public void setPicked(Boolean picked) {
        this.picked = picked;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public NumberButton(Context context) {
        super(context);
    }

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
