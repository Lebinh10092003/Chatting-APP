package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.GroupAdapter;
import vn.edu.tlu.cse470_team8.model.Group;

public class GroupFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> publicGroups = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String userId;

    // Listener để lắng nghe sự thay đổi
    private ListenerRegistration groupListener;
    private ListenerRegistration messageListener;

    public GroupFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        // Lấy userId từ SharedPreferences
        userId = sharedPreferences.getString("userId", null);

        recyclerView = view.findViewById(R.id.rcv_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getUserGroups(userId);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Đăng ký lắng nghe sự thay đổi khi fragment được hiển thị
        if (messageListener == null) {
            setupMessageListener();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        // Hủy listener khi fragment không còn hiển thị
        if (groupListener != null) {
            groupListener.remove();
            groupListener = null;
        }
        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
        }
    }


    private void setupMessageListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        // Lắng nghe sự thay đổi trong tin nhắn của các nhóm
        messageListener = messagesRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }

            // Kiểm tra lại tin nhắn trong từng nhóm để cập nhật
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String groupId = document.getString("group_id");
                Group group = findGroupById(groupId);
                if (group != null) {
                    getLastMessageForGroup(group);
                    countUnreadMessagesForGroup(group);
                }
            }
        });
    }

    private void getUserGroups(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupMembersRef = db.collection("group_members");

        // Truy vấn tất cả nhóm mà userId tham gia
        groupMembersRef.whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> groupIds = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        groupIds.add(document.getString("group_id"));
                    }

                    // Sau khi có danh sách groupId, lấy từng nhóm riêng lẻ
                    if (!groupIds.isEmpty()) {
                        for (String groupId : groupIds) {
                            getPublicGroup(groupId);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting user groups", e);
                });
    }

    private void getPublicGroup(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsRef = db.collection("groups");

        // Truy vấn nhóm công khai với group_id
        groupsRef.whereEqualTo("group_id", groupId)
                .whereEqualTo("is_private", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Group group = document.toObject(Group.class);

                        // Kiểm tra nếu nhóm đã có trong danh sách publicGroups
                        if (group != null && !publicGroups.contains(group)) {
                            // Thêm nhóm vào danh sách publicGroups nếu chưa có
                            publicGroups.add(group);

                            // Tiến hành lấy tin nhắn cuối cùng và đếm tin nhắn chưa đọc
                            getLastMessageForGroup(group);
                            countUnreadMessagesForGroup(group);

                            // Cập nhật giao diện
                            if (groupAdapter == null) {
                                groupAdapter = new GroupAdapter(requireContext(), publicGroups);
                                recyclerView.setAdapter(groupAdapter);
                            } else {
                                groupAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting public group", e);
                });
    }



    private Group findGroupById(String groupId) {
        for (Group group : publicGroups) {
            if (group.getGroup_id().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    private void getLastMessageForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        // Lấy tin nhắn cuối cùng cho nhóm theo group_id
        messagesRef.whereEqualTo("group_id", group.getGroup_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    DocumentSnapshot lastMessageDoc = null;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        if (lastMessageDoc == null ||
                                document.getTimestamp("timestamp").compareTo(lastMessageDoc.getTimestamp("timestamp")) > 0) {
                            lastMessageDoc = document;
                        }
                    }

                    if (lastMessageDoc != null) {
                        group.setLast_message(lastMessageDoc.getString("content"));
                        group.setLast_message_time(lastMessageDoc.getTimestamp("timestamp"));
                    }

                    // Cập nhật giao diện nếu nhóm chưa có trong danh sách
                    if (!publicGroups.contains(group)) {
                        publicGroups.add(group);
                    }

                    if (groupAdapter == null) {
                        groupAdapter = new GroupAdapter(requireContext(), publicGroups);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        groupAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting last message for group", e);
                });
    }

    private void countUnreadMessagesForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        // Lọc tin nhắn của nhóm với trạng thái is_read = false
        messagesRef.whereEqualTo("group_id", group.getGroup_id())
                .whereEqualTo("is_read", false) // Trạng thái là false
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long unreadMessageCount = 0;

                    // Đếm số lượng tin nhắn có trạng thái is_read = false
                    if (queryDocumentSnapshots != null) {
                        unreadMessageCount = queryDocumentSnapshots.size();
                    }

                    // Hiển thị kết quả
                    Log.d("Firestore", "Unread messages count: " + unreadMessageCount);

                    // Bạn có thể lưu số lượng tin nhắn chưa đọc vào đối tượng Group nếu cần
                    group.setUnread_messages_count(unreadMessageCount);

                    // Cập nhật giao diện (nếu cần)
                    if (groupAdapter != null) {
                        groupAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error counting unread messages", e);
                });
    }
}
