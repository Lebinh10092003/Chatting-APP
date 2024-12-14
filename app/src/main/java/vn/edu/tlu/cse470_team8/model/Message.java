package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class Message {
    private String message_id;
    private String sender_id;
    private String group_id;
    private String message_type;
    private String content;
    private String status;
    private Timestamp created_at;

    public Message() {
    }
    public Message(String message_id, String sender_id, String group_id, String message_type, String content, String status, Timestamp created_at) {
        this.message_id = message_id;
        this.sender_id = sender_id;
        this.group_id = group_id;
        this.message_type = message_type;
        this.content = content;
        this.status = status;
        this.created_at = created_at;
    }

    public String getMessage_id() {
        return message_id;
    }
    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
    public String getSender_id() {
        return sender_id;
    }
    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }
    public String getGroup_id() {
        return group_id;
    }
    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
    public String getMessage_type() {
        return message_type;
    }
    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
