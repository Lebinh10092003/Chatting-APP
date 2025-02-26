package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.controller.MemberSearchAdapter;
import vn.edu.tlu.cse470_team8.model.User;

import android.os.Handler;
import android.os.Looper;

public class SearchAddMemberFragment extends Fragment {
    private List<User> userList = new ArrayList<>();
    private List<String> selectedUserIds = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText edtSearchAddMember;
    private MemberSearchAdapter adapter;
    private ProgressDialog progressDialog;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_member_group, container, false);

        // Nhận dữ liệu từ Fragment
        if (getArguments() != null) {
            selectedUserIds = getArguments().getStringArrayList("currentUserIds");
        }

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rcv_search_add_group_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter
        adapter = new MemberSearchAdapter(userList, selectedUserIds, this::onUserIdsSelected);
        recyclerView.setAdapter(adapter);

        // Ánh xạ EditText và xử lý tìm kiếm
        edtSearchAddMember = view.findViewById(R.id.edt_search_add_member);
        edtSearchAddMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String phone = s.toString().trim();
                    if (!phone.isEmpty()) {
                        searchUser(phone);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Xử lý nút xác nhận
        Button btnConfirmSelection = view.findViewById(R.id.btn_add_member_group);
        btnConfirmSelection.setOnClickListener(v -> {
            if (selectedUserIds.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một thành viên.", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedUserIds", (ArrayList<String>) selectedUserIds);

                MemberWillAddFragment fragment = new MemberWillAddFragment();
                fragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_create_public_group, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void onUserIdsSelected(List<String> updatedUserIds) {
        this.selectedUserIds = updatedUserIds;
    }

    private void searchUser(String query) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tìm kiếm...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String phonePrefix = query.toLowerCase();
        String nameQuery = query.toLowerCase();

        // Danh sách kết quả tìm kiếm
        List<User> searchResults = new ArrayList<>();

        // Truy vấn theo số điện thoại
        db.collection("users")
                .whereGreaterThanOrEqualTo("phone", phonePrefix)
                .whereLessThan("phone", phonePrefix + "\uF8FF")
                .get()
                .addOnCompleteListener(phoneTask -> {
                    if (phoneTask.isSuccessful() && phoneTask.getResult() != null) {
                        for (QueryDocumentSnapshot document : phoneTask.getResult()) {
                            User user = document.toObject(User.class);
                            searchResults.add(user);
                        }
                    }

                    // Truy vấn theo tên người dùng
                    db.collection("users")
                            .whereGreaterThanOrEqualTo("username", nameQuery)
                            .whereLessThan("username", nameQuery + "\uF8FF")
                            .get()
                            .addOnCompleteListener(nameTask -> {
                                progressDialog.dismiss();
                                if (nameTask.isSuccessful() && nameTask.getResult() != null) {
                                    for (QueryDocumentSnapshot document : nameTask.getResult()) {
                                        User user = document.toObject(User.class);
                                        if (!searchResults.contains(user)) {
                                            searchResults.add(user); // Tránh trùng lặp
                                        }
                                    }

                                    // Cập nhật RecyclerView với kết quả tìm kiếm
                                    if (!searchResults.isEmpty()) {
                                        updateRecyclerView(searchResults);
                                    } else {
                                        Toast.makeText(getContext(), "Không tìm thấy người dùng nào phù hợp.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi tìm kiếm theo tên. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Log.e("Firestore", "Error searching by username: " + e.getMessage());
                                Toast.makeText(getContext(), "Lỗi khi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("Firestore", "Error searching by phone: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi khi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    private void updateRecyclerView(List<User> users) {
        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();
    } 

}
