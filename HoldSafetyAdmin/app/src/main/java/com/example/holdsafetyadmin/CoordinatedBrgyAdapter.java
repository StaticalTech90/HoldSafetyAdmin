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

public class CoordinatedBrgyAdapter extends RecyclerView.Adapter<CoordinatedBrgyAdapter.CoordinatedBrgyHolder> {
    String[] name, city, mobileNum;
    Context context;

    public CoordinatedBrgyAdapter(Context ct, String brgyName[], String brgyCity[], String brgyMobileNum[]){
        context = ct;
        name = brgyName;
        city = brgyCity;
        mobileNum = brgyMobileNum;
    }

    @NonNull
    @Override
    public CoordinatedBrgyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.coordinated_brgy_row, parent, false);
        return new CoordinatedBrgyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoordinatedBrgyAdapter.CoordinatedBrgyHolder holder, int position) {
        holder.textViewName.setText(name[position]);
        holder.textViewCity.setText(city[position]);
        holder.textViewMobileNum.setText(mobileNum[position]);

        holder.brgyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CoordinatedBrgyDetailsActivity.class);
                intent.putExtra("brgyName", name[position]);
                intent.putExtra("brgyCity", city[position]);
                intent.putExtra("brgyMobileNum", mobileNum[position]);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public class CoordinatedBrgyHolder extends RecyclerView.ViewHolder{
        TextView textViewName, textViewCity, textViewMobileNum;
        ConstraintLayout brgyLayout;

        public CoordinatedBrgyHolder(@NonNull  View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.txtBrgyName);
            textViewCity = itemView.findViewById(R.id.txtBrgyCity);
            textViewMobileNum = itemView.findViewById(R.id.txtBrgyMobileNum);
            brgyLayout = itemView.findViewById(R.id.layoutCoordinatedBrgys);
        }
    }

}
