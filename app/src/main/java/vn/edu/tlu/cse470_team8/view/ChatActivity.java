package vn.edu.tlu.cse470_team8.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

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
    private ImageView avatar_chat;

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
        avatar_chat = findViewById(R.id.avatar_chat);


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

        // Tải thông tin nhóm
        loadGroupInfo(groupId);
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

        // Xử lý khi người dùng nhấp vào nút quay lại
        bt_back.setOnClickListener(v -> finish());

        txt_group_name.setOnClickListener(v -> {
            //Chuyen den edit group va truyen groupId
            Intent intent = new Intent(ChatActivity.this, EditGroupActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });


    }
    // Hàm tải thông tin nhóm (tên nhóm và ảnh nhóm)
    private void loadGroupInfo(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups").document(groupId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String newGroupName = documentSnapshot.getString("group_name");
                        String newGroupPhotoUrl = documentSnapshot.getString("avatar_url");

                        // Cập nhật tên nhóm
                        if (newGroupName != null) {
                            txt_group_name.setText(newGroupName);
                        }

                        // Cập nhật ảnh nhóm nếu có URL
                        if (newGroupPhotoUrl != null && !newGroupPhotoUrl.isEmpty()) {
                            FirebaseStorage.getInstance().getReferenceFromUrl(newGroupPhotoUrl)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Glide.with(ChatActivity.this)
                                                .load(uri) // Lấy URL tải về của ảnh
                                                .into(avatar_chat); // Cập nhật ảnh nhóm vào ImageView
                                    })
                                    .addOnFailureListener(exception -> {
                                        Log.e("FirebaseStorage", "Error loading image", exception);
                                    });
                        }
                    }
                });
    }

    private void loadMessages(String groupId) {
        db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        messagesRef.whereEqualTo("group_id", groupId)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
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
                    }
                });
    }
    private void sendMessage(String content) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String groupId = getIntent().getStringExtra("groupId");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000, 0);

        String messageId = FirebaseFirestore.getInstance().collection("messages").document().getId();

        Message message = new Message();
        message.setSender_id(userId);
        message.setContent(content);
        message.setTimestamp(timestamp);
        message.setGroup_id(groupId);
        message.setIs_read(false);
        message.setMessage_id(messageId);
        message.setMessage_type("text");
        message.setStatus("sent");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .document(messageId)
                .set(message)
                .addOnSuccessListener(documentReference -> {
                    edt_content_chat.setText(""); // Xóa nội dung tin nhắn
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }

}
