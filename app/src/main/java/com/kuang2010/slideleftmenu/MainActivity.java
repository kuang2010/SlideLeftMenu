package com.kuang2010.slideleftmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kuang2010.slidemenuview.SlideMenuView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SlideMenuView mSmv_home;
    private TextView mTv_menu_one;
    private TextView mTv_menu_two;
    private TextView mTv_content_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSmv_home = findViewById(R.id.smv_home);
        findViewById(R.id.iv_open_menu_main).setOnClickListener(this);
        mTv_menu_one = findViewById(R.id.tv_menu_one);
        mTv_menu_two = findViewById(R.id.tv_menu_two);
        mTv_content_main = findViewById(R.id.tv_content_main);
        mTv_menu_one.setOnClickListener(this);
        mTv_menu_two.setOnClickListener(this);
        mTv_content_main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_menu_one:
                mTv_content_main.setText(mTv_menu_one.getText().toString());
                break;
            case R.id.tv_menu_two:
                mTv_content_main.setText(mTv_menu_two.getText().toString());
                break;
        }
        boolean leftMenuIsOpen = mSmv_home.getLeftMenuIsOpen();
        if (leftMenuIsOpen){
            mSmv_home.closeLeftMenu();
        }else {
            mSmv_home.openLeftMenu();
        }
    }
}
