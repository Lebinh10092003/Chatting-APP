package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Message> chatMessages;
    private SharedPreferences sharedPreferences;
    private String userId;

    public ChatAdapter(Context context, List<Message> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");
        Log.d("ChatAdapter", "Initialized with userId: " + userId);
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        // Kiem tra neu user_id == sender_id thi hien thi tin nhan ben phai
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
        // Hiển thị nội dung tin nhắn
        Log.d("ChatAdapter", "Binding message: " + message.getContent());
        holder.messageContent.setText(message.getContent());
        // Hiển thị anh đại diện của người gửi
        holder.senderAvatar.setImageResource(R.drawable.logo_remove);

        // Hiển thị thời gian tin nhắn
        if (message.getTimestamp() != null) {
            String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.getTimestamp().toDate());
            holder.timestamp.setText(formattedTime);
        }

        // Hiển thị trạng thái đọc của tin nhắn
        if (message.isIs_read()) {
            holder.status.setText("Read");
        } else {
            holder.status.setText("Unread");
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent, timestamp, status;
        ImageView senderAvatar;

        public ChatViewHolder(View itemView) {
            super(itemView);
            // Ánh xạ các thành phần của item layout
            messageContent = itemView.findViewById(R.id.text_message_content);
            senderAvatar = itemView.findViewById(R.id.img_avatar_chat);
            timestamp = itemView.findViewById(R.id.txt_chat_send_time);
            status = itemView.findViewById(R.id.txt_is_read_status);
        }
    }
    @Override
    public int getItemViewType(int position) {
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        // Nếu tin nhắn được gửi bởi người dùng hiện tại, trả về 1
        if (chatMessages.get(position).getSender_id().equals(userId)) {
            return 1;
        } else {
            return 0;
        }
    }

}
