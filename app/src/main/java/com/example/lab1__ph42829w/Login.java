package com.example.lab1__ph42829w;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lab1__ph42829w.bai2.LoginPhone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputEditText txtEmail;
    TextInputEditText txtPass;

    Button btnLogin;

    TextView txtForgot;
    TextView txtSignup;

    TextView txtLoginPhone;

    // b1 : khởi tạo FirebaseAuth
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getView();

        // b2 : gán giá trị cho mAuth
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences pre = getSharedPreferences("USER", Context.MODE_PRIVATE);
        String emailUser = pre.getString("email","");

        txtEmail.setText(emailUser);

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Sigup.class));
            }
        });

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                if (email.isEmpty()){
                    Toast.makeText(Login.this, "Vui lòng nhập email để reset password", Toast.LENGTH_SHORT).show();
                }else {
                    sendCode(email);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
                }else {
                    // b4 : sử dụng hàm
                    checkLogin(email,password);
                }
            }
        });

        txtLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, LoginPhone.class));
            }
        });
    }

    private void getView(){
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgot = findViewById(R.id.txtForgot);
        txtSignup = findViewById(R.id.txtSignup);
        txtLoginPhone = findViewById(R.id.txtLoginPhone);
    }


    // b3 : viết hàm Login
    private void checkLogin(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // lấy thông tin tài khoản mới vừa đăng nhập
                    FirebaseUser user = mAuth.getCurrentUser();

                    String emailUser = user.getEmail(); // lấy email vừa đăng ký

                    // lưu vào sharedpreferences
                    SharedPreferences preferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email",emailUser);
                    editor.apply();

                    Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, Home.class));
                }else {
                    Log.e("Lỗi","LoginWithEmail : Failure", task.getException());
                    Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendCode(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login.this, "Vui lòng kiểm tra hộp thư để reset pass", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Login.this, "Lỗi gửi mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}