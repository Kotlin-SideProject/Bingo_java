package com.angus.bingo_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoActivity extends AppCompatActivity {

    private static final String TAG = BingoActivity.class.getSimpleName();
    private TextView infomation;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        String roomId = getIntent().getStringExtra("ROOM_ID");
        Boolean isCreator = getIntent().getBooleanExtra("IS_CREATOR", false);
        Log.d(TAG, "onCreate:" +roomId + "/" + isCreator);
        if(isCreator){
            for (int i = 0; i < 25; i++) {
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomId)
                        .child("numbers")
                        .child(String.valueOf(i+1))
                        .setValue(false);
            }
        }
        List<NumberButton> buttons = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            NumberButton button = new NumberButton(BingoActivity.this);
            button.setNumber(i+1);
        }
        Collections.shuffle(buttons);
        findViews();
    }

    private void findViews() {
        infomation = findViewById(R.id.info);
        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(BingoActivity.this, 5));
    }
}
