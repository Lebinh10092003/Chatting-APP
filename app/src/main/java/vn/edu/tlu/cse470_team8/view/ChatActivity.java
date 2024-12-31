package vn.edu.tlu.cse470_team8.view;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.ChatAdapter;
import vn.edu.tlu.cse470_team8.controller.SuggestWordAdapter;
import vn.edu.tlu.cse470_team8.model.Message;
import vn.edu.tlu.cse470_team8.service.CallAPI;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView, rcv_suggest;
    private ChatAdapter chatAdapter;
    private SuggestWordAdapter suggestAdapter;
    private List<Message> messages = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView txt_group_name,txt_status,txt_suggest_word;
    private ImageButton bt_back,bt_add_media;
    private Button bt_send_message;
    private EditText edt_content_chat;
    private ImageView avatar_chat;
    private List<String> suggestWords = new ArrayList<>();

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
        //Lay ra avatar tu firebase dua vao groupId
        loadGroupInfo(groupId);
        Log.d("ChatActivity", "Group ID: " + groupId);


        // Cấu hình RecyclerView cho tin nhắn
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);

        suggestAdapter = new SuggestWordAdapter(this, suggestWords, word -> {
            // Xử lý khi người dùng nhấp vào một từ gợi ý
            String currentText = edt_content_chat.getText().toString();
            // Nếu hiện tại có văn bản, thêm dấu cách trước từ mới
            if (!currentText.isEmpty()) {
                currentText += " ";
            }
            // Nối từ gợi ý vào cuối
            currentText += word;
            edt_content_chat.setText(currentText);
            // Đặt con trỏ vào cuối văn bản (tự động di chuyển đến cuối sau khi thay đổi)
            edt_content_chat.setSelection(currentText.length());
        });

        // Thiết lập LayoutManager và Adapter cho RecyclerView
        rcv_suggest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcv_suggest.setAdapter(suggestAdapter);
        // Tải tin nhắn
        loadMessages(groupId);


        // Xử lý khi người dùng nhấp vào nút gửi tin nhắn
        bt_send_message.setOnClickListener(v -> {
            String messageContent = edt_content_chat.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);
                // Goi API
                CallAPI.initializeFirestore();
                CallAPI.callFlaskAPI();

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

        edt_content_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần xử lý
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (!text.isEmpty()) {
                    // Lấy từ cuối cùng trong chuỗi
                    String[] words = text.split("\\s+"); // Tách từ dựa trên khoảng trắng
                    String lastWord = words[words.length - 1]; // Lấy từ cuối cùng
                    // Gọi hàm loadSuggestions với từ cuối cùng
                    loadSuggestions(lastWord);
                }
            }
        });



    }

    protected void onResume() {
        super.onResume();
        // Reload lại dữ liệu tại đây
        String groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo(groupId);
        loadMessages(groupId);

    }

    private void loadSuggestions(String text) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference suggestionsRef = db.collection("q_table_learning");

        suggestionsRef
                .whereEqualTo("state", text) // Tìm kiếm theo từ khóa
                .limit(3) // Giới hạn số lượng kết quả trả về
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    suggestWords.clear(); // Xóa danh sách cũ
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String action = document.getString("action");
                        if (action != null) {
                            suggestWords.add(action); // Thêm từ gợi ý vào danh sách
                        }
                    }
                    suggestAdapter.notifyDataSetChanged(); // Cập nhật giao diện
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading suggestions: ", e);
                });
    }


    // Hàm tải thông tin nhóm (tên nhóm và ảnh nhóm)
    private void loadGroupInfo(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String groupName = documentSnapshot.getString("group_name");
                        String avatarUrl = documentSnapshot.getString("avatar_url");

                        // Log thông tin nhóm để kiểm tra
                        Log.d("ChatActivity", "Group Name: " + groupName);
                        Log.d("ChatActivity", "Avatar URL: " + avatarUrl);

                        // Cập nhật giao diện
                        if (groupName != null) {
                            txt_group_name.setText(groupName);
                        } else {
                            txt_group_name.setText("No Name"); // Tên mặc định nếu không có
                        }

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.logo_remove) // Avatar mặc định khi tải
                                    .error(R.drawable.logo_remove) // Avatar mặc định khi lỗi
                                    .into(avatar_chat);
                        } else {
                            avatar_chat.setImageResource(R.drawable.logo_remove); // Avatar mặc định
                        }
                    } else {
                        Log.w("ChatActivity", "Group document does not exist.");
                        txt_group_name.setText("Unknown Group"); // Tên mặc định nếu nhóm không tồn tại
                        avatar_chat.setImageResource(R.drawable.logo_remove); // Avatar mặc định
                    }
                })
                .addOnFailureListener(e -> {
                    // Log lỗi nếu có
                    Log.e("ChatActivity", "Error loading group info", e);
                    txt_group_name.setText("Error Loading"); // Tên mặc định khi lỗi
                    avatar_chat.setImageResource(R.drawable.logo_remove); // Avatar mặc định
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
