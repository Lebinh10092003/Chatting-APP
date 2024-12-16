package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class Message {
    private String content;
    private String group_id;
    private boolean is_read;
    private String message_id;
    private String message_type;
    private String sender_id;
    private String status;
    private Timestamp timestamp;  // Sử dụng kiểu Timestamp từ Firebase

    // Constructor mặc định
    public Message() {}

    // Constructor với các tham số
    public Message(String content, String group_id, boolean is_read, String message_id, String message_type,
                   String sender_id, String status, Timestamp timestamp) {
        this.content = content;
        this.group_id = group_id;
        this.is_read = is_read;
        this.message_id = message_id;
        this.message_type = message_type;
        this.sender_id = sender_id;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Các phương thức getter và setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
