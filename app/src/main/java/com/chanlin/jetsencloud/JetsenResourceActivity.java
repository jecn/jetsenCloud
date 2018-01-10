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

import com.chanlin.jetsencloud.controller.BookController;
import com.chanlin.jetsencloud.controller.CourseStandardController;
import com.chanlin.jetsencloud.database.DatabaseService;
import com.chanlin.jetsencloud.entity.Book;
import com.chanlin.jetsencloud.entity.CourseStandardTree;
import com.chanlin.jetsencloud.http.MessageConfig;
import com.chanlin.jetsencloud.util.JsonSuccessUtil;
import com.chanlin.jetsencloud.util.LogUtil;
import com.chanlin.jetsencloud.util.ToastUtils;
import com.chanlin.jetsencloud.view.LoadingDialog;
import com.chanlin.jetsencloud.view.LoadingProgressDialog;

import org.json.JSONException;

import java.util.ArrayList;

public class JetsenResourceActivity extends FragmentActivity {
    private static final String TAG = "JetsenResourceActivity";
    private Context mContext;
    private String courseId;
    private BookController bookController;//获取教材controller
    private CourseStandardController courseStandardController;//获取课标树controller
    private FrameLayout fl_no_data,frameLayout_content;
    ArrayList<Book> mybooks =null;//教材列表
    Book thisBook = null;//当前选中的教材项
    ArrayList<CourseStandardTree> courseStandardTreeArrayList = new ArrayList<>();//课标树、数据源
    private ViewPager viewPager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageConfig.book_http_success_MESSAGE:
                    LogUtil.showInfo("Result", "book_http_success_MESSAGE result:" + (String) msg.obj);
                    try {
                        //解析json
                        mybooks = JsonSuccessUtil.getBookList(Integer.parseInt(courseId), (String)msg.obj);
                        if (mybooks != null && mybooks.size() > 0){
                            thisBook = mybooks.get(0);//默认选中第一条
                            //刷新数据
                            initData();
                            courseStandardController.getCourseStandardList(thisBook.getId());
                        }
                    }catch (JSONException e){
                        mHandler.sendEmptyMessage(MessageConfig.book_http_exception_MESSAGE);
                        e.printStackTrace();
                    }
                    break;
                case MessageConfig.book_http_false_MESSAGE:
                    ToastUtils.shortToast(mContext, getResources().getString(R.string.http_exception));
                    break;
                case MessageConfig.course_standard_http_success_MESSAGE:
                    LogUtil.showInfo("Result", "course_standard_http_success_MESSAGE result:" + (String) msg.obj);
                    try{
                        courseStandardTreeArrayList = JsonSuccessUtil.getCourseStandardTree(thisBook.getId(),(String)msg.obj);
                        //刷新列表数据
                    }catch(JSONException e){
                        mHandler.sendEmptyMessage(MessageConfig.book_http_exception_MESSAGE);
                    }
                    break;
                }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetsen_resource);
        mContext = this;
        bookController = new BookController(mContext,mHandler);
        courseStandardController = new CourseStandardController(mContext,mHandler);
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
        courseId = it.getStringExtra("courceId");
        courseId = courseId == null ? "0" : courseId;
        //数据库中获取数据
        mybooks = DatabaseService.findBookList(Integer.parseInt(courseId));
        if(mybooks != null && mybooks.size() > 0){
            fl_no_data.setVisibility(View.GONE);
            frameLayout_content.setVisibility(View.VISIBLE);
            thisBook = mybooks.get(0);
            courseStandardTreeArrayList = DatabaseService.findCourseStandardTreeList(thisBook.getCourse_id());
        }
    }
    private void refreshData(){
        //LoadingProgressDialog.show(mContext,true,true);
        //fl_no_data.setVisibility(View.VISIBLE);
        //frameLayout_content.setVisibility(View.GONE);
        //如果没获取到则要到网络上获取数据
        bookController.getBookList(mHandler,courseId);


    }
}
