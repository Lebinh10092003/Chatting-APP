from flask import Flask, request, jsonify  
import firebase_admin  
from firebase_admin import credentials, firestore  
from pyvi import ViTokenizer  
import logging  
import time

# Khởi tạo Firebase Admin SDK  
cred = credentials.Certificate("py_google-services.json")  
firebase_admin.initialize_app(cred)  

# Tạo kết nối Firestore  
db = firestore.client()  

# Hàm tách từ trong tin nhắn  
def extract_words(message):  
    return ViTokenizer.tokenize(message).split()  

# Hàm cập nhật hoặc thêm mới tài liệu vào Firestore  
def update_or_add_q_value(state, action, reward, next_state):
    try:
        # Tạo ID duy nhất cho tài liệu
        doc_id = f"{state}_{action}"
        doc_ref = db.collection('q_table_learning').document(doc_id)
        doc = doc_ref.get()

        # Hệ số học và chiết khấu
        alpha = 0.5  # Hệ số học
        gamma = 0.95  # Chiết khấu


        if doc.exists:
            # Nếu tài liệu tồn tại, cập nhật Q-value
            existing_data = doc.to_dict()
            current_q_value = existing_data['q_value']

            # Tìm Q-value tối đa cho trạng thái tiếp theo (next_state)
            next_q_values = db.collection('q_table_learning').where('state', '==', next_state).stream()
            max_next_q_value = max([doc.to_dict()['q_value'] for doc in next_q_values], default=1)

            # Cập nhật Q-value theo công thức Q-Learning
            new_q_value = current_q_value + alpha * (reward + gamma * max_next_q_value - current_q_value)

            # Cập nhật tài liệu
            doc_ref.update({
                'q_value': new_q_value,
                'timestamp': firestore.SERVER_TIMESTAMP
            })
            logging.info(f"Cập nhật Q-value cho state='{state}', action='{action}' thành {new_q_value}")
        else:
            # Nếu tài liệu không tồn tại, thêm mới với giá trị Q ban đầu
            new_entry = {
                'state': state,
                'action': action,
                'q_value': reward,
                'timestamp': firestore.SERVER_TIMESTAMP
            }

            doc_ref.set(new_entry)  # Sử dụng set để lưu với ID cụ thể
            logging.info(f"Thêm mới state='{state}', action='{action}' với Q-value={reward}")
    except Exception as e:
        logging.error(f"Lỗi khi cập nhật hoặc thêm mới tài liệu: {e}")

# Hàm phân tích và xử lý tin nhắn  
def analyze_and_store_message(message):  
    try:  
        # Kiểm tra nội dung tin nhắn
        if not message.get('content'):
            logging.warning("Tin nhắn không có nội dung!")
            return

        # Phân tích từ ngữ trong tin nhắn  
        words = extract_words(message['content'])  
        logging.info(f"Tách từ từ tin nhắn: {words}")

        # Lưu các cặp trạng thái và hành động  
        for i in range(len(words) - 1):  
            state = words[i]  # Trạng thái: từ hiện tại  
            action = words[i + 1]  # Hành động: từ tiếp theo  g
            next_state = words[i + 1] if i + 1 < len(words) else None  # Trạng thái tiếp theo
            reward = 1  # Giá trị thưởng cơ bản  

            # Cập nhật hoặc thêm mới tài liệu  
            update_or_add_q_value(state, action, reward, next_state)

    except Exception as e:  
        logging.error(f"Lỗi khi xử lý tin nhắn: {e}")  

# Khởi tạo Flask ứng dụng  
app = Flask(__name__)  

@app.route('/latest_message', methods=['GET'])
def latest_message():
    try:
        time.sleep(0.4)
        # Truy vấn Firestore để lấy tin nhắn mới nhất
        messages_ref = db.collection('messages')
        query = messages_ref.order_by('timestamp', direction=firestore.Query.DESCENDING).limit(1)
        messages = query.stream()

        # Chuyển stream thành danh sách và kiểm tra có tin nhắn mới không
        messages_list = list(messages)
        if len(messages_list) == 0:
            return jsonify({"status": "error", "message": "Không có tin nhắn mới"}), 404

        # Xử lý tin nhắn và trả về kết quả
        message_data = messages_list[0].to_dict()  # Lấy tin nhắn đầu tiên
        analyze_and_store_message(message_data)
        return jsonify({"status": "success", "message": message_data})

    except Exception as e:
        logging.error(f"Lỗi khi lấy tin nhắn mới nhất: {e}")
        return jsonify({"status": "error", "message": "Lỗi khi lấy tin nhắn mới nhất"}), 500

if __name__ == "__main__":  
    # Cấu hình logging  
    logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s: %(message)s', datefmt='%Y-%m-%d %H:%M:%S')  

    # Chạy Flask ứng dụng  
    app.run(host='0.0.0.0', port=5000)
