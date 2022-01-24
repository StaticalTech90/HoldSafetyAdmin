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

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsHolder> {

    String[] id, username, location;
    Context context;

    public ReportsAdapter(Context ct, String[] reportID, String[] reportUsername, String[] reportLocation){
        context = ct;
        id = reportID;
        username = reportUsername;
        location = reportLocation;
    }

    @NonNull
    @Override
    public ReportsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater reportInflater = LayoutInflater.from(context);
        View view = reportInflater.inflate(R.layout.reports_row, parent, false);
        return new ReportsAdapter.ReportsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsHolder holder, int position) {
        holder.textViewID.setText(id[position]);
        holder.textViewUsername.setText(username[position]);
        holder.textViewLocation.setText(location[position]);

        holder.reportsLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReportDetailsActivity.class);
                intent.putExtra("reportID", id[position]);
                intent.putExtra("reportUsername", username[position]);
                intent.putExtra("reportLocation", location[position]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.length;
    }

    public class ReportsHolder extends RecyclerView.ViewHolder {
        TextView textViewID, textViewUsername, textViewLocation;
        ConstraintLayout reportsLayout;

        public ReportsHolder(@NonNull View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.txtReportID);
            textViewUsername = itemView.findViewById(R.id.txtReportUsername);
            textViewLocation = itemView.findViewById(R.id.txtReportLocation);

            reportsLayout = itemView.findViewById(R.id.layoutReports);
        }
    }
}
