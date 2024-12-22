package vn.edu.tlu.cse470_team8.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import vn.edu.tlu.cse470_team8.R;

public class CreatePublicGroupActivity extends AppCompatActivity {
    private ImageView imgBack;
    private EditText edtGroupName;
    private Button btnCreateGroup;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_public_group);
        imgBack = findViewById(R.id.bt_back_create_public_group);
        imgBack.setOnClickListener(v -> finish());
        // Load mặc định SearchAddMemberFragment
        loadFragment(new MemberWillAddFragment());

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_create_public_group, fragment)
                .commit();
    }
}
