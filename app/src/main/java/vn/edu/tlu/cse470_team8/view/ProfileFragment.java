package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.tlu.cse470_team8.R;

public class ProfileFragment extends Fragment {
    private Button btnChange_Save, btnLogout_Cancel, btnSelectAvatar;
    private EditText edtName, edtPhone, edtGender, edtBirthday, edtEmail;
    private ImageView avatar;
    private SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Boolean status_edit = false;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View profile = inflater.inflate(R.layout.fragment_profile, container, false);
        avatar = profile.findViewById(R.id.profile_avatar);
        edtName = profile.findViewById(R.id.profile_name);
        edtPhone = profile.findViewById(R.id.profile_edt_Phone);
        edtGender = profile.findViewById(R.id.profile_edt_gender);
        edtBirthday = profile.findViewById(R.id.profile_edt_birthday);
        edtEmail = profile.findViewById(R.id.profile_edt_email);
        btnChange_Save = profile.findViewById(R.id.profile_bt_edit_save);
        btnLogout_Cancel = profile.findViewById(R.id.profile_bt_logout_cancel);
        btnSelectAvatar = profile.findViewById(R.id.profile_bt_choose_image);
        loadProfile();

        btnLogout_Cancel.setOnClickListener(v -> {
            if (status_edit == false) {
                // Tiến hành logout
                sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } else {
                status_edit = false;
                edtName.setEnabled(false);
                edtPhone.setEnabled(false);
                edtGender.setEnabled(false);
                edtBirthday.setEnabled(false);
                edtEmail.setEnabled(false);
                btnChange_Save.setText("Chỉnh sửa");
                btnLogout_Cancel.setText("Đăng xuất");
                btnLogout_Cancel.setBackgroundColor(Color.parseColor("#3483D3"));
            }
        });

        btnChange_Save.setOnClickListener(v -> {
            if (status_edit == false) {
                status_edit = true;
                edtName.setEnabled(true);
                edtPhone.setEnabled(true);
                edtGender.setEnabled(true);
                edtBirthday.setEnabled(true);
                edtEmail.setEnabled(true);
                btnChange_Save.setText("Lưu");
                btnLogout_Cancel.setText("Hủy");
                btnLogout_Cancel.setBackgroundColor(Color.parseColor("#3483D3"));
            } else {
                status_edit = false;
                edtName.setEnabled(false);
                edtPhone.setEnabled(false);
                edtGender.setEnabled(false);
                edtBirthday.setEnabled(false);
                edtEmail.setEnabled(false);
                btnChange_Save.setText("Chỉnh sửa");
                btnLogout_Cancel.setText("Đăng xuất");
                btnLogout_Cancel.setBackgroundColor(Color.parseColor("#FF0000"));
                saveProfile();
            }
        });

        btnSelectAvatar.setOnClickListener(v -> {
            ImagePicker.Companion.with(this)
                    .crop()                // Tùy chọn cắt ảnh
                    .compress(1024)        // Giới hạn dung lượng
                    .maxResultSize(1080, 1080) // Giới hạn kích thước
                    .start();
        });

        return profile;
    }

    private void loadProfile() {
        // Lấy ra user_id người dùng từ SharedPreferences và hiển thị thông tin
        sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String user_id = sharedPreferences.getString("userId", "");
        Log.d("TAG", "userId: " + user_id);
        // Tải thông tin từ Firestore
        if (!user_id.isEmpty()) {
            db.collection("users").document(user_id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Sử dụng Glide để hiển thị ảnh avatar
                            String avatarUrl = documentSnapshot.getString("avatar_url");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.logo_remove)
                                        .error(R.drawable.logo_remove)
                                        .into(avatar);
                            } else {
                                avatar.setImageResource(R.drawable.logo_remove);
                            }

                            edtName.setText(documentSnapshot.getString("username"));
                            edtPhone.setText(documentSnapshot.getString("phone"));
                            edtGender.setText(documentSnapshot.getString("gender"));
                            Timestamp birth_day = documentSnapshot.getTimestamp("birth_day");
                            // Chuyển đổi Timestamp sang String với định dạng ngày tháng năm
                            if (birth_day != null) {
                                Date birthDayDate = birth_day.toDate();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String birthDayString = dateFormat.format(birthDayDate);
                                edtBirthday.setText(birthDayString);
                            }
                            edtEmail.setText(documentSnapshot.getString("email"));
                            Log.d("TAG", "DocumentSnapshot data: " + documentSnapshot.getData());
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }

    private void saveProfile() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String user_id = sharedPreferences.getString("userId", "");

        if (!user_id.isEmpty()) {
            try {
                String birthdayString = edtBirthday.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date birthDate = dateFormat.parse(birthdayString);

                if (birthDate != null) {
                    com.google.firebase.Timestamp birthTimestamp = new com.google.firebase.Timestamp(birthDate);

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("username", edtName.getText().toString().isEmpty() ? "Người dùng mới" : edtName.getText().toString());
                    updatedData.put("phone", edtPhone.getText().toString().isEmpty() ? "Không có" : edtPhone.getText().toString());
                    updatedData.put("gender", edtGender.getText().toString().isEmpty() ? "Không xác định" : edtGender.getText().toString());
                    updatedData.put("birth_day", birthTimestamp);
                    updatedData.put("email", edtEmail.getText().toString().isEmpty() ? "Không có" : edtEmail.getText().toString());

                    db.collection("users").document(user_id)
                            .update(updatedData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("TAG", "Đã cập nhật thông tin người dùng");
                                Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("TAG", "Lỗi cập nhật thông tin người dùng", e);
                                Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("TAG", "Ngày sinh không hợp lệ");
                    Toast.makeText(getContext(), "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("TAG", "Lỗi khi chuyển đổi ngày sinh", e);
                Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            avatar.setImageURI(fileUri);
            uploadImageToFirebase(fileUri);

            Glide.with(getContext())
                    .load(fileUri)
                    .into(avatar);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Hủy chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri fileUri) {
        if (fileUri != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
            String user_id = sharedPreferences.getString("userId", "");

            if (!user_id.isEmpty()) {
                db.collection("users").document(user_id).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String oldAvatarUrl = documentSnapshot.getString("avatar_url");

                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReference("avatars/" + System.currentTimeMillis() + ".jpg");

                                storageReference.putFile(fileUri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String avatarUrl = uri.toString();

                                                Glide.with(getContext())
                                                        .load(avatarUrl)
                                                        .into(avatar);

                                                saveAvatarUrlToDatabase(avatarUrl);

                                                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                                                    StorageReference oldAvatarRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldAvatarUrl);
                                                    oldAvatarRef.delete()
                                                            .addOnSuccessListener(aVoid -> Log.d("TAG", "Ảnh cũ đã bị xóa"))
                                                            .addOnFailureListener(e -> Log.e("TAG", "Lỗi khi xóa ảnh cũ", e));
                                                }
                                            });

                                            Toast.makeText(getContext(), "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> Log.e("TAG", "Lỗi khi lấy URL ảnh cũ", e));
            } else {
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAvatarUrlToDatabase(String avatarUrl) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
        String user_id = sharedPreferences.getString("userId", "");
        if (!user_id.isEmpty()) {
            db.collection("users").document(user_id)
                    .update("avatar_url", avatarUrl)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TAG", "Đã cập nhật URL avatar");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Lỗi cập nhật URL avatar", e);
                    });
        }
    }
}
