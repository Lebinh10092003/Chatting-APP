package vn.edu.tlu.cse470_team8.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.User; // Giả sử bạn có lớp User

public class MemberSearchAdapter extends RecyclerView.Adapter<MemberSearchAdapter.MemberViewHolder> {

    private List<User> userList;
    private List<String> selectedUserIds; // Danh sách userId đã chọn
    private OnItemSelectedListener onItemSelectedListener;

    public MemberSearchAdapter(List<User> userList, List<String> selectedUserIds, OnItemSelectedListener listener) {
        this.userList = userList;
        this.selectedUserIds = selectedUserIds;
        this.onItemSelectedListener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_member_in_group, parent, false);


        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txt_user_name.setText(user.getUsername());
        holder.txt_phone.setText(user.getPhone());

        // Kiểm tra xem user này đã được chọn chưa
        holder.checkBox.setChecked(selectedUserIds.contains(user.getUser_id()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUserIds.add(user.getUser_id());  // Thêm userId vào danh sách
            } else {
                selectedUserIds.remove(user.getUser_id()); // Xóa userId khỏi danh sách
            }
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(selectedUserIds); // Gửi lại danh sách đã chọn
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Lớp ViewHolder chứa các view trong mỗi item
    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView txt_user_name,txt_phone;
        CheckBox checkBox;
        ImageView avatar;

        public MemberViewHolder(View itemView) {
            super(itemView);
            txt_user_name = itemView.findViewById(R.id.txt_name_add_member_in_group);
            avatar = itemView.findViewById(R.id.avatar_add_member_in_group);
            txt_phone = itemView.findViewById(R.id.txt_phone_add_member_in_group);
            checkBox = itemView.findViewById(R.id.cb_add_member_in_group);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(List<String> selectedUserIds);
    }
}
