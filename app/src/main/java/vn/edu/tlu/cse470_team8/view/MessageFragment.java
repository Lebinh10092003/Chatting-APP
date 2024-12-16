package vn.edu.tlu.cse470_team8.view;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.GroupAdapter;
import vn.edu.tlu.cse470_team8.model.Group;

public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> privateGroups = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String userId;


    public MessageFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        // Lấy userId từ SharedPreferences
        userId = sharedPreferences.getString("userId", null);

        recyclerView = view.findViewById(R.id.rcv_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getUserGroups(userId);

        return view;
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
                            getPrivateGroup(groupId);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting user groups", e);
                });
    }

    private void getPrivateGroup(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsRef = db.collection("groups");

        // Lấy thông tin nhóm theo group_id và kiểm tra nếu là nhóm private
        groupsRef.whereEqualTo("group_id", groupId)
                .whereEqualTo("is_private", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Group group = document.toObject(Group.class);

                        // Tiếp tục lấy tin nhắn cuối cùng và đếm số tin nhắn chưa đọc nếu nhóm tồn tại
                        if (group != null) {
                            getLastMessageForGroup(group);
                            countUnreadMessagesForGroup(group);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting private group", e);
                });
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
                            Log.d("Firestore", "Last message: " + lastMessageDoc.getString("content"));
                        }
                    }

                    if (lastMessageDoc != null) {
                        group.setLast_message(lastMessageDoc.getString("content"));
                        group.setLast_message_time(lastMessageDoc.getTimestamp("timestamp"));
                    }

                    // Thêm nhóm vào danh sách hiển thị
                    privateGroups.add(group);

                    // Cập nhật giao diện
                    if (groupAdapter == null) {
                        groupAdapter = new GroupAdapter(requireContext(), privateGroups);
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