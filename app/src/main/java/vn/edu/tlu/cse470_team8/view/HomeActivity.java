package vn.edu.tlu.cse470_team8.view;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.tlu.cse470_team8.R;

public class HomeActivity extends AppCompatActivity {
    ImageButton bt_nav_message_icon, bt_nav_group_icon, bt_nav_profile_icon, bt_nav_setting_icon;
    TextView tv_nav_message, tv_nav_group, tv_nav_profile, tv_nav_setting;
    LinearLayout ll_nav_message, ll_nav_group, ll_nav_profile, ll_nav_setting;
    String selected = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bt_nav_message_icon = findViewById(R.id.bt_nav_message_icon);
        bt_nav_group_icon = findViewById(R.id.bt_nav_group_icon);
        bt_nav_profile_icon = findViewById(R.id.bt_nav_profile_icon);
        bt_nav_setting_icon = findViewById(R.id.bt_nav_setting_icon);
        tv_nav_message = findViewById(R.id.txt_nav_message);
        tv_nav_group = findViewById(R.id.txt_nav_group);
        tv_nav_profile = findViewById(R.id.txt_nav_profile);
        tv_nav_setting = findViewById(R.id.txt_nav_setting);
        ll_nav_message = findViewById(R.id.frame_nav_message);
        ll_nav_group = findViewById(R.id.frame_nav_group);
        ll_nav_profile = findViewById(R.id.frame_nav_profile);
        ll_nav_setting = findViewById(R.id.frame_nav_setting);


        //Bắt sự kiện click vào icon message
        bt_nav_message_icon.setOnClickListener(v -> {
            selected = "message";
            changeSelectedState(selected);
            checkSelected(selected);
        });
        //Bắt sự kiện click vào icon group
        bt_nav_group_icon.setOnClickListener(v -> {
            selected = "group";
            checkSelected(selected);
            changeSelectedState(selected);
        });
        //Bắt sự kiện click vào icon profile
        bt_nav_profile_icon.setOnClickListener(v -> {
            selected = "profile";
            checkSelected(selected);
            changeSelectedState(selected);
        });

        //Bắt sự kiện click vào icon setting
        bt_nav_setting_icon.setOnClickListener(v -> {
            selected = "setting";
            checkSelected(selected);
            changeSelectedState(selected);
        });
        // Mặc định mở MessageFragment khi Activity khởi tạo
        if (savedInstanceState == null) {
            StartMessageFragment();
        }

    }
    protected  void StartMessageFragment(){
        // Chuyen qua message Fragment
        MessageFragment messageFragment = new MessageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_Frame, messageFragment).commit();


    }
    protected  void StartGroupFragment(){
        // Chuyen qua group Fragment
        GroupFragment groupFragment = new GroupFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_Frame, groupFragment).commit();

    }
    protected  void StartProfileFragment(){
        // Chuyen qua profile Fragment
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_Frame, profileFragment).commit();


    }
    protected  void startSettingFragment(){
        // Chuyen qua setting Fragment
        SettingFragment settingFragment = new SettingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_Frame, settingFragment).commit();

    }
    // Thay đổi trạng thái khi bấm vào item, thay đổi màu sắc của các phần tử
    private void changeSelectedState(String selected) {
        int appColor = ContextCompat.getColor(this, R.color.appcolor);
        int backgroudColor = ContextCompat.getColor(this, R.color.background);
        int whiteColor = ContextCompat.getColor(this, R.color.white);
        int blackColor = ContextCompat.getColor(this, R.color.black);
        // Reset màu của icon, text và background cho tất cả các mục
        bt_nav_message_icon.setColorFilter(blackColor); // Thay đổi màu icon
        tv_nav_message.setTextColor(blackColor); // Thay đổi màu chữ
        ll_nav_message.setBackgroundColor(backgroudColor); // Thay đổi nền LinearLayout
        bt_nav_group_icon.setColorFilter(blackColor);
        tv_nav_group.setTextColor(blackColor);
        ll_nav_group.setBackgroundColor(backgroudColor);
        bt_nav_profile_icon.setColorFilter(blackColor);
        tv_nav_profile.setTextColor(blackColor);
        ll_nav_profile.setBackgroundColor(backgroudColor);
        bt_nav_setting_icon.setColorFilter(blackColor);
        tv_nav_setting.setTextColor(blackColor);
        ll_nav_setting.setBackgroundColor(backgroudColor);


        // Thay đổi màu của icon, text và background cho mục được chọn
        if (selected.equals("message")) {
            bt_nav_message_icon.setColorFilter(whiteColor); // Thay đổi màu icon
            tv_nav_message.setTextColor(whiteColor); // Thay đổi màu chữ
            ll_nav_message.setBackgroundColor(appColor); // Thay đổi nền LinearLayout
        }else if (selected.equals("group")) {
            bt_nav_group_icon.setColorFilter(whiteColor);
            tv_nav_group.setTextColor(whiteColor);
            ll_nav_group.setBackgroundColor(appColor);
        }
        else if (selected.equals("profile")) {
            bt_nav_profile_icon.setColorFilter(whiteColor);
            tv_nav_profile.setTextColor(whiteColor);
            ll_nav_profile.setBackgroundColor(appColor);
        }
        else if (selected.equals("setting")) {
            bt_nav_setting_icon.setColorFilter(whiteColor);
            tv_nav_setting.setTextColor(whiteColor);
            ll_nav_setting.setBackgroundColor(appColor);
        }

    }

    //Hàm kiểm tra và xử lý chuyển đổi fagment
    protected void checkSelected(String selected){
        if(selected.equals("message")){
            StartMessageFragment();
        }
        if(selected.equals("group")){
            StartGroupFragment();
        }
        if(selected.equals("profile")){
            StartProfileFragment();
        }
        if(selected.equals("setting")){
            startSettingFragment();
        }
    }



}
