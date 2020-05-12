package com.angus.bingo_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
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
                    });
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
}
