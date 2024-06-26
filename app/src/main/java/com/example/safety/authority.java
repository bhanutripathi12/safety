package com.example.safety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.safety.databinding.ActivityAuthorityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class authority extends AppCompatActivity {

    private ActivityAuthorityBinding binding;
    private FirebaseAuth auth;

    String auth_name, auth_id, auth_email, auth_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();

        binding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth_name = binding.AuthName.getText().toString();
                auth_email = binding.EmployeeEmail.getText().toString();
                auth_password = binding.Password.getText().toString();
                auth_id = binding.EmployeeId.getText().toString();

                if (!auth_name.isEmpty() && !auth_email.isEmpty() && !auth_password.isEmpty() && !auth_id.isEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(auth_email).matches() && auth_password.length() >= 8) {
                        createUser();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(auth_email).matches()) {
                        binding.EmployeeEmail.setError("Enter a valid Email");
                    } else {
                        binding.Password.setError("Password should be at least 8 characters");
                    }
                } else {
                    if (auth_name.isEmpty())
                        binding.AuthName.setError("Empty fields are not allowed");
                    if (auth_email.isEmpty())
                        binding.EmployeeEmail.setError("Empty fields are not allowed");
                    if (auth_password.isEmpty())
                        binding.Password.setError("Empty fields are not allowed");
                    if (auth_id.isEmpty())
                        binding.EmployeeId.setError("Empty fields are not allowed");
                }
            }
        });

        binding.NewAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(authority.this, AuthLogIn.class));
            }
        });
    }

    private void createUser() {
        auth.createUserWithEmailAndPassword(auth_email, auth_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(authority.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    storeData(uid);

                    Intent it = new Intent(authority.this, AuthNav.class);
                    startActivity(it);
                } else {
                    Toast.makeText(authority.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeData(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference().child("Authority").child(uid);

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("UserName", auth_name);
        userMap.put("Email", auth_email);
        userMap.put("Password", auth_password);
        userMap.put("Profile", "");
        userMap.put("Gender", "");
        userMap.put("UID", uid);

        userRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.AuthName.setText("");
                    binding.Password.setText("");
                    binding.EmployeeEmail.setText("");
                    binding.EmployeeId.setText("");
                } else {
                    Log.d("Exception", "StoreData: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }
}
