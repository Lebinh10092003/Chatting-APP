package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class Notification {
    private String notification_id;
    private String sender_id;
    private String receiver_id;
    private String notification_type;
    private String content;
    private Timestamp created_at;

    public Notification() {
    }
    public Notification(String notification_id, String sender_id, String receiver_id, String notification_type, String content, Timestamp created_at) {
        this.notification_id = notification_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.notification_type = notification_type;
        this.content = content;
        this.created_at = created_at;
    }

    public String getNotification_id() {
        return notification_id;
    }
    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }
    public String getSender_id() {
        return sender_id;
    }
    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }
    public String getReceiver_id() {
        return receiver_id;
    }
    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }
    public String getNotification_type() {
        return notification_type;
    }
    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

}
