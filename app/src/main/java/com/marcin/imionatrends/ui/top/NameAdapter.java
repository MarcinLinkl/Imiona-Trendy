package com.marcin.imionatrends.ui.top;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcin.imionatrends.R;
import com.marcin.imionatrends.data.FirstNameData;

import java.util.ArrayList;
import java.util.List;

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameViewHolder> {

    private List<FirstNameData> dataList = new ArrayList<>();

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        FirstNameData data = dataList.get(position);
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.nameTextView.setText(data.getName());
        holder.countTextView.setText(String.valueOf(data.getCount()));
        holder.percentageTextView.setText(String.format("%.2f%%", data.getPercentage()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<FirstNameData> data) {
        this.dataList.clear();
        if (data != null) {
            this.dataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    static class NameViewHolder extends RecyclerView.ViewHolder {

        TextView rankTextView;
        TextView nameTextView;
        TextView countTextView;
        TextView percentageTextView;

        NameViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rank_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            countTextView = itemView.findViewById(R.id.count_text_view);
            percentageTextView = itemView.findViewById(R.id.percentage_text_view);
        }
    }
}
