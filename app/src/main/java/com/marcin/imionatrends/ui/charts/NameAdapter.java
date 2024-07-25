package com.marcin.imionatrends.ui.charts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcin.imionatrends.R;

import java.util.ArrayList;
import java.util.List;


public class NameAdapter extends RecyclerView.Adapter<NameAdapter.ViewHolder> {

    private List<String> nameList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private String selectedName;

    public interface OnItemClickListener {
        void onItemClick(String name);
    }

    public NameAdapter(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = nameList.get(position);
        holder.nameTextView.setText(name);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(name));
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void setData(List<String> names) {
        this.nameList.clear();
        if (names != null) {
            this.nameList.addAll(names);
        }
        notifyDataSetChanged();
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.first_name);
        }
    }
}
