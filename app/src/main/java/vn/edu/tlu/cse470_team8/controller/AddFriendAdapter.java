package vn.edu.tlu.cse470_team8.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.Message;
import vn.edu.tlu.cse470_team8.model.User;
import vn.edu.tlu.cse470_team8.view.HomeActivity;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendViewHolder> {
    private Context context;
    private List<User> userList;

    public AddFriendAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();  // Cập nhật dữ liệu khi danh sách thay đổi
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_friend, parent, false);
        return new AddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendViewHolder holder, int position) {
        // Bind dữ liệu vào view tại vị trí position
        User user = userList.get(position);
        Log.d("AddFriendAdapter", "Binding user: " + user.getUsername());
        // Hiển thị thông tin user lên view
        ImageView img_avatar = holder.itemView.findViewById(R.id.avatar_add_friend);
        TextView txt_username = holder.itemView.findViewById(R.id.txt_add_friend_name);
        TextView txt_email = holder.itemView.findViewById(R.id.txt_add_friend_email);
        txt_username.setText(user.getUsername());
        txt_email.setText(user.getEmail());
        img_avatar.setImageResource(R.drawable.logo_remove);
        ImageButton bt_add_friend = holder.itemView.findViewById(R.id.imgBt_add_friend);

        bt_add_friend.setOnClickListener(v -> {
            // Lấy userId từ SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", null);
            String friendId = user.getUser_id();
            Log.d("AddFriendAdapter", "Adding friend: " + friendId);
            checkIfGroupExists(userId, friendId);
            new Handler().postDelayed(() -> bt_add_friend.setEnabled(true), 5000);
            // Quay lại HomeActivity sau khi thêm bạn thành công
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            Intent intent = new Intent(activity, HomeActivity.class);
            intent.putExtra("fragment", "message");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa tất cả các Activity trên stack
            activity.startActivity(intent);
            activity.finish(); // Kết thúc Activity hiện tại

        });

    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class AddFriendViewHolder extends RecyclerView.ViewHolder {
        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
    private void checkIfGroupExists(String userId1, String userId2) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupMembersRef = db.collection("group_members");

        // Truy vấn tất cả nhóm mà user1 tham gia
        groupMembersRef.whereEqualTo("user_id", userId1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> user1GroupIds = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String groupId = document.getString("group_id");
                    user1GroupIds.add(groupId);
                }

                // Lấy tất cả nhóm mà user2 tham gia và kiểm tra nhóm chung
                groupMembersRef.whereEqualTo("user_id", userId2).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        boolean hasCommonGroup = false;
                        for (QueryDocumentSnapshot document : task2.getResult()) {
                            String groupId = document.getString("group_id");

                            // Kiểm tra nếu groupId của user2 có trong danh sách nhóm của user1
                            if (user1GroupIds.contains(groupId)) {
                                hasCommonGroup = true;
                                Log.d("CheckGroup", "User1 and User2 already have a common group with ID: " + groupId);
                                break;  // Kết thúc nếu đã có nhóm chung
                            }
                        }

                        // Nếu không có nhóm chung, tạo nhóm mới
                        if (!hasCommonGroup) {
                            createNewGroup(userId1, userId2);
                        } else {
                            Toast.makeText(context, "You already have a common group!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Error", "Error fetching user2 groups", task2.getException());
                    }
                });
            } else {
                Log.e("Error", "Error fetching user1 groups", task.getException());
            }
        });
    }

    private void createNewGroup(String userId1, String userId2) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        CollectionReference groupsRef = db.collection("groups");

        // Tạo tên file avatar ngẫu nhiên với thời gian hiện tại
        String avatarFileName = "avatar_" + Instant.now().getEpochSecond() + ".jpg";
        StorageReference avatarRef = storageRef.child("avatars/" + avatarFileName);

        // Đường dẫn ảnh từ resources (thay bằng ảnh thật trong ứng dụng của bạn)
        Uri avatarUri = Uri.parse("android.resource://vn.edu.tlu.cse470_team8/drawable/logo_remove");

        // Tải ảnh lên Storage
        UploadTask uploadTask = avatarRef.putFile(avatarUri);

        // Tạo ID nhóm mới
        String groupId = UUID.randomUUID().toString();

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Lấy URL ảnh sau khi tải lên thành công
            avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String avatarUrl = uri.toString();

                // Tạo dữ liệu nhóm mới
                Map<String, Object> groupData = new HashMap<>();
                groupData.put("avatar_url", avatarUrl);
                groupData.put("created_at", FieldValue.serverTimestamp());
                groupData.put("created_by", userId1);
                groupData.put("group_id", groupId);
                groupData.put("group_name", "Group of " + userId1 + " & " + userId2);
                groupData.put("is_private", true);

                // Lưu nhóm vào Firestore với ID là newGroupId
                groupsRef.document(groupId).set(groupData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thêm user1 và user2 vào nhóm
                        addUserToGroupPrivate(groupId, userId1);
                        addUserToGroupPrivate(groupId, userId2);

                        Log.d("CreateGroup", "Group created successfully: " + groupId);
                    } else {
                        Log.e("CreateGroup", "Error creating group: ", task.getException());
                    }
                });
            }).addOnFailureListener(e -> {
                Log.e("CreateGroup", "Error getting avatar URL: ", e);
            });
        }).addOnFailureListener(e -> {
            Log.e("CreateGroup", "Error uploading avatar: ", e);
        });
    }



    private void addUserToGroupPrivate(String groupId, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupMembersRef = db.collection("group_members");

        Map<String, Object> groupMemberData = new HashMap<>();
        groupMemberData.put("group_id", groupId);
        groupMemberData.put("joined_at", FieldValue.serverTimestamp());
        groupMemberData.put("left_at", null);
        groupMemberData.put("user_id", userId);
        groupMemberData.put("role", "admin");


        groupMembersRef.add(groupMemberData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AddUserToGroup", "User added to group successfully.");
                Toast.makeText(context, "Group created and users added!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("AddUserToGroup", "Error adding user to group: ", task.getException());
            }
        });
    }


}

