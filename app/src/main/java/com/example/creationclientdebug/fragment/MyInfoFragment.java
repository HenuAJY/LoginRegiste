package com.example.creationclientdebug.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginregiste.R;
import com.example.loginregiste.activity_login;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInfoFragment extends Fragment {

    private static MyInfoFragment instance;
    public static MyInfoFragment getInstance(){
        if(instance==null){
            instance = new MyInfoFragment();
        }
        return instance;
    }
    public MyInfoFragment() {
        // Required empty public constructor
    }

    private Button bLogOut;

    private MyBtnListener listener = new MyBtnListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);
        // Inflate the layout for this fragment
        bLogOut = view.findViewById(R.id.btn_log_out);
        bLogOut.setOnClickListener(listener);
        return view;
    }

    class MyBtnListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_log_out:
                    activity_login.startActivity(getContext());
                    getActivity().finish();
                    break;
                case R.id.btn_view_my_info:
                    break;
            }
        }
    }

}
