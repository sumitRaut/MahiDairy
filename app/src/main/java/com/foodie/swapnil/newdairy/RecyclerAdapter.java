package com.foodie.swapnil.newdairy;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sumit on 6/19/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    ArrayList<DairyModel> dairyModelArrayList;

    public RecyclerAdapter(ArrayList<DairyModel> dairyModelArrayList) {
        this.dairyModelArrayList = dairyModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DairyModel dairyModel = dairyModelArrayList.get(position);

        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
        if (position % 2 == 0) {
            //holder.itemView.setBackgroundResource(R.drawable.row_background);

        }
        holder.tvId.setText(dairyModel.getId());
        holder.tvName.setText("cow_" + dairyModel.getId());
        holder.tvMorn.setText(dairyModel.getMilk_Morn());
        holder.tvEven.setText(dairyModel.getMilk_Even());

    }

    @Override
    public int getItemCount() {
        return dairyModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId;
        private TextView tvName;
        private TextView tvMorn;
        private TextView tvEven;

        public ViewHolder(View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvMorn = itemView.findViewById(R.id.tvMorn);
            tvEven = itemView.findViewById(R.id.tvEven);
        }
    }
}
