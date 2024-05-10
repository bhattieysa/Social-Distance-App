package com.devsoft.socialdistanceapp.loginsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devsoft.socialdistanceapp.R;
import com.devsoft.socialdistanceapp.helper.Progress_Validation_H;
import com.devsoft.socialdistanceapp.helper.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText edit_email,edit_password,edit_name;
    Progress_Validation_H helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        helper = new Progress_Validation_H(this);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_name.getText().toString().isEmpty()
                    || edit_email.getText().toString().isEmpty()
                    || edit_password.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }   else {
                    helper.show_Dialog();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(edit_email.getText().toString(),edit_password.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String id = FirebaseAuth.getInstance().getUid();
                            User user = new User();
                            user.setName(edit_name.getText().toString());
                            user.setEmail(edit_email.getText().toString());
                            user.setPassword(edit_password.getText().toString());
                            user.setId(id);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("user")
                                    .child(id)
                                    .setValue(user);
                            helper.show_Dialog();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.show_Dialog();
                            Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    }

            }
        });
    }
}