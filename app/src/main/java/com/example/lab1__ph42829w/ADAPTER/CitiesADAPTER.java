package com.example.lab1__ph42829w.ADAPTER;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1__ph42829w.MODEL.Cities;
import com.example.lab1__ph42829w.R;


import java.util.ArrayList;

public class CitiesADAPTER extends RecyclerView.Adapter<CitiesADAPTER.viewholder> {
    private final Context context;
    private final ArrayList<Cities> list;

    public CitiesADAPTER(Context context, ArrayList<Cities> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.items_cities, null);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Cities cities = list.get(position);
        holder.txtCountry_IT.setText("Country: " + cities.getCountry());
        holder.txtName_IT.setText("Name: " + cities.getName());
        holder.txtPopulation_IT.setText("Country: " + cities.getPopulation());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openDialogUpdate(cities);
                return false;
            }
        });
    }

    public void openDialogUpdate(Cities cities) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cities, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        EditText edtCountry_DL = view.findViewById(R.id.edtCountry_DL);
        EditText edtName_DL = view.findViewById(R.id.edtName_DL);
        EditText edtPopulation_DL = view.findViewById(R.id.edtPopulation_DL);
        EditText edtRegions_DL = view.findViewById(R.id.edtRegions_DL);
        EditText edtState_DL = view.findViewById(R.id.edtState_DL);
        RadioButton rdoCapitalTrue_DL = view.findViewById(R.id.rdoCapitalTrue_DL);
        RadioButton rdoCapitalFalse_DL = view.findViewById(R.id.rdoCapitalFalse_DL);
        Button btnUpdate_DL = view.findViewById(R.id.btnUpdate_DL);
        Button btnCancel_DL = view.findViewById(R.id.btnCancel_DL);

        edtCountry_DL.setText(cities.getCountry());
        edtName_DL.setText(cities.getName());
        edtPopulation_DL.setText(cities.getPopulation() + "");

        // Convert danh sách regions thành một chuỗi
        StringBuilder regionsStringBuilder = new StringBuilder();
        for (String region : cities.getRegions()) {
            regionsStringBuilder.append(region).append(", "); // Thêm các phần tử vào chuỗi, cách nhau bằng dấu phẩy
        }
        if (regionsStringBuilder.length() > 0) {// Xóa dấu phẩy cuối cùng nếu có
            regionsStringBuilder.deleteCharAt(regionsStringBuilder.length() - 2);
        }
        edtRegions_DL.setText(regionsStringBuilder.toString());// Gán chuỗi đã tạo vào TextView

        edtState_DL.setText(cities.getState());

        if (cities.isCapital()) {
            rdoCapitalTrue_DL.setChecked(true);
            rdoCapitalFalse_DL.setChecked(false);
        } else {
            rdoCapitalFalse_DL.setChecked(true);
            rdoCapitalTrue_DL.setChecked(false);
        }

        btnUpdate_DL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCancel_DL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView txtCountry_IT, txtName_IT, txtPopulation_IT;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            txtCountry_IT = itemView.findViewById(R.id.txtCountry_IT);
            txtName_IT = itemView.findViewById(R.id.txtName_IT);
            txtPopulation_IT = itemView.findViewById(R.id.txtPopulation_IT);
        }
    }
}
