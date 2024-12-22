package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.content.Intent;
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
import vn.edu.tlu.cse470_team8.model.Group;
import vn.edu.tlu.cse470_team8.view.ChatActivity;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private Context context;

    public GroupAdapter(Context context, List<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_message, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        // Set tên nhóm
        holder.groupName.setText(group.getGroup_name());
        // Set thời gian gửi tin nhắn cuối cùng bằng cách lấy dữ liệu từ firestore và chuyển đổi
        Timestamp timestamp = group.getLast_message_time();
        String formattedTime = formatTimestampToTime(timestamp);
        holder.sentTime.setText(formattedTime);
        // Set tin nhắn cuối
        holder.lastMessage.setText(group.getLast_message());
        // Set số tin nhắn chưa đọc
        holder.unreadMessagesCount.setText(String.valueOf(group.getUnread_messages_count()));

        // Set avatar (nếu có URL)
        holder.avatarImageView.setImageResource(R.drawable.logo_remove);

        // Thêm sự kiện click
        holder.itemView.setOnClickListener(v -> {
            // Chuyển đến ChatActivity và truyền groupId
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("groupId", group.getGroup_id());  // Truyền groupId
            intent.putExtra("groupName", group.getGroup_name());  // Truyền groupName
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView groupName;
        TextView sentTime;
        TextView lastMessage;
        TextView unreadMessagesCount;

        public GroupViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatar_group);
            groupName = itemView.findViewById(R.id.txt_group_name);
            sentTime = itemView.findViewById(R.id.txt_send_time);
            lastMessage = itemView.findViewById(R.id.txt_last_message);
            unreadMessagesCount = itemView.findViewById(R.id.txt_new_message);
        }
    }

    private String formatTimestampToTime(Timestamp timestamp) {
        if (timestamp == null) return ""; // Trường hợp không có timestamp

        // Chuyển Timestamp thành Date
        java.util.Date date = timestamp.toDate();

        // Định dạng thành giờ:phút (HH:mm)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
