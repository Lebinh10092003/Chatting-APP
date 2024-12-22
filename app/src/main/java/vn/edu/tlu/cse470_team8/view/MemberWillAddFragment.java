package vn.edu.tlu.cse470_team8.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.MemberWillAddAdapter;
import vn.edu.tlu.cse470_team8.model.Group;
import vn.edu.tlu.cse470_team8.model.User;

public class MemberWillAddFragment extends Fragment {
    private Button btn_search_member, btn_create_new_public_group;
    private EditText edt_search_member;
    private String groupName;
    private String LogintuserId;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_public_group, container, false);


        // Khởi tạo userList và lấy dữ liệu người dùng từ bundle
        userList = new ArrayList<>();
        GetListUserFromBundel();

        // Thiết lập RecyclerView và Adapter
        RecyclerView recyclerView = view.findViewById(R.id.rcv_add_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));  // Thiết lập LayoutManager
        MemberWillAddAdapter adapter = new MemberWillAddAdapter(userList);
        recyclerView.setAdapter(adapter);

        btn_search_member = view.findViewById(R.id.btn_add_member);
        btn_create_new_public_group = view.findViewById(R.id.btn_create_public_group);
        edt_search_member = view.findViewById(R.id.edt_create_group_name);
        btn_search_member.setOnClickListener(v -> openSearchMemberFragment());
        btn_create_new_public_group.setOnClickListener(v -> {
            List<String> userIds = new ArrayList<>();
            groupName = edt_search_member.getText().toString();
            for (User user : userList) {
                userIds.add(user.getUser_id());
            }
            createNewPublicGroup(userIds,groupName);
        });

        return view;
    }

    private void openSearchMemberFragment() {
        SearchAddMemberFragment fragment = new SearchAddMemberFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_create_public_group, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void createNewPublicGroup(List<String> userIds,String GroupName) {
        // Kiểm tra xem danh sách người dùng có hợp lệ không
        if (userIds == null || userIds.isEmpty()) {
            Toast.makeText(getContext(), "Không có người dùng nào để tạo nhóm.", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = UUID.randomUUID().toString(); // Đảm bảo duy nhất

        // Thời gian tạo nhóm (sử dụng timestamp)
        Timestamp createdAt = new Timestamp(new java.util.Date());
        // Tạo dữ liệu nhóm để lưu vào Firestore
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("group_name", GroupName);
        groupData.put("group_id", groupId);
        groupData.put("avatar_url", "");
        groupData.put("is_private", false);
        groupData.put("created_at", createdAt);
        groupData.put("created_by", LogintuserId);

        // Lưu nhóm vào Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .document(groupId)  // Lưu với groupId
                .set(groupData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupInfo", "Group created successfully!");

                    // Thêm thành viên vào nhóm
                    for (String userId : userIds) {
                        addMemberToGroup(groupId, userId,LogintuserId);
                    }

                    // Cập nhật giao diện hoặc chuyển sang màn hình khác
                    Toast.makeText(getContext(), "Tạo nhóm thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyen den home activity voi fragment group
                    Intent homeActivity = new Intent(getContext(), HomeActivity.class);
                    startActivity(homeActivity);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frame_create_public_group, new GroupFragment())
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Log.w("GroupInfo", "Error creating group", e);
                    Toast.makeText(getContext(), "Lỗi khi tạo nhóm. Thử lại sau.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addMemberToGroup(String groupId, String userId,String loginUserId) {
        // Lưu thông tin thành viên vào subcollection "members" trong Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> memberData = new HashMap<>();
        memberData.put("user_id", userId);
        memberData.put("joined_at", new Timestamp(new java.util.Date()));
        // Nếu userId trùng với userId đang đăng nhập thì set role là "admin"
        if (userId.equals(loginUserId)) {
            memberData.put("role", "admin");
        } else {
            memberData.put("role", "member");
        }
        memberData.put("left_at", null);
        memberData.put("group_id", groupId);
        db.collection("group_members")
                .document(groupId + "_" + userId)  // Lưu với groupId_userId
                .set(memberData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("GroupInfo", "Member added to group successfully: " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.w("GroupInfo", "Error adding member to group: " + userId, e);
                });


    }

    private void GetListUserFromBundel() {
        Bundle bundle = getArguments();
        userList = new ArrayList<>();
        List<String> selectedUserIds = null;

        if (bundle != null) {
            Log.d("MemberWillAddFragment", "Bundle is not null");
            selectedUserIds = (List<String>) bundle.getSerializable("selectedUserIds");
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
            LogintuserId = sharedPreferences.getString("userId", "");
            // Thêm login user vào danh sách
            selectedUserIds.add(LogintuserId);

            Log.d("MemberWillAddFragment", "selectedUserIds: " + selectedUserIds);
        } else {
            Log.d("MemberWillAddFragment", "Bundle is null");
        }

        // Kiểm tra nếu selectedUserIds không null và không rỗng
        if (selectedUserIds != null && !selectedUserIds.isEmpty()) {
            // Lấy dữ liệu người dùng từ Firestore dựa trên list user ids từ bundle
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereIn("user_id", selectedUserIds)  // Sử dụng selectedUserIds trong query
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                users.add(user);
                            }
                            // Cập nhật RecyclerView với danh sách người dùng
                            updateRecyclerView(users);
                        } else {
                            Log.d("MemberWillAddFragment", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            Log.d("MemberWillAddFragment", "No selected user IDs found");
        }
    }

    private void updateRecyclerView(List<User> users) {
        // Cập nhật lại danh sách người dùng
        userList.clear();
        userList.addAll(users);
        if (userList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy người dùng nào phù hợp.", Toast.LENGTH_SHORT).show();
        }

        // Tạo adapter và gán vào RecyclerView
        MemberWillAddAdapter adapter = new MemberWillAddAdapter(userList);
        RecyclerView recyclerView = getView().findViewById(R.id.rcv_add_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Cập nhật dữ liệu trong adapter
        adapter.notifyDataSetChanged();  // Gọi notifyDataSetChanged trong adapter để làm mới UI
    }

}
