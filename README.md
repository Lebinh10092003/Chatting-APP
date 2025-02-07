## 📌 Nhánh GitHub Riêng - Tách Phần Công Việc Cá Nhân

## 📢 Giới thiệu
Xin chào anh/chị,  
Đây là nhánh GitHub em tạo riêng để tách phần em làm từ bài nhóm. Nhánh này giúp anh/chị dễ dàng theo dõi tiến độ và đánh giá phần công việc của em một cách rõ ràng.

## 🛠 Tổng quan về ứng dụng
Ứng dụng nhắn tin với các công nghệ chính:  
- **📱 Ngôn ngữ lập trình**: Java  
- **🔥 Cơ sở dữ liệu**: Firebase Firestore (lưu trữ và cập nhật dữ liệu theo thời gian thực)  
- **🤖 Trí tuệ nhân tạo**: Sử dụng thuật toán **Q-learning** để gợi ý từ cho người dùng

## 📂 Cấu trúc dự án
- Ứng dụng được xây dựng theo mô hình MVC
## 🚀 Hướng dẫn cài đặt
- Ứng dụng gồm 2 phần chính :
    + Ứng dụng nhắn tin được viết bằng Java
    + API xử lý tin nhắn được viết bằng Python
- Cách hoạt động :  Khi sever được viết bằng **Python** chạy , mỗi khi tin nhắn được gửi đi trên ứng dụng nó sẽ lấy tin nhắn mới nhất chưa được xử lý từ **Firebase** và xử  lý dựa trên thuật toán **Q-learning** sau đó lưu dữ liệu đã được xử lý vào **Firebase** , sau đó khi người dùng nhập tin nhắn trên ứng dụng, ứng dụng sẽ lấy dữ liệu dựa trên **state** hiện tại mà người dùng nhập và **esilon** random được để đưa ra gợi ý!
**Em cảm ơn anh chị đã dành thời gian đọc bài của em ạ!** 
  
  
