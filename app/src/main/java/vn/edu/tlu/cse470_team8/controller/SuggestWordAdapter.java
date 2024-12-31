package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;

public class SuggestWordAdapter extends RecyclerView.Adapter<SuggestWordAdapter.SuggestWordViewHolder> {

    private List<String> suggestWords;
    private Context context;
    private OnWordClickListener onWordClickListener;

    // Giao diện cho sự kiện click vào từ gợi ý
    public interface OnWordClickListener {
        void onWordClick(String word);
    }

    // Constructor
    public SuggestWordAdapter(Context context, List<String> suggestWords, OnWordClickListener onWordClickListener) {
        this.context = context;
        this.suggestWords = suggestWords != null ? suggestWords : new ArrayList<>();
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

        // Gắn sự kiện click vào item
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

    // ViewHolder nội bộ
    public static class SuggestWordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;

        public SuggestWordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.txt_suggest_word);
        }
    }

    // Phương thức cập nhật danh sách từ gợi ý
    public void updateSuggestions(List<String> newSuggestions) {
        suggestWords.clear(); // Xóa danh sách cũ
        suggestWords.addAll(newSuggestions); // Thêm danh sách mới
        notifyDataSetChanged(); // Làm mới giao diện
    }

}
