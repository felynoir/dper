package com.pornattapat.dper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    EditText signUpEmail,signUpPassword,signUpRePassword,signUpName,signUpSurName;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpRePassword = findViewById(R.id.signUpRePassword);
        signUpName = findViewById(R.id.signUpName);
        signUpSurName = findViewById(R.id.signUpSurName);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {

                }
            }
        };
    }

    public void onBackToSignIn(View view) {
        finish();
    }

    public void onSignUpConfirm(View view) {
        final String email = signUpEmail.getText().toString();
        final String password = signUpPassword.getText().toString();
        final String name = signUpName.getText().toString();
        final String repassword = signUpRePassword.getText().toString();
        final String surname = signUpSurName.getText().toString();

        if(     email.isEmpty() ||
                password.isEmpty() ||
                name.isEmpty() ||
                repassword.isEmpty() ||
                surname.isEmpty() ) {
            Toast.makeText(getApplicationContext(),"กรุณากรอกข้อมูลให้ครบทุกช่อง",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6 || repassword.length() < 6) {
            Toast.makeText(getApplicationContext(),"รหัสผ่านควรมี 6 ตัว ขึ้นไป",Toast.LENGTH_SHORT).show();
            return;
        }
        if( !password.equals(repassword)) {
            Toast.makeText(getApplicationContext(), "รหัสผ่าน ไม่ตรงกันกรุณาตรวจสอบอีกครั้ง", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            final Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                            final String UID = user.getUid();
                            Map<String,Object> userMap = new HashMap<>();
                            userMap.put("email",email);
                            userMap.put("name",name);
                            userMap.put("sur_name",surname);
                            userMap.put("carrot","0");
                            userMap.put("level","1");

                            db.collection("users")
                                    .document(UID)
                                    .set(userMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),
                                                    "ข้อมูลของคุณ หรือระบบมีปัญหากรุณาติดต่อผู้พัฒนา",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "สมัครสมาชิกไม่สำเร็จมีข้อผิดพลาด",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
