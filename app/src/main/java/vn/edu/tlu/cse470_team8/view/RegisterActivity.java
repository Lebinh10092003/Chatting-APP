package vn.edu.tlu.cse470_team8.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText edt_name_register,edt_phone_register, edt_password_register, edt_confirm_password_register;
    private Button bt_register;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edt_name_register = findViewById(R.id.edt_name_register);
        edt_phone_register = findViewById(R.id.edt_phone_register);
        edt_password_register = findViewById(R.id.edt_password_register);
        edt_confirm_password_register = findViewById(R.id.edt_password_comfirm_register);
        bt_register = findViewById(R.id.btn_register);
        bt_register.setOnClickListener(view -> {
            String phone = edt_phone_register.getText().toString();
            String name = edt_name_register.getText().toString();
            String password = edt_password_register.getText().toString();
            String confirmPassword = edt_confirm_password_register.getText().toString();

            // Kiểm tra dữ liệu hợp lệ
            if (phone.length() != 10) {
                edt_phone_register.setError("Số điện thoại không hợp lệ");
                return;
            }
            if (password.length() < 6) {
                edt_password_register.setError("Mật khẩu phải có ít nhất 6 ký tự");
                return;
            }
            if (!password.equals(confirmPassword)) {
                edt_confirm_password_register.setError("Mật khẩu không trùng khớp");
                return;
            }

            // Kiểm tra số điện thoại đã tồn tại
            db.collection("users")
                    .whereEqualTo("phone", phone)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                edt_phone_register.setError("Số điện thoại đã tồn tại");
                            } else {
                                // Nếu không tồn tại, tiến hành tạo tài khoản
                                createUser(name, phone, password);
                                //Sau khi dang ky thanh cong thi quay ve login
                                finish();

                            }
                        } else {
                            Toast.makeText(this, "Có lỗi xảy ra khi kiểm tra số điện thoại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });



    }
    // ham tao user moi
    private void createUser(String name, String phone, String password) {
        // Tao userId moi
        String userId = "user" + System.currentTimeMillis();
        String avatar_url = "";
        Timestamp created_at = Timestamp.now();
        String email = "";
        Boolean is_verified = false;
        Timestamp last_login = Timestamp.now();
        String status = "offline";
        User user = new User(userId, name, phone, email, password, avatar_url, status, last_login, is_verified, created_at);
        db = FirebaseFirestore.getInstance();
        // Luu user vao database
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                });
    }

}