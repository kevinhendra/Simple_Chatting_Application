package com.uksw.khw.whatsapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;


    private FirebaseAuth myAuth;
    private DatabaseReference RootKev;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();

        RootKev = FirebaseDatabase.getInstance().getReference();


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatsApp");

        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = myAuth.getCurrentUser();

        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else
            {
            updateUserStatus("online");

            VerifyUserExistance();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        FirebaseUser currentUser = myAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    private void VerifyUserExistance() {
        String currentUserId = myAuth.getCurrentUser().getUid();
        //buat save informasi user ke firebase
        RootKev.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT);
                } else {
                    sendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }
    //untuk mengurus bagian optional menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout)
        {
            updateUserStatus("offline");

            myAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.create_group)
        {
            RequestNewGroup();
        }
        if (item.getItemId() == R.id.main_setting) {
            sendUserToSettingActivity();
        }
        if (item.getItemId() == R.id.main_find_friends) {

            sendUserToFindFriendsActivity();

        }

        return true;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name : ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Tugas Rancang");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();

            }
        });

        builder.show();

    }

    private void CreateNewGroup(final String groupName)
    {
        RootKev.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + "group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, loginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void sendUserToSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingIntent);
    }


    private void sendUserToFindFriendsActivity() {
        Intent FindFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(FindFriendsIntent);
    }



    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time", saveCurrentTime);
        onlineState.put("date", saveCurrentDate);
        onlineState.put("state", state);

        currentUserID = myAuth.getCurrentUser().getUid();

        RootKev.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineState);
    }

}

