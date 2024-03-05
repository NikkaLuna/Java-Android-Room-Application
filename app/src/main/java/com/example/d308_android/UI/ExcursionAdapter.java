package com.example.d308_android.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308_android.R;
import com.example.d308_android.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private TextView excursionNameTextView = null;


        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionNameTextView = itemView.findViewById(R.id.excursionNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Excursion current = mExcursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("excursionID", current.getExcursionID());
                    intent.putExtra("excursionName", current.getExcursionName());
                    intent.putExtra("excursionPrice", current.getPrice());
                    intent.putExtra("vacationID", current.getVacationID());
                    intent.putExtra("startDate", current.getStartDate());
                    context.startActivity(intent);

                }
            });
        }
    }
        public ExcursionAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
            return new ExcursionViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null && position < mExcursions.size()) {
            Excursion current = mExcursions.get(position);
            String name = current.getExcursionName();
            holder.excursionNameTextView.setText(name);
        } else {
            holder.excursionNameTextView.setText("No excursion name");
        }
    }

    public void setExcursions(List<Excursion> excursions){
        mExcursions=excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mExcursions!=null) return mExcursions.size();
        else return 0;
    }
}




