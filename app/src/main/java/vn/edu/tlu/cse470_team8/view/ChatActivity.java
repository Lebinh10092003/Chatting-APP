package vn.edu.tlu.cse470_team8.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.ChatAdapter;
import vn.edu.tlu.cse470_team8.controller.SuggestWordAdapter;
import vn.edu.tlu.cse470_team8.model.Message;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView, rcv_suggest;
    private ChatAdapter chatAdapter;
    private SuggestWordAdapter suggestAdapter;
    private List<Message> messages = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView txt_group_name, txt_status, txt_suggest_word;
    private ImageButton bt_back, bt_add_media;
    private Button bt_send_message;
    private EditText edt_content_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.rcv_chat_message);
        rcv_suggest = findViewById(R.id.rcv_sugget_message);
        txt_group_name = findViewById(R.id.txt_group_chat);
        txt_status = findViewById(R.id.txt_status_chat);
        bt_back = findViewById(R.id.bt_back_chat);
        bt_add_media = findViewById(R.id.bt_add_media);
        bt_send_message = findViewById(R.id.bt_send_message);
        edt_content_chat = findViewById(R.id.edt_chat_message);
        txt_suggest_word = findViewById(R.id.txt_suggest_word);

        // Lấy thông tin nhóm từ intent
        String groupId = getIntent().getStringExtra("groupId");
        String groupName = getIntent().getStringExtra("groupName");

        // Cấu hình RecyclerView cho tin nhắn
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);

        // Cấu hình RecyclerView cho từ gợi ý
        List<String> suggestWords = Arrays.asList("Hello", "Hi", "Good morning", "Thanks", "Goodbye");
        suggestAdapter = new SuggestWordAdapter(this, suggestWords, word -> {
            // Xử lý khi người dùng nhấp vào một từ gợi ý
            Toast.makeText(ChatActivity.this, "Clicked: " + word, Toast.LENGTH_SHORT).show();
        });
        rcv_suggest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_suggest.setAdapter(suggestAdapter);

        // Set tên nhóm
        txt_group_name.setText(groupName);

        // Tải tin nhắn
        loadMessages(groupId);

        // Xử lý khi người dùng nhấp vào nút gửi tin nhắn
        bt_send_message.setOnClickListener(v -> {
            String messageContent = edt_content_chat.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);
            } else {
                Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadMessages(String groupId) {
        db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        messagesRef.whereEqualTo("group_id", groupId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    messages.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Message message = document.toObject(Message.class);

                        // Kiểm tra và chuyển đổi timestamp nếu cần
                        if (document.contains("timestamp") && document.get("timestamp") != null) {
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            if (timestamp != null) {
                                message.setTimestamp(timestamp); // Đảm bảo set đúng kiểu Timestamp
                            }
                        }

                        messages.add(message);
                    }

                    // Sắp xếp tin nhắn theo timestamp
                    messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

                    // Cập nhật RecyclerView cho tin nhắn
                    chatAdapter.notifyDataSetChanged();

                    // Scroll xuống tin nhắn mới nhất
                    recyclerView.scrollToPosition(messages.size() - 1);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting messages", e);
                });
    }

    private void sendMessage(String content) {
        // Lấy userId và groupId từ SharedPreferences và Intent
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String groupId = getIntent().getStringExtra("groupId");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000, 0); // Lấy thời gian hiện tại

        // Tạo ID cho tin nhắn, có thể sử dụng UUID hoặc Firebase-generated ID
        String messageId = FirebaseFirestore.getInstance().collection("messages").document().getId();

        // Tạo đối tượng Message
        Message message = new Message();
        message.setSender_id(userId);
        message.setContent(content);
        message.setTimestamp(timestamp);
        message.setGroup_id(groupId);
        message.setIs_read(false);
        message.setMessage_id(messageId);
        message.setMessage_type("text");
        message.setStatus("sent");
        // Lưu tin nhắn vào Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .document(messageId)  // Dùng messageId để lưu tin nhắn
                .set(message)
                .addOnSuccessListener(documentReference -> {
                    // Tin nhắn đã được gửi thành công
                    edt_content_chat.setText(""); // Xóa nội dung tin nhắn trong EditText

                    // Cập nhật danh sách tin nhắn
                    messages.add(message);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }


}
