package com.marcin.imionatrends.ui.people;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marcin.imionatrends.R;
import com.marcin.imionatrends.data.LiveFirstNameData;

import java.util.List;

public class LiveFirstNameDataAdapter extends RecyclerView.Adapter<LiveFirstNameDataAdapter.ViewHolder> {
    private List<LiveFirstNameData> liveFirstNameData;
    private List<LiveFirstNameData> originalLiveFirstNameData;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_first_name_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiveFirstNameData data = liveFirstNameData.get(position);

        // Calculate the order number based on the original data
        int orderIndex = originalLiveFirstNameData.indexOf(data) + 1;
        holder.orderNumber.setText(String.valueOf(orderIndex));

        holder.firstName.setText(data.getName());
        holder.count.setText(String.valueOf(data.getCount()));
        holder.percentage.setText(String.format("%.2f%%", data.getPercentage()));
    }

    public LiveFirstNameDataAdapter(List<LiveFirstNameData> liveFirstNameData, List<LiveFirstNameData> originalData) {
        this.liveFirstNameData = liveFirstNameData;
        this.originalLiveFirstNameData = originalData;
    }

    // Method to update the data
    public void updateData(List<LiveFirstNameData> newData) {
        Log.d("LiveFirstNameDataAdapter", "Data updated: " + newData);
        this.liveFirstNameData = newData;
        notifyDataSetChanged();
    }

    // Method to update the original data
    public void updateOriginalData(List<LiveFirstNameData> originalData) {
        Log.d("LiveFirstNameDataAdapter", "Updating original data: " + originalData);
        this.originalLiveFirstNameData = originalData;
    }

    @Override
    public int getItemCount() {
        return liveFirstNameData.size();
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
