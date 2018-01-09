package com.chanlin.jetsencloud;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanlin.jetsencloud.database.DatabaseService;
import com.chanlin.jetsencloud.entity.Book;
import com.chanlin.jetsencloud.entity.CourseStandardTree;
import com.chanlin.jetsencloud.view.LoadingDialog;
import com.chanlin.jetsencloud.view.LoadingProgressDialog;

import java.util.ArrayList;

public class JetsenResourceActivity extends FragmentActivity {
    private static final String TAG = "JetsenResourceActivity";
    private Context mContext;
    private String courceId;
    private JetsenController controller;
    private FrameLayout fl_no_data,frameLayout_content;
    ArrayList<Book> mybook =null;//教材列表
    Book thisBook = null;//当前选中的教材项
    ArrayList<CourseStandardTree> courseStandardTreeArrayList = new ArrayList<>();//课标树、数据源
    private ViewPager viewPager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetsen_resource);
        mContext = this;
        controller = new JetsenController(mContext,mHandler);

        initView();
        initData();
        refreshData();
    }
    private void initView(){
        TextView mTvTitle = (TextView) findViewById(R.id.title_text);
        RelativeLayout mRLBack=(RelativeLayout)findViewById(R.id.title_back);
        mRLBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fl_no_data = (FrameLayout) findViewById(R.id.fl_no_data);
        frameLayout_content = (FrameLayout) findViewById(R.id.frameLayout_content);
        viewPager=(ViewPager)findViewById(R.id.motion_view_pager);
    }
    private void initData(){
        Intent it  = getIntent();
        courceId = it.getStringExtra("courceId");
        courceId = courceId == null ? "0" : courceId;
        //数据库中获取数据
        mybook = DatabaseService.findBookList(Integer.parseInt(courceId));
        if(mybook != null && mybook.size() > 0){
            fl_no_data.setVisibility(View.GONE);
            frameLayout_content.setVisibility(View.VISIBLE);
            thisBook = mybook.get(0);
            courseStandardTreeArrayList = DatabaseService.findCourseStandardTreeList(thisBook.getCourse_id());

        }else {
            //fl_no_data.setVisibility(View.VISIBLE);
            //frameLayout_content.setVisibility(View.GONE);
            //如果没获取到则要到网络上获取数据
            controller.courceList(courceId);
        }
    }
    private void refreshData(){
        //LoadingProgressDialog.show(mContext,true,true);
    }
}
