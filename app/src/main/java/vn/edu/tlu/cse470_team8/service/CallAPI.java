package vn.edu.tlu.cse470_team8.service;

import android.util.Log;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import okhttp3.*;

import java.io.IOException;

public class CallAPI {

    // Firestore instance
    private static FirebaseFirestore db;

    // Hàm khởi tạo Firestore
    public static void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    // Hàm gọi API Flask
    public static void callFlaskAPI() {
        OkHttpClient client = new OkHttpClient();

        // URL của API Flask, chú ý sử dụng địa chỉ IP chính xác nếu ứng dụng chạy trên thiết bị thật
        String url = "http://10.0.2.2:5000/latest_message";  // URL cho Android Emulator, thay đổi nếu chạy trên thiết bị thật

        // Tạo request cho API
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Gửi request bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CallAPI", "Lỗi khi gọi API: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Lấy phản hồi từ API và log lại
                    String responseBody = response.body().string();
                    Log.d("CallAPI", "API Flask trả về: " + responseBody);

                    // Nếu muốn xử lý dữ liệu trả về, có thể thêm logic xử lý ở đây
                    // Ví dụ: parse JSON hoặc sử dụng dữ liệu trả về
                } else {
                    Log.e("CallAPI", "API Flask trả về lỗi: " + response.code());
                }
            }
        });
    }
}
