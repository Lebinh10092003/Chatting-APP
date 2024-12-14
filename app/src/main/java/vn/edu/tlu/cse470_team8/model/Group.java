package vn.edu.tlu.cse470_team8.model;

import com.google.firebase.Timestamp;

public class Group {
    private String group_id;
    private String group_name;
    private String group_type;
    private String created_by;
    private Timestamp created_at;
    private Boolean is_private;
    private String avatar_url;

    public Group() {
    }
    public Group(String group_id, String group_name, String group_type, String created_by, Timestamp created_at, Boolean is_private, String avatar_url) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.created_by = created_by;
        this.created_at = created_at;
        this.is_private = is_private;
        this.avatar_url = avatar_url;
    }
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
    public Boolean getIs_private() {
        return is_private;
    }
    public void setIs_private(Boolean is_private) {
        this.is_private = is_private;
    }
    public String getAvatar_url() {
        return avatar_url;
    }
    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

}
