package vn.edu.tlu.cse470_team8.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.edu.tlu.cse470_team8.R;
public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firestore và SharedPreferences
        db = FirebaseFirestore.getInstance();
        // Lấy SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Lấy các view từ layout
        usernameEditText = findViewById(R.id.edt_user_name);
        passwordEditText = findViewById(R.id.edt_pass_word);
        loginButton = findViewById(R.id.bt_login);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String phone = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            // Hiển thị thông báo lỗi nếu thiếu thông tin
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm kiếm người dùng theo tên đăng nhập
        db.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Người dùng tồn tại, kiểm tra mật khẩu
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String storedPasswordHash = userDoc.getString("password_hash");

                        if (checkPassword(password, storedPasswordHash)) {
                            // Mật khẩu hợp lệ, cập nhật trạng thái và thông tin đăng nhập
                            updateLoginStatus(userDoc);

                            // Lưu userId vào SharedPreferences
                            String userId = userDoc.getString("user_id");
                            saveUserId(userId);

                            // Chuyển đến màn hình chính
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Mật khẩu sai
                            Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Tên đăng nhập không tồn tại
                        Toast.makeText(this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginError", "Error checking user", e);
                    Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean checkPassword(String inputPassword, String storedPasswordHash) {

        return inputPassword.equals(storedPasswordHash);
    }

    private void updateLoginStatus(DocumentSnapshot userDoc) {
        // Cập nhật trạng thái đăng nhập và thời gian đăng nhập
        String userId = userDoc.getString("user_id");
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "online");
        updates.put("last_login", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật trạng thái thành công
                    Log.d("Login", "User status updated successfully.");
                    Log.d("Login", "User status updated successfully: " + userId);
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi gặp lỗi
                    Log.e("Login", "Error updating user status", e);
                });
    }

    private void saveUserId(String userId) {
        // Xoa tat ca id cu
        sharedPreferences.edit().clear().apply();

        // Lưu userId vào SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();


    }
}


