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

        // Bắt đầu lắng nghe các thay đổi
        listenToUserGroups(userId);

        return view;
    }

    // Lắng nghe các nhóm mà người dùng tham gia
    private void listenToUserGroups(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupMembersRef = db.collection("group_members");

        groupMembersRef.whereEqualTo("user_id", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error listening to group_members changes", e);
                        return;
                    }

                    List<String> groupIds = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        groupIds.add(document.getString("group_id"));
                    }

                    // Lấy thông tin từng nhóm sau khi có danh sách groupIds
                    if (!groupIds.isEmpty()) {
                        for (String groupId : groupIds) {
                            getPrivateGroup(groupId);
                        }
                    }
                });
    }

    // Lấy thông tin nhóm riêng tư và lắng nghe tin nhắn
    private void getPrivateGroup(String groupId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsRef = db.collection("groups");

        groupsRef.whereEqualTo("group_id", groupId)
                .whereEqualTo("is_private", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Group group = document.toObject(Group.class);

                        // Nếu nhóm là private, bắt đầu lắng nghe tin nhắn và số lượng tin nhắn chưa đọc
                        if (group != null) {
                            getLastMessageForGroup(group);
                            countUnreadMessagesForGroup(group);
                            listenToMessagesForGroup(group);
                            listenToUnreadMessagesForGroup(group);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting private group", e);
                });
    }

    // Lắng nghe thay đổi tin nhắn trong nhóm và cập nhật tin nhắn cuối
    private void listenToMessagesForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        messagesRef.whereEqualTo("group_id", group.getGroup_id())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error listening to messages changes", e);
                        return;
                    }

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

                    // Cập nhật giao diện nếu Fragment đã được thêm vào
                    if (!isAdded()) return;

                    // Kiểm tra xem nhóm đã có trong danh sách chưa
                    if (!privateGroups.contains(group)) {
                        privateGroups.add(group);
                    }

                    // Cập nhật giao diện
                    if (groupAdapter == null) {
                        groupAdapter = new GroupAdapter(requireContext(), privateGroups);
                        recyclerView.setAdapter(groupAdapter);
                    } else {
                        groupAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Lắng nghe số lượng tin nhắn chưa đọc và cập nhật
    private void listenToUnreadMessagesForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        messagesRef.whereEqualTo("group_id", group.getGroup_id())
                .whereEqualTo("is_read", false)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error listening to unread messages changes", e);
                        return;
                    }

                    long unreadMessageCount = queryDocumentSnapshots.size();
                    Log.d("Firestore", "Unread messages count: " + unreadMessageCount);

                    // Cập nhật số tin nhắn chưa đọc vào đối tượng Group
                    group.setUnread_messages_count(unreadMessageCount);

                    // Cập nhật giao diện nếu Fragment đã được thêm vào
                    if (!isAdded()) return;

                    if (groupAdapter != null) {
                        groupAdapter.notifyDataSetChanged();
                    }
                });
    }

    // Lấy tin nhắn cuối cùng của nhóm
    private void getLastMessageForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

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

                    // Cập nhật giao diện nếu Fragment đã được thêm vào
                    if (!isAdded()) return;

                    // Kiểm tra xem nhóm đã có trong danh sách chưa
                    if (!privateGroups.contains(group)) {
                        privateGroups.add(group);
                    }

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

    // Đếm số lượng tin nhắn chưa đọc trong nhóm
    private void countUnreadMessagesForGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        messagesRef.whereEqualTo("group_id", group.getGroup_id())
                .whereEqualTo("is_read", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    long unreadMessageCount = 0;
                    if (queryDocumentSnapshots != null) {
                        unreadMessageCount = queryDocumentSnapshots.size();
                    }
                    Log.d("Firestore", "Unread messages count: " + unreadMessageCount);

                    // Cập nhật số lượng tin nhắn chưa đọc
                    group.setUnread_messages_count(unreadMessageCount);

                    // Cập nhật giao diện nếu Fragment đã được thêm vào
                    if (!isAdded()) return;

                    if (groupAdapter != null) {
                        groupAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error counting unread messages", e);
                });
    }
}
