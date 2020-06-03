package com.angus.bingo_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoActivity extends AppCompatActivity {

    public static final int STATUS_INIT = 0;
    public static final int STATUS_CREATED = 1;
    public static final int STATUS_JOINED = 2;
    public static final int STATUS_CREATORS_TURN = 3;
    public static final int STATUS_JOINER_TURN = 4;
    public static final int STATUS_CREATOR_BINGO = 5;
    public static final int STATUS_JOINER_BINGO = 6;
    boolean  myTurn = false;
    ValueEventListener statusListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long status = (long) dataSnapshot.getValue();
                switch((int) status){
                    case STATUS_CREATED:
                        infomation.setText("等待對手加入");
                        break;
                    case STATUS_JOINED:
                        infomation.setText("對手已經加入");
                        FirebaseDatabase.getInstance().getReference("rooms")
                                .child(roomId)
                                .child("status")
                                .setValue(STATUS_CREATORS_TURN);
                        break;
                    case STATUS_CREATORS_TURN:
                        setMyTurn(isCreator);
                        break;
                }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };



    private static final String TAG = BingoActivity.class.getSimpleName();
    private TextView infomation;
    private RecyclerView recycler;
    private FirebaseRecyclerAdapter<Boolean, NumberHolder> adapter;
    private String roomId;
    private Boolean isCreator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingo);
        roomId = getIntent().getStringExtra("ROOM_ID");
        isCreator = getIntent().getBooleanExtra("IS_CREATOR", false);
        Log.d(TAG, "onCreate:" + roomId + "/" + isCreator);
        if(isCreator){
            for (int i = 0; i < 25; i++) {
                FirebaseDatabase.getInstance().getReference("rooms")
                        .child(roomId)
                        .child("numbers")
                        .child(String.valueOf(i+1))
                        .setValue(false);
            }
            //change room status
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("status")
                    .setValue(STATUS_CREATED);
        }else{
            //change room status
            FirebaseDatabase.getInstance().getReference("rooms")
                    .child(roomId)
                    .child("status")
                    .setValue(STATUS_JOINED);
        }
        // map for number to position
        final Map<Integer, Integer> numberMap =new HashMap();
        final List<NumberButton> buttons = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            NumberButton button = new NumberButton(BingoActivity.this);
            button.setNumber(i+1);
            buttons.add(button);
        }
        Collections.shuffle(buttons);
        for (int i = 0; i < 25; i++) {
            numberMap.put(buttons.get(i).getNumber(), i);
        }


        //RecyclerView
        Query query = FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("numbers")
                .orderByKey();
        FirebaseRecyclerOptions<Boolean> options = new FirebaseRecyclerOptions.Builder<Boolean>()
                .setQuery(query, Boolean.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Boolean, NumberHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NumberHolder holder, int position, @NonNull Boolean model) {
                holder.button.setText(String.valueOf(buttons.get(position).getNumber()));
//                holder.button.setEnabled(!model);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                Log.d(TAG, "onChildChanged: " + type + "/" +snapshot.getKey() + "/" + snapshot.getValue());
                if (type == ChangeEventType.CHANGED){
                    int numbert = Integer.parseInt(snapshot.getKey());
                    Boolean picked = (Boolean) snapshot.getValue();
                    int position = numberMap.get(numbert);
                    NumberHolder holder = (NumberHolder) recycler.findViewHolderForAdapterPosition(position);
                    holder.button.setEnabled(!picked);
                }
            }

            @NonNull
            @Override
            public NumberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(BingoActivity.this)
                        .inflate(R.layout.single_button, parent, false);
                return new NumberHolder(view);
            }
        };
        findViews();
    }
    class NumberHolder extends RecyclerView.ViewHolder{
        NumberButton button;
        public NumberHolder(@NonNull View itemView) {
            super(itemView);
             button = itemView.findViewById(R.id.button);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("status")
                .addValueEventListener(statusListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        FirebaseDatabase.getInstance().getReference("rooms")
                .child(roomId)
                .child("status")
                .removeEventListener(statusListener);
    }

    private void findViews() {
        infomation = findViewById(R.id.info);
        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(BingoActivity.this, 5));
        recycler.setAdapter(adapter);
    }
    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
        infomation.setText(myTurn ? "請選球" : "等待對手選號");
    }
}
