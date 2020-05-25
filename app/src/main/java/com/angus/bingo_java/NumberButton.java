package com.angus.bingo_java;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class NumberButton extends androidx.appcompat.widget.AppCompatButton {
    int number;
    Boolean enabled;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;
    public NumberButton(Context context) {
        super(context);
    }

    public NumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
