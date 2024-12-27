package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.Message;
import vn.edu.tlu.cse470_team8.model.User;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Message> chatMessages;
    private SharedPreferences sharedPreferences;
    private String userId;
    private FirebaseFirestore db;

    public ChatAdapter(Context context, List<Message> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");

        // Kiểm tra và khởi tạo Firestore nếu chưa khởi tạo
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        Log.d("ChatAdapter", "Initialized with userId: " + userId);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = chatMessages.get(position);
        holder.messageContent.setText(message.getContent());

        //Kiem tra nguoi dọc tin nhắn là ai
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("userId", "");
        if (message.getSender_id() != null && !message.getSender_id().equals(currentUserId) && !message.isIs_read()) {
            markMessageAsRead(message.getMessage_id());
        }
        // Kiểm tra nếu db đã được khởi tạo
        if (db != null && message.getSender_id() != null) {
            db.collection("users").document(message.getSender_id())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String avatarUrl = documentSnapshot.getString("avatar_url");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(context)
                                        .load(avatarUrl)
                                        .circleCrop()
                                        .into(holder.senderAvatar);
                            } else {
                                Glide.with(context)
                                        .load(R.drawable.logo_remove)
                                        .circleCrop()
                                        .into(holder.senderAvatar);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.d("ChatAdapter", "Failed to get sender's avatar: " + e.getMessage()));
        }

        if (message.getTimestamp() != null) {
            String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.getTimestamp().toDate());
            holder.timestamp.setText(formattedTime);
        }

        if (message.isIs_read()) {
            holder.status.setText("Read");
        } else {
            holder.status.setText("Unread");
        }

        if (getItemViewType(position) == 0) { // Kiểm tra nếu đây là tin nhắn nhận
            boolean translated = sharedPreferences.getBoolean("translated", false);
            if (translated) {
                if (holder.img_translator != null) {
                    holder.img_translator.setVisibility(View.VISIBLE);
                } else {
                    Log.e("ChatAdapter", "ImageButton img_translator is null");
                }
            } else {
                if (holder.img_translator != null) {
                    holder.img_translator.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, timestamp, status;
        ImageView senderAvatar;
        ImageButton img_translator;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.text_message_content);
            senderAvatar = itemView.findViewById(R.id.img_avatar_chat);
            timestamp = itemView.findViewById(R.id.txt_chat_send_time);
            status = itemView.findViewById(R.id.txt_is_read_status);
            img_translator = itemView.findViewById(R.id.img_translator);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = chatMessages.get(position);
        if (message.getSender_id() != null && message.getSender_id().equals(userId)) {
            return 1;
        } else {
            return 0;
        }
    }
    private void markMessageAsRead(String messageId) {
        db.collection("messages")
                .document(messageId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Kiểm tra trạng thái "is_read" của tin nhắn trước khi cập nhật
                    boolean isRead = documentSnapshot.getBoolean("is_read");
                    if (!isRead) {
                        // Cập nhật trạng thái "is_read" thành true
                        db.collection("messages")
                                .document(messageId)
                                .update("is_read", true)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ChatAdapter", "Message marked as read successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("ChatAdapter", "Error marking message as read: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ChatAdapter", "Failed to get message status: " + e.getMessage());
                });
    }
    private void translateMessage(String messageId) {
        db.collection("messages")
                .document(messageId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String messageContent = documentSnapshot.getString("content");
                        if (messageContent != null && !messageContent.isEmpty()) {
                            // Gọi API dịch ngôn ngữ

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ChatAdapter", "Failed to get message content: " + e.getMessage());
                });
    }


}

