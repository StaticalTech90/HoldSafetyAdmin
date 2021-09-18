package com.example.holdsafetyadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ValidationListActivity extends AppCompatActivity {
//    RecyclerView validRv;
//    String[] userID, userFullName;
    private FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
    private CollectionReference nbRef = fsdb.collection("users");

    private UserAdapter uAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_list);

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = nbRef.orderBy("FirstName", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class).build();

        uAdapter = new UserAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerviewValidationList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(uAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        uAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uAdapter.stopListening();
    }
}