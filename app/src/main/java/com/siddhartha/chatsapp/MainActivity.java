package com.siddhartha.chatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.siddhartha.chatsapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
FirebaseDatabase database;
ArrayList<User> users;
UsersAdapter usersAdapter;
FirebaseAuth auth;
//TopStatusAdapter statusAdapter;
ArrayList<UserStatus> userStatuses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(this,users);
     //   statusAdapter = new TopStatusAdapter(this,userStatuses);

LinearLayoutManager layoutManager = new LinearLayoutManager(this);
layoutManager.setOrientation(RecyclerView.HORIZONTAL);
//binding.statusList.setLayoutManager(layoutManager);
      //  binding.statusList.setAdapter(statusAdapter);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleView.setAdapter(usersAdapter);

        binding.recycleView.showShimmerAdapter();

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Logging Out..");

        binding.metting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,veidoCall.class));
            }
        });




     database.getReference().child("users").addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             users.clear();
             for(DataSnapshot snapshot1:snapshot.getChildren())
             {
                 User user = snapshot1.getValue(User.class);

                 if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                     users.add(user);
                 }
             }
             binding.recycleView.hideShimmerAdapter();
             usersAdapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.search:
                Toast.makeText(this,"Search clicked",Toast.LENGTH_SHORT).show();

                break;
            case R.id.settings:
                Toast.makeText(this,"Settings clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.logOut:

                Toast.makeText(this,"Logging Out...",Toast.LENGTH_SHORT).show();
                auth.signOut();

                Intent intent = new Intent(new Intent(MainActivity.this,PhoneNumberActivity.class));
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}