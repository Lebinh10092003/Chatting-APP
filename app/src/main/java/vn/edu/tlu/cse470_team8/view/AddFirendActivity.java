package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.AddFriendAdapter;
import vn.edu.tlu.cse470_team8.model.User;

public class AddFirendActivity extends AppCompatActivity {
    private ImageButton bt_back_add_firend,bt_add_friend;
    private EditText edt_phone_add_friend;
    private RecyclerView rv_add_friend;
    private List<User> userList = new ArrayList<>();
    private AddFriendAdapter AddFriendAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_firend);

        // Anh xa
        bt_back_add_firend = findViewById(R.id.bt_back_add_firend);
        edt_phone_add_friend = findViewById(R.id.edt_phone_add_friend);
        rv_add_friend = findViewById(R.id.rcv_add_friend);

        // Set su kien
        bt_back_add_firend.setOnClickListener(v -> finish());
        // Khi người dùng nhập số điện thoại, tìm kiếm người dùng
        edt_phone_add_friend.setOnEditorActionListener((v, actionId, event) -> {
            String phone = edt_phone_add_friend.getText().toString();
            searchUser(phone);
            return true;
        });

        // Xu ly recyclerview
        AddFriendAdapter addFriendAdapter = new AddFriendAdapter(this, userList);
        rv_add_friend.setLayoutManager(new LinearLayoutManager(this));
        rv_add_friend.setAdapter(addFriendAdapter);

        // Lưu adapter vào biến để cập nhật khi tìm kiếm
        this.AddFriendAdapter = addFriendAdapter;
    }



    private void searchUser(String phone) {
        // Lấy dữ liệu từ Firestore và lưu vào List
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                }
                // Lưu lại danh sách người dùng để tìm kiếm sau
                filterUserList(userList, phone);
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });

    }

    // Hàm lọc dữ liệu người dùng
    private void filterUserList(List<User> userList, String searchQuery) {
        List<User> filteredList = new ArrayList<>();

        // Chuyển searchQuery thành chữ thường để dễ dàng so sánh
        String query = searchQuery.toLowerCase();

        // Duyệt qua tất cả người dùng và kiểm tra nếu username hoặc phone chứa searchQuery
        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(query) || user.getPhone().contains(query)) {
                filteredList.add(user);
            }
        }
        // Cập nhật dữ liệu cho RecyclerView
        updateRecyclerView(filteredList);
    }

    // Cập nhật danh sách người dùng vào RecyclerView
    private void updateRecyclerView(List<User> filteredList) {
        // Cập nhật dữ liệu vào Adapter
        AddFriendAdapter.setUserList(filteredList);
        AddFriendAdapter.notifyDataSetChanged();
    }



}