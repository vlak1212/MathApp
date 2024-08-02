package com.example.ocr2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocr2.HistoryItem;
import com.example.ocr2.R;
import com.zanvent.mathview.MathView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public List<HistoryItem> historyList = new ArrayList<>();
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
        holder.textProblem.setText(currentItem.equation.replaceAll("\\\\", "<br/>"));
        holder.textSolution.setText(currentItem.solution.replaceAll("\\\\", "<br/>"));

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

        private MathView textProblem;
        private MathView textSolution;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textProblem = itemView.findViewById(R.id.HProblems);
            textSolution = itemView.findViewById(R.id.HSolution);
        }
    }
}
