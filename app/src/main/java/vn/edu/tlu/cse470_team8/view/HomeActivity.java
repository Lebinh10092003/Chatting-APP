package vn.edu.tlu.cse470_team8.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.tlu.cse470_team8.R;

public class HomeActivity extends AppCompatActivity {
    ImageButton bt_nav_message_icon, bt_nav_group_icon, bt_nav_profile_icon, bt_nav_setting_icon,bt_Logo_topbar, bt_search_topbar, bt_add_topbar;
    EditText edt_search_topbar;
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
        // Lấy trạng thái  từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        selected = sharedPreferences.getString("fragment", "message");
        boolean remember_status = sharedPreferences.getBoolean("remember", false);
        Log.d("MainActivity", "Remember status: " + remember_status);
        // Ánh xạ các phần tử cua bottom bar
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

        // Ánh xạ các phần tử của topbar
        bt_Logo_topbar = findViewById(R.id.imgBt_iconLogo_topnav);
        bt_search_topbar = findViewById(R.id.imgBt_search_topnav);
        bt_add_topbar = findViewById(R.id.img_Bt_addfriend_topnav);
        edt_search_topbar = findViewById(R.id.edt_search_topnav);

        // Bắt sự kiện click vào icon add
        bt_add_topbar.setOnClickListener(v -> {
            // Tạo PopupMenu
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_add, popupMenu.getMenu());

            // Xử lý sự kiện khi chọn một mục trong menu
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_add_friend) {
                    // Xử lý khi chọn "Thêm bạn"
                    // Chuyen den AddFriendActivity
                    Intent intent = new Intent(HomeActivity.this, AddFirendActivity.class);
                    startActivity(intent);
                    return true;
                }
                if (item.getItemId() == R.id.menu_create_group) {
                    // Xử lý khi chọn "Tạo nhóm"
                    // Chuyen den CreatePublicGroupActivity
                    Intent intent = new Intent(HomeActivity.this, CreatePublicGroupActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            // Hiển thị PopupMenu
            popupMenu.show();
        });




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
    protected void onResume() {
        super.onResume();
        // Reload lại dữ liệu tại đây
        changeSelectedState(selected);
        checkSelected(selected);
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
