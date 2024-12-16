package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.cse470_team8.R;

public class SuggestWordAdapter extends RecyclerView.Adapter<SuggestWordAdapter.SuggestWordViewHolder> {

    private List<String> suggestWords;
    private Context context;
    private OnWordClickListener onWordClickListener;

    public interface OnWordClickListener {
        void onWordClick(String word);
    }

    public SuggestWordAdapter(Context context, List<String> suggestWords, OnWordClickListener onWordClickListener) {
        this.context = context;
        this.suggestWords = suggestWords;
        this.onWordClickListener = onWordClickListener;
    }

    @NonNull
    @Override
    public SuggestWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggest_word, parent, false);
        return new SuggestWordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestWordViewHolder holder, int position) {
        String word = suggestWords.get(position);
        holder.wordTextView.setText(word);

        holder.itemView.setOnClickListener(v -> {
            if (onWordClickListener != null) {
                onWordClickListener.onWordClick(word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestWords.size();
    }

    public static class SuggestWordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;

        public SuggestWordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_suggest_word);
        }
    }
}
