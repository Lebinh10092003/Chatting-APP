package vn.edu.tlu.cse470_team8.service;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EmojiService {
    private Map<String, Map<String, Double>> qTable; // Q-table
    private FirebaseFirestore db;

    public EmojiService() {
        this.qTable = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
    }

    // Lấy gợi ý emoji cho từ khóa
    public String getEmojiSuggestion(String keyword) {
        if (!qTable.containsKey(keyword)) {
            return randomEmoji();
        }

        // Chọn emoji có điểm Q cao nhất
        return qTable.get(keyword).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(randomEmoji());
    }

    // Cập nhật Q-table
    public void updateQValue(String keyword, String emoji, double reward) {
        qTable.putIfAbsent(keyword, new HashMap<>());
        Map<String, Double> actions = qTable.get(keyword);
        double currentQ = actions.getOrDefault(emoji, 0.0);
        double newQ = currentQ + 0.1 * (reward - currentQ); // alpha = 0.1
        actions.put(emoji, newQ);
    }

    // Lưu Q-table vào Firestore
    public void saveQTable() {
        db.collection("q_table_emoji").document("emoji_suggestions")
                .set(qTable)
                .addOnSuccessListener(aVoid -> System.out.println("Q-table saved successfully!"))
                .addOnFailureListener(e -> System.err.println("Error saving Q-table: " + e.getMessage()));
    }

    // Tải Q-table từ Firestore
    public void loadQTable(OnQTableLoadListener listener) {
        db.collection("q_table_emoji").document("emoji_suggestions")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        Map<String, Map<String, Double>> loadedTable = new HashMap<>();
                        for (String state : data.keySet()) {
                            Map<String, Double> actions = (Map<String, Double>) data.get(state);
                            loadedTable.put(state, actions);
                        }
                        this.qTable = loadedTable;
                        listener.onSuccess();
                    } else {
                        listener.onFailure("No Q-table found!");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Listener để xử lý callback
    public interface OnQTableLoadListener {
        void onSuccess();
        void onFailure(String error);
    }

    // Trả về emoji ngẫu nhiên
    private String randomEmoji() {
        String[] emojis = {"😊", "😂", "😢", "😡", "❤️", "👍"};
        return emojis[(int) (Math.random() * emojis.length)];
    }
}
