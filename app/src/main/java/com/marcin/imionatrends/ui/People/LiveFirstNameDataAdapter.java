package com.marcin.imionatrends.ui.People;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcin.imionatrends.R;
import com.marcin.imionatrends.data.LiveFirstNameData;

import java.util.ArrayList;
import java.util.List;

public class LiveFirstNameDataAdapter extends RecyclerView.Adapter<LiveFirstNameDataAdapter.ViewHolder> {
    private List<LiveFirstNameData> liveFirstNameData;
    private List<LiveFirstNameData> filteredData;
    private int totalCount;

    public LiveFirstNameDataAdapter(List<LiveFirstNameData> liveFirstNameData) {
        this.liveFirstNameData = liveFirstNameData;
        this.filteredData = new ArrayList<>(liveFirstNameData);
        this.totalCount = calculateTotalCount(liveFirstNameData);
    }

    public void setLiveFirstNameData(List<LiveFirstNameData> liveFirstNameData) {
        this.liveFirstNameData = liveFirstNameData;
        this.filteredData = new ArrayList<>(liveFirstNameData);
        this.totalCount = calculateTotalCount(liveFirstNameData);
        notifyDataSetChanged();
    }

    private int calculateTotalCount(List<LiveFirstNameData> data) {
        int count = 0;
        for (LiveFirstNameData item : data) {
            count += item.getCount();
        }
        return count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_first_name_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiveFirstNameData data = filteredData.get(position);
        holder.orderNumber.setText(String.valueOf(position + 1));
        holder.firstName.setText(data.getName());
        holder.count.setText(String.valueOf(data.getCount()));
        holder.percentage.setText(String.format("%.2f%%", (data.getCount() * 100.0 / totalCount)));
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void filter(String query, String gender) {
        filteredData.clear();
        if (query.isEmpty() && gender.equals("Wszyscy")) {
            filteredData.addAll(liveFirstNameData);
        } else {
            for (LiveFirstNameData item : liveFirstNameData) {
                boolean matchesName = item.getName().toLowerCase().contains(query.toLowerCase());
                boolean matchesGender = gender.equals("Wszyscy") ||
                        (gender.equals("Mężczyźni") && item.isMale()==1 ||
                        (gender.equals("Kobiety") && item.isMale()==0));
                if (matchesName && matchesGender) {
                    filteredData.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView firstName;
        TextView count;
        TextView percentage;

        ViewHolder(View view) {
            super(view);
            orderNumber = view.findViewById(R.id.order_number);
            firstName = view.findViewById(R.id.first_name);
            count = view.findViewById(R.id.count);
            percentage = view.findViewById(R.id.percentage);
        }
    }
}
