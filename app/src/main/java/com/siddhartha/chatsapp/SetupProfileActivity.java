package com.siddhartha.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.siddhartha.chatsapp.databinding.ActivitySetupProfileBinding;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
Uri selectedImage;
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loadng...");
        progressDialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();


        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,45);
            }
        });

binding.continueBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String name = binding.nameBoc.getText().toString();
        if(name.isEmpty())
        {
            binding.nameBoc.setError("Plaease type a name");
            return;
        }
progressDialog.show();


        if(selectedImage != null)
        {
            final StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                String uid = auth.getUid();
                                String phone = auth.getCurrentUser().getPhoneNumber();
                                User user = new User(uid,name,phone,imageUri);
                                database.getReference()
                                        .child("users")
                                        .child(uid)
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(SetupProfileActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        });
                    }
                }
            });
        }else
        {
            String uid = auth.getUid();
            String phone = auth.getCurrentUser().getPhoneNumber();
            User user = new User(uid,name,phone,"No Image");
            database.getReference()
                    .child("users")
                    .child(uid)
                    .setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(SetupProfileActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }



    }
});



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null)
        {
            if(data.getData() != null)
            {
                binding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();

            }
        }
    }
}