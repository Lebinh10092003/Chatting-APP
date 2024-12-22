package vn.edu.tlu.cse470_team8.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.tlu.cse470_team8.R;
import vn.edu.tlu.cse470_team8.model.User;

public class MemberWillAddAdapter extends RecyclerView.Adapter<MemberWillAddAdapter.MemberViewHolder> {

    private List<User> selectedUsers; // Danh sách đối tượng User đã chọn

    // Khởi tạo adapter với danh sách người dùng
    public MemberWillAddAdapter(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_will_add_in_group, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = selectedUsers.get(position);
        holder.txtName.setText(user.getUsername()); // Hiển thị Name
        holder.txtPhone.setText(user.getPhone()); // Hiển thị số điện thoại

        // Hiển thị avatar (nếu có URL)
        if (user.getAvatar_url() != null) {
            holder.avatar_will_add_member.setImageResource(R.drawable.logo_remove);
        } else {
            holder.avatar_will_add_member.setImageResource(R.drawable.logo_remove);
        }

        // Thêm sự kiện cho nút xóa thành viên
        holder.btnRemoveMember.setOnClickListener(v -> {
            selectedUsers.remove(position);
            notifyItemRemoved(position);
            // Cập nhật lại vị trí của các phần tử sau khi xóa
            notifyItemRangeChanged(position, selectedUsers.size());

        });
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPhone;
        ImageView avatar_will_add_member;
        ImageButton btnRemoveMember;

        public MemberViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_member_will_add_in_group);
            txtPhone = itemView.findViewById(R.id.txt_phone_member_will_add_in_group);
            avatar_will_add_member = itemView.findViewById(R.id.avatar_member_will_add_in_group);
            btnRemoveMember = itemView.findViewById(R.id.imgBt_remove_member_will_add_in_group);
        }
    }
}
