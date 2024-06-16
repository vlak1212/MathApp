package com.example.ocr2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    List<HistoryItem> historyList = new ArrayList<>();
    private delListener delListener;

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detailed_history, parent, false);
        return new HistoryViewHolder(itemView);
    }

    public interface delListener {
        void onDeleteClick(HistoryItem historyItem);
    }

    public HistoryItem getItem(int position) {
        return historyList.get(position);
    }

    public void deleteItem(int position) {
        historyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, historyList.size());
    }

    public void setDelListener(delListener listener) {
        delListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem currentItem = historyList.get(position);
        holder.textEquation.setText(currentItem.equation);
        holder.textSolution.setText(currentItem.solution);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (delListener != null) {
                    delListener.onDeleteClick(currentItem);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<HistoryItem> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView textEquation;
        private TextView textSolution;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textEquation = itemView.findViewById(R.id.text_equation);
            textSolution = itemView.findViewById(R.id.text_solution);
        }
    }
}
