package com.example.holdsafetyadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class VerificationAdapter extends RecyclerView.Adapter<VerificationAdapter.ValidationHolder>{
    String id[], name[];
    Context context;

    public VerificationAdapter(Context ct, String userID[], String userFullName[]){
        context = ct;
        id = userID;
        name = userFullName;
    }

    @NonNull
    @Override
    public ValidationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater validationInflater = LayoutInflater.from(context);
        View view = validationInflater.inflate(R.layout.verification_row, parent, false);
        return new ValidationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerificationAdapter.ValidationHolder holder, int position) {
        holder.textViewID.setText("User ID: " + id[position]);
        holder.textViewName.setText("Name: " + name[position]);

        holder.validationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RegistrationDetailsActivity.class);
                intent.putExtra("userID", id[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.length;
    }

    public class ValidationHolder extends RecyclerView.ViewHolder{
        TextView textViewID, textViewName;
        ConstraintLayout validationLayout;

        public ValidationHolder(@NonNull View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.txtUserID);
            textViewName = itemView.findViewById(R.id.txtUserName);
            validationLayout = itemView.findViewById(R.id.layoutValidation);
        }
    }

}
