package com.example.lab1__ph42829w;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab1__ph42829w.ADAPTER.CitiesADAPTER;
import com.example.lab1__ph42829w.MODEL.Cities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView rcv_Cities;
    CitiesADAPTER citiesADAPTER;
    FloatingActionButton fab_addCities;
    private ArrayList<Cities> listCities = new ArrayList<Cities>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        db = FirebaseFirestore.getInstance();
        rcv_Cities = findViewById(R.id.rcv_Cities);
        fab_addCities = findViewById(R.id.fab_addCities);
        docDuLieu();

        fab_addCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCities();
            }
        });

//        ghiDuLieu();
//        LottieAnimationView animationView = findViewById(R.id.animationView);
//
//        animationView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(Home.this, Login.class);
//                startActivity(intent);
//            }
//        });
    }

    String TAG = "HomeActivity";

    private void docDuLieu() {
        CollectionReference citiesRef = db.collection("cities");
        citiesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder dataBuilder = new StringBuilder();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Lấy dữ liệu từ mỗi tài liệu Firestore
                        String name = document.getString("name");
                        String state = document.getString("state");
                        String country = document.getString("country");
                        boolean capital = document.getBoolean("capital");
                        long populationLong = document.getLong("population");
// Sử dụng giá trị population như là một số nguyên
                        int population = (int) populationLong;
                        List<String> regions = (List<String>) document.get("regions");

                        Cities cities = new Cities(name, state, country, capital, population, regions);
                        listCities.add(cities);

                        //Toast.makeText(Home.this, name+state+country+capital+population+regions, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(Home.this, listCities.size() + "", Toast.LENGTH_SHORT).show();
                        LinearLayoutManager layout = new LinearLayoutManager(Home.this);
                        rcv_Cities.setLayoutManager(layout);
                        citiesADAPTER = new CitiesADAPTER(Home.this, listCities);

                        rcv_Cities.setAdapter(citiesADAPTER);
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addCities() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cities, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        EditText edtCountry_DL = view.findViewById(R.id.edtCountry_DL);
        EditText edtName_DL = view.findViewById(R.id.edtName_DL);
        EditText edtPopulation_DL = view.findViewById(R.id.edtPopulation_DL);
        EditText edtRegions_DL = view.findViewById(R.id.edtRegions_DL);
        EditText edtState_DL = view.findViewById(R.id.edtState_DL);
        RadioButton radioButtonTrue = view.findViewById(R.id.rdoCapitalTrue_DL);
        RadioButton radioButtonFalse = view.findViewById(R.id.rdoCapitalFalse_DL);
        Button btnUpdate_DL = view.findViewById(R.id.btnUpdate_DL);
        Button btnCancel_DL = view.findViewById(R.id.btnCancel_DL);
        btnUpdate_DL.setText("Add New Cities");

        btnUpdate_DL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = edtCountry_DL.getText().toString();
                String population = edtPopulation_DL.getText().toString();
                String name = edtName_DL.getText().toString();
                String state = edtState_DL.getText().toString();
                boolean capital;
                if (radioButtonTrue.isChecked() == true) {
                    // RadioButton True đã được chọn
                    capital = true;
                } else if (radioButtonFalse.isChecked() == true) {
                    // RadioButton False đã được chọn
                    capital = false;
                } else {
                    // Không có RadioButton nào được chọn
                    Toast.makeText(Home.this, "Bạn phải chọn Capital !", Toast.LENGTH_SHORT).show();
                    return;
                }
                String regions = edtRegions_DL.getText().toString();
                String[] regionArray = regions.split(",\\s*"); // Tách chuỗi theo dấu phẩy và bỏ qua khoảng trắng sau dấu phẩy
                List<String> regionList = Arrays.asList(regionArray);
                if (country.trim().isEmpty() || population.trim().isEmpty() || name.trim().isEmpty() || regions.trim().isEmpty()) {
                    Toast.makeText(Home.this, "Không bỏ trống thông tin !", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(population) < 0) {
                    Toast.makeText(Home.this, "Population phải lớn hơn 0 !", Toast.LENGTH_SHORT).show();
                } else {
                    Cities cities1 = new Cities(name, state, country, capital, Integer.parseInt(population), regionList);

                    CollectionReference cities = db.collection("cities");
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", name);
                    data.put("state", state);
                    data.put("country", country);
                    data.put("capital", capital);
                    data.put("population", Integer.parseInt(population));
                    data.put("regions", regionList);
                    cities.document(kiTuDauName(name)).set(data);
                    Toast.makeText(Home.this, "Thêm thành công !", Toast.LENGTH_SHORT).show();
                    listCities.clear();
                    docDuLieu();
                    dialog.dismiss();
                }
            }
        });

        btnCancel_DL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private String kiTuDauName(String name) {
        // Tạo một StringBuilder để xây dựng chuỗi kết quả
        StringBuilder kyTuDauVaSauBuilder = new StringBuilder();
        // Tạo một mảng từ chuỗi đầu vào bằng cách tách theo dấu cách
        String[] words = name.split("\\s+");
        // Duyệt qua mỗi từ trong mảng
        for (String word : words) {
            // Kiểm tra xem từ có ít nhất 2 ký tự không
            if (word.length() > 1) {
                // Lấy ký tự đầu tiên của từ và chuyển thành chữ in hoa
                kyTuDauVaSauBuilder.append(Character.toUpperCase(word.charAt(0)));
            } else if (word.length() == 1) {
                // Nếu từ chỉ có một ký tự, thì chỉ lấy ký tự đầu tiên và chuyển thành chữ in hoa
                kyTuDauVaSauBuilder.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        // Trả về chuỗi kết quả
//        Toast.makeText(this, kyTuDauVaSauBuilder.toString(), Toast.LENGTH_SHORT).show();
        return kyTuDauVaSauBuilder.toString();
    }

// KHoi đầu đọc ghi DL
//    private void docDuLieu(TextView dataTextView) {
//        DocumentReference docRef=db.collection("cities").document("LA");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()){
//
//
//                        // Lấy dữ liệu từ tài liệu Firestore
//                        String name = document.getString("name");
//                        String state = document.getString("state");
//                        String country = document.getString("country");
//
//                        // Hiển thị dữ liệu trong TextView
//                        String data = "Name: " + name + "\nState: " + state + "\nCountry: " + country;
//                        dataTextView.setText(document.getData()+"");
//
//
//                        Log.d(TAG,"DocumentSnapshot data: "+document.getData());
//                    }else {
//                        Log.d(TAG,"No such document");
//                    }
//                }else {
//                    Log.d(TAG,"get failed with ",task.getException());
//                }
//            }
//        });
//    }
    private void ghiDuLieu() {
        CollectionReference cities = db.collection("cities");

        Map<String,Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        data1.put("regions", Arrays.asList("west_coast", "norcal"));
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        data2.put("regions", Arrays.asList("west_coast", "socal"));
        cities.document("LA").set(data2);
    }
}