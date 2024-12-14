package vn.edu.tlu.cse470_team8.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import vn.edu.tlu.cse470_team8.R;

public class GroupFragment extends Fragment {

    public GroupFragment() {
        // Required empty public constructor
    }

        public static GroupFragment newInstance () {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

}
