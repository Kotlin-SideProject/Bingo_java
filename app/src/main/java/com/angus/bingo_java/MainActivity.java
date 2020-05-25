package com.angus.bingo_java;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;
    private TextView nickText;
    private ImageView avatar;
    private Group groupAvatars;
    int avatarIds [] = {R.drawable.avatar_0, R.drawable.avatar_1, R.drawable.avatar_2,
            R.drawable.avatar_3, R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6};
    private Member member;
    public FirebaseRecyclerAdapter<GameRoom, RoomHolder> adapter;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
        // FirebaseAdapter 需要設定開始傾聽
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        findViews();
    }

    private void findViews() {
        nickText = findViewById(R.id.nickname);
        avatar = findViewById(R.id.avatar);
        nickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNickNameDialog((String) nickText.getText());
            }
        });
        groupAvatars = findViewById(R.id.group_avatars);
        groupAvatars.setVisibility(View.GONE);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupAvatars.setVisibility(
                        groupAvatars.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        findViewById(R.id.avatar_0).setOnClickListener(this);
        findViewById(R.id.avatar_1).setOnClickListener(this);
        findViewById(R.id.avatar_2).setOnClickListener(this);
        findViewById(R.id.avatar_3).setOnClickListener(this);
        findViewById(R.id.avatar_4).setOnClickListener(this);
        findViewById(R.id.avatar_5).setOnClickListener(this);
        findViewById(R.id.avatar_6).setOnClickListener(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText roomEdit = new EditText(MainActivity.this);
                roomEdit.setText("Wellcome");
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("GameRoom ")
                                .setMessage("Please enter your Room Title")
                                .setView(roomEdit)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                            String roomTitle =  roomEdit.getText().toString();
                                            GameRoom room = new GameRoom(roomTitle, member);
                                            FirebaseDatabase.getInstance().getReference("rooms")
                                                    .push()
                                                    .setValue(room, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError databaseError,
                                                                               @NonNull DatabaseReference databaseReference) {
                                                            if(databaseError == null){
                                                                String roomId = databaseReference.getKey();
                                                                Intent bingoIntent = new Intent(MainActivity.this,
                                                                        BingoActivity.class);
                                                                bingoIntent.putExtra("ROOM_ID", roomId);
                                                                bingoIntent.putExtra("IS_CREATOR", true);
                                                                startActivity(bingoIntent);
                                                            }
                                                        }
                                                    });
                                    }
                                })
                                .setNegativeButton("cancel", null)
                                .show();
            }
        });
        //RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FirebaseRecyclerOptions 需要 Query
        Query query = FirebaseDatabase.getInstance().getReference("rooms").limitToLast(30);

        // FirebaseRecyclerAdapter 需要 FirebaseRecyclerOptions
        FirebaseRecyclerOptions<GameRoom> options = new FirebaseRecyclerOptions.Builder<GameRoom>()
                .setQuery(query, GameRoom.class)
                .build();
        Log.d(TAG, "options: " +options);
        adapter = new FirebaseRecyclerAdapter<GameRoom, RoomHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RoomHolder holder, int position, @NonNull GameRoom gameRoom) {
                Log.d(TAG, "onBindViewHolder: ");
                holder.image.setImageResource(avatarIds[gameRoom.init.avatarId]);
                holder.text.setText(gameRoom.getTitle());
            }

            @NonNull
            @Override
            public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                // 當空的時候，產生一個 view 給他
                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.room_row, parent, false);
                return new RoomHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }
    // 若使用 FirebaseUI Database，只需要設計 ViewHolder
    public class RoomHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.room_image);
            text = itemView.findViewById(R.id.room_text);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_menu_signout:
                AuthUI.getInstance().signOut(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        Log.d(TAG, "onAuthStateChanged: + QQ");
         FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false)
                            .build()
                    , RC_SIGN_IN);
        } else {
            Log.d(TAG, "onAuthStateChanged:" +
                    auth.getCurrentUser().getEmail() + "/" +
                    auth.getCurrentUser().getUid());
            final String displayName = user.getDisplayName();
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("displayName")
                    .setValue(displayName);

            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("uid")
                    .setValue(user.getUid());
            //傾聽整筆會員紀錄
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            member = dataSnapshot.getValue(Member.class);
                            if (member != null){
                                if (member.nickName != null){
                                    nickText.setText(member.nickName);
                                }else{
                                    showNickNameDialog(displayName);
                                }
                                Log.d(TAG, "onDataChange: " + member.nickName);
                                Log.d(TAG, "onDataChange: " + member.avatarId);
                                avatar.setImageResource(avatarIds[member.avatarId]);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

           /* FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("nickName")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                String nickname = (String) dataSnapshot.getValue();
                            }else{
                                showNickNameDialog(displayName);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/
        }
    }

    private void showNickNameDialog(String displayName) {
        final EditText nickEdit = new EditText(this);
        nickEdit.setText(displayName);
        new AlertDialog.Builder(this)
                .setTitle("Your Nickname")
                .setMessage("Please enter your nickname")
                .setView(nickEdit)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = String.valueOf(nickEdit.getText());
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(auth.getUid())
                                .child("nickName")
                                .setValue(nickname);
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        int selected = 0;
        if(v instanceof ImageView){
            switch (v.getId()){
                case R.id.avatar_0:
                    selected = 0;
                    break;
                case R.id.avatar_1:
                    selected = 1;
                    break;
                case R.id.avatar_2:
                    selected = 2;
                    break;
                case R.id.avatar_3:
                    selected = 3;
                    break;
                case R.id.avatar_4:
                    selected = 4;
                    break;
                case R.id.avatar_5:
                    selected = 5;
                    break;
                case R.id.avatar_6:
                    selected = 6;
                    break;
            }

            groupAvatars.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference("users")
                    .child(auth.getUid())
                    .child("avatarId")
                    .setValue(selected);
        }
    }
}
