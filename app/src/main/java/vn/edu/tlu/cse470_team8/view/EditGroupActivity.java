package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import vn.edu.tlu.cse470_team8.R;

public class EditGroupActivity extends AppCompatActivity {
    private Button edtiGroupBtnEdit,btnSelectGroupAvatar;
    private ImageView groupAvatar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String groupId = getIntent().getStringExtra("groupId");
        groupAvatar = findViewById(R.id.edit_group_image);
        btnSelectGroupAvatar = findViewById(R.id.edit_group_btn_change_avatar);
        // Lấy thông tin nhóm từ  firebase dua vao groupId
        loadGroupInfo(groupId);
        // Xử lý sự kiện khi người dùng nhấn nút quay lại
        findViewById(R.id.edit_group_txt_back).setOnClickListener(v -> finish());
        edtiGroupBtnEdit = findViewById(R.id.edit_group_btn_edit);
        // Xử lý sự kiện khi người dùng nhấn nút cập nhật thông tin nhóm
        edtiGroupBtnEdit.setOnClickListener(v -> {
            // Cho phep sua ten nhom
            updateGroupInfo(groupId);
        });

        btnSelectGroupAvatar.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .crop()                // Tùy chọn cắt ảnh
                    .compress(1024)        // Giới hạn dung lượng
                    .maxResultSize(1080, 1080) // Giới hạn kích thước
                    .start();  // Bắt đầu lựa chọn ảnh
        });


    }

    private void loadGroupInfo(String groupId) {
        // Truy cập đến Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn nhóm theo groupId
        db.collection("groups") // Giả sử tên bộ sưu tập là "groups"
                .document(groupId) // Truyền groupId vào để lấy thông tin nhóm
                .get() // Lấy tài liệu của nhóm
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy thông tin nhóm từ tài liệu
                        String groupName = documentSnapshot.getString("group_name");
                        String avatarUrl = documentSnapshot.getString("avatar_url");
                        boolean isPrivate = documentSnapshot.getBoolean("is_private");
                        Timestamp createdAt = documentSnapshot.getTimestamp("created_at");
                        String createdBy = documentSnapshot.getString("created_by");

                        // Hiển thị thông tin nhóm (Ví dụ: Hiển thị lên TextView)
                        TextView groupNameTextView = findViewById(R.id.edit_group_edt_group_name);
                        groupNameTextView.setText(groupName);
                        // Hiển thị avatar (nếu có URL)
                        ImageView avatarImageView = findViewById(R.id.edit_group_image);
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(this) // Dùng Glide để tải ảnh từ URL
                                    .load(avatarUrl)
                                    .into(avatarImageView);
                        }


                    } else {
                        Toast.makeText(this, "Nhóm không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu có
                    Log.e("LoadGroupInfo", "Lỗi khi tải thông tin nhóm", e);
                    Toast.makeText(this, "Không thể tải thông tin nhóm!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateGroupInfo(String groupId) {
        // Truy cập đến Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy thông tin mới từ UI (ví dụ: tên nhóm, avatar URL, trạng thái riêng tư)
        EditText groupNameEditText = findViewById(R.id.edit_group_edt_group_name);
        String newGroupName = groupNameEditText.getText().toString().trim();

        // Kiểm tra xem tên nhóm có trống không, nếu có thì yêu cầu người dùng nhập tên nhóm
        if (newGroupName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nhóm!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một đối tượng map để lưu trữ các trường cần cập nhật
        Map<String, Object> updatedGroupInfo = new HashMap<>();
        updatedGroupInfo.put("group_name", newGroupName);

        // Cập nhật thông tin nhóm trong Firestore
        db.collection("groups").document(groupId)
                .update(updatedGroupInfo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thông tin nhóm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateGroupInfo", "Lỗi khi cập nhật thông tin nhóm", e);
                    Toast.makeText(this, "Không thể cập nhật thông tin nhóm!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData(); // URI của ảnh được chọn
            groupAvatar.setImageURI(fileUri); // Hiển thị ảnh trên ImageView (Ảnh nhóm)

            // Gọi hàm tải ảnh lên Firebase
            uploadGroupImageToFirebase(fileUri);

            // Dùng Glide để tải ảnh vào ImageView
            Glide.with(this)
                    .load(fileUri)
                    .into(groupAvatar);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(EditGroupActivity.this, "Chọn ảnh", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(EditGroupActivity.this, "Hủy chọn ảnh", Toast.LENGTH_SHORT).show();

        }
    }
    private void uploadGroupImageToFirebase(Uri fileUri) {
        if (fileUri != null) {
            // Lấy groupId từ Intent (hoặc SharedPreferences)
            String groupId = getIntent().getStringExtra("groupId");

            if (groupId != null && !groupId.isEmpty()) {
                // Tạo tham chiếu đến vị trí lưu ảnh nhóm mới trong Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference("group_avatars/" + System.currentTimeMillis() + ".jpg");

                // Tải ảnh nhóm lên Firebase Storage
                storageReference.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Lấy URL của ảnh vừa tải lên
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String avatarUrl = uri.toString();
                                // Lưu URL ảnh vào Firestore trong tài liệu nhóm
                                saveGroupAvatarUrlToDatabase(groupId, avatarUrl);

                                // Hiển thị ảnh nhóm mới (dùng Glide)
                                Glide.with(this)
                                        .load(avatarUrl) // Hiển thị ảnh từ URL Firebase
                                        .into(groupAvatar);

                                Toast.makeText(this, "Tải ảnh nhóm lên thành công!", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Tải ảnh nhóm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Không tìm thấy groupId", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveGroupAvatarUrlToDatabase(String groupId, String avatarUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("groups").document(groupId)
                .update("avatar_url", avatarUrl)  // Cập nhật URL ảnh nhóm
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "Đã cập nhật URL ảnh nhóm");
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Lỗi cập nhật URL ảnh nhóm", e);
                });
    }





}