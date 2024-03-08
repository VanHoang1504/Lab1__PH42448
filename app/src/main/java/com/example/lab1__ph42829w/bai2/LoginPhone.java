package com.example.lab1__ph42829w.bai2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.lab1__ph42829w.Home;
import com.example.lab1__ph42829w.MainActivity;
import com.example.lab1__ph42829w.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginPhone extends AppCompatActivity {

    TextInputEditText txtPhone;
    TextInputEditText txtOTP;

    Button btnLogin;
    Button btnGetOTP;


    // b1 : khởi tạo FirebaseAuth, PhoneAuthProvider, mVerificationId
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        getView();

        // b2 : gán giá trị FirebaseAuth
        mAuth =FirebaseAuth.getInstance();

        // b4 : viết hàm callback
        // hàm này sẽ tự động gọi khi ta gửi yêu cầu OTP
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // khi số điện thoại đăng nhập của bạn nằm trong máy bạn sẽ thực hiện getOTP
                // Chúng ta có thể lấy mã OTP và setText mã OTP lên mà không cần phải nhập
                txtOTP.setText(phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            // khi fail chạy vào hàm này
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                // Hàm này sẽ được chạy khi otp gửi thành công, ta sẽ lấy verificationId
                mVerificationId = s;
            }
        };


        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = txtPhone.getText().toString().trim();
                if (phoneNumber.isEmpty()){
                    Toast.makeText(LoginPhone.this, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginPhone.this, "Kiểm tra hộp thư để lấy mã otp", Toast.LENGTH_SHORT).show();
                    // b6 : gọi hàm lấy mã otp
                    getOTP(phoneNumber);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = txtOTP.getText().toString().trim();
                if (otp.isEmpty()){
                    Toast.makeText(LoginPhone.this, "Vui lòng điền mã otp", Toast.LENGTH_SHORT).show();
                }else {
                    // b8 : gọi hàm check mã otp để login
                    verifyOTP(otp);
                }

            }
        });



    }

    private void getView(){
        txtPhone = findViewById(R.id.txtPhone);
        txtOTP = findViewById(R.id.txtOTP);
        btnLogin = findViewById(R.id.btnLogin);
        btnGetOTP = findViewById(R.id.btnGetOTP);
    }


    // b3 : viết hàm lấy mã otp
    private void getOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+84"+phoneNumber) //phone number verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // (optional) Activity for callback binding
                        //If no activity is passed, reCAPTCHA vertification can not be used.
                        .setCallbacks(mCallback) // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // b7 : viết hàm xác thực mã otp
    private void verifyOTP(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // đăng nhập thành công , update UI with the signed in user's information
                            Toast.makeText(LoginPhone.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(LoginPhone.this, Home.class));
                            // Update UI
                        }else {
                            Log.w("Lỗi","Đăng nhập với số điện thoại thất bại", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                // the verification code entered was invalid
                                Toast.makeText(LoginPhone.this, " Mã otp không hợp lệ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }
}