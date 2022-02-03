package com.example.holdsafetyadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.auth.User;

public class UserAdapter extends FirestoreRecyclerAdapter<Users, UserAdapter.UserHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<Users> options) {
        super(options);
    }

    //    String[] id, name;
//    Context context;

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull Users model) {
        holder.txtUserId.setText(model.getID());
        holder.txtUsername.setText(model.getFirstName());
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.verification_row, parent
        , false);
        return new UserHolder(v);
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView txtUserId;
        TextView txtUsername;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            txtUserId = itemView.findViewById(R.id.txtUserID);
            txtUsername = itemView.findViewById(R.id.txtUserName);
        }
    }
//    public ValidationAdapter(Context ct, String[] userID, String[] userFullName){
//        context = ct;
//        id = userID;
//        name = userFullName;
//    }
//
//    @NonNull
//    @Override
//    public ValidationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater validationInflater = LayoutInflater.from(context);
//        View view = validationInflater.inflate(R.layout.validation_row, parent, false);
//        return new ValidationHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ValidationAdapter.ValidationHolder holder, int position) {
//        holder.textViewID.setText("User ID: " + id[position]);
//        holder.textViewName.setText("Name: " + name[position]);
//
//        holder.validationLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, RegistrationDetailsActivity.class);
//                intent.putExtra("userID", id[holder.getAbsoluteAdapterPosition()]);
//                context.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return id.length;
//    }
//
//    public static class ValidationHolder extends RecyclerView.ViewHolder{
//        TextView textViewID, textViewName;
//        ConstraintLayout validationLayout;
//
//        public ValidationHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewID = itemView.findViewById(R.id.txtUserID);
//            textViewName = itemView.findViewById(R.id.txtUserName);
//            validationLayout = itemView.findViewById(R.id.layoutValidation);
//        }
//    }

}
