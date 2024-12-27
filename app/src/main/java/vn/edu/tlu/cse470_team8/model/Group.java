package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class Group {
    private String group_id;
    private String group_name;
    private String group_type;
    private String created_by;
    private Timestamp created_at;
    private boolean is_private;
    private String avatar_url;

    // Thuộc tính mới
    private String last_message;
    private Timestamp last_message_time;
    private Long unread_messages_count; // Dùng Long thay vì int để tương thích với Firestore.

    // Constructor mặc định
    public Group() {
    }

    // Constructor đầy đủ
    public Group(String group_id, String group_name, String group_type, String created_by, Timestamp created_at, boolean is_private, String avatar_url, String last_message, Timestamp last_message_time, Long unread_messages_count) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.created_by = created_by;
        this.created_at = created_at;
        this.is_private = is_private;
        this.avatar_url = avatar_url;
        this.last_message = last_message;
        this.last_message_time = last_message_time;
        this.unread_messages_count = unread_messages_count;
    }

    // Getter và Setter
    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public boolean isPrivate() {
        return is_private;
    }

    // Setter
    public void setPrivate(boolean is_private) {
        this.is_private = is_private;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public Timestamp getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(Timestamp last_message_time) {
        this.last_message_time = last_message_time;
    }

    public Long getUnread_messages_count() {
        return unread_messages_count;
    }

    public void setUnread_messages_count(Long unread_messages_count) {
        this.unread_messages_count = unread_messages_count;
    }
}
