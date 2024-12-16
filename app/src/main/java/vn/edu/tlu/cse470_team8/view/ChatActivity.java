package vn.edu.tlu.cse470_team8.view;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.ChatAdapter;
import vn.edu.tlu.cse470_team8.model.Message;
import vn.edu.tlu.cse470_team8.model.Group;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messages = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView txt_group_name,txt_status;
    private ImageButton bt_back,bt_add_media;
    private Button bt_send_message;
    private EditText edt_content_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.rcv_chat_message);
        txt_group_name = findViewById(R.id.txt_group_chat);
        txt_status = findViewById(R.id.txt_status_chat);
        bt_back = findViewById(R.id.bt_back_chat);
        bt_add_media = findViewById(R.id.bt_add_media);
        bt_send_message = findViewById(R.id.bt_send_message);
        edt_content_chat = findViewById(R.id.edt_chat_message);

        // Lấy thông tin nhóm từ intent
        String groupId = getIntent().getStringExtra("groupId");
        String groupName = getIntent().getStringExtra("groupName");

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);

        // Set ten nhom
        txt_group_name.setText(groupName);
        loadMessages(groupId);
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

                    // Sắp xếp thủ công dựa trên timestamp
                    messages.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

                    // Cập nhật RecyclerView
                    chatAdapter.notifyDataSetChanged();

                    // Scroll xuống tin nhắn mới nhất
                    recyclerView.scrollToPosition(messages.size() - 1);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting messages", e);
                });
    }

}
