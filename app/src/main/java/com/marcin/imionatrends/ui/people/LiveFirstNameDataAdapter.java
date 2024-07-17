package com.marcin.imionatrends.ui.people;

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


    public LiveFirstNameDataAdapter(List<LiveFirstNameData> liveFirstNameData) {
        this.liveFirstNameData = liveFirstNameData;

    }

    public void setLiveFirstNameData(List<LiveFirstNameData> liveFirstNameData) {
        this.liveFirstNameData = liveFirstNameData;
        notifyDataSetChanged();
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
        LiveFirstNameData data = liveFirstNameData.get(position);
        holder.orderNumber.setText(String.valueOf(position + 1));
        holder.firstName.setText(data.getName());
        holder.count.setText(String.valueOf(data.getCount()));
        holder.percentage.setText(String.format("%.2f%%", (data.getPercentage())));
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
