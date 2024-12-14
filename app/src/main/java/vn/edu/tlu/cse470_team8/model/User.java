package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class User {
    private String user_id;
    private String username;
    private String phone;
    private String email;
    private String password_hash;
    private String avatar_url;
    private String status;
    private Timestamp last_login;
    private Boolean is_verified;
    private Timestamp created_at;

    public User() {
    }
    public User(String user_id, String username, String phone, String email, String password_hash, String avatar_url, String status, Timestamp last_login, Boolean is_verified, Timestamp created_at) {
        this.user_id = user_id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.password_hash = password_hash;
        this.avatar_url = avatar_url;
        this.status = status;
        this.last_login = last_login;
        this.is_verified = is_verified;
        this.created_at = created_at;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword_hash() {
        return password_hash;
    }
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }
    public String getAvatar_url() {
        return avatar_url;
    }
    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getLast_login() {
        return last_login;
    }
    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }
    public Boolean getIs_verified() {
        return is_verified;
    }
    public void setIs_verified(Boolean is_verified) {
        this.is_verified = is_verified;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

}
