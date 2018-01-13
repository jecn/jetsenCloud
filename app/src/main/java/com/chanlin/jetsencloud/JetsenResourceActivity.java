package com.chanlin.jetsencloud;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chanlin.jetsencloud.controller.BookController;
import com.chanlin.jetsencloud.controller.CourseStandardController;
import com.chanlin.jetsencloud.controller.QuestionController;
import com.chanlin.jetsencloud.controller.ResourceController;
import com.chanlin.jetsencloud.database.DatabaseService;
import com.chanlin.jetsencloud.entity.Book;
import com.chanlin.jetsencloud.entity.CourseStandardTree;
import com.chanlin.jetsencloud.entity.QuestionPeriod;
import com.chanlin.jetsencloud.entity.ResourceTree;
import com.chanlin.jetsencloud.expandable.ExpandView;
import com.chanlin.jetsencloud.expandable.ExpandablePresenter;
import com.chanlin.jetsencloud.expandable.FileAdapter;
import com.chanlin.jetsencloud.fragment.MainFragmentPagerAdapter;
import com.chanlin.jetsencloud.fragment.QuestionFragment;
import com.chanlin.jetsencloud.fragment.ResourceFragment;
import com.chanlin.jetsencloud.fragment.UpdateData;
import com.chanlin.jetsencloud.http.MessageConfig;
import com.chanlin.jetsencloud.util.JsonSuccessUtil;
import com.chanlin.jetsencloud.util.LogUtil;
import com.chanlin.jetsencloud.util.ToastUtils;
import com.chanlin.jetsencloud.view.LoadingDialog;
import com.chanlin.jetsencloud.view.LoadingProgressDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JetsenResourceActivity extends FragmentActivity implements ExpandView ,View.OnClickListener{
    private static final String TAG = "JetsenResourceActivity";
    private Context mContext;
    private String courseId;
    private BookController bookController;//获取教材controller
    private CourseStandardController courseStandardController;//获取课标树controller
    private ResourceController resourceController;//获取资源列表的controller
    private QuestionController questionController;

    public static boolean mIsGrant = false;//是否授权
    private FrameLayout fl_no_data,frameLayout_content;
    ArrayList<Book> mybooks =null;//教材列表
    Book thisBook = null;//当前选中的教材项
    CourseStandardTree courseStandardTree = null;//当前选中的课标树
    ArrayList<CourseStandardTree> courseStandardTreeArrayList = new ArrayList<>();//课标树、数据源

    private RecyclerView fileRv;
    private FileAdapter adapter;
    private ExpandablePresenter presenter;


    //右侧布局相关声明
    private LinearLayout mRLResource,mRLQuestion;
    private TextView mTabChatTv,mTabContactsTv;
    private ImageView mTabLineIv;//Tab的那个引导线
    private int screenWidth;//viewpager的宽度
    private ViewPager viewPager;//右侧的viewpager
    private MainFragmentPagerAdapter pagerAdapter;
    int currentIndex=0;

    //定义发送消息的接口
    private UpdateData updateFragment;
    private ResourceFragment resourceFragment;
    private ArrayList<ResourceTree> resourceTreeList = new ArrayList<>();
    private ArrayList<QuestionPeriod> questionPeriodList = new ArrayList<>();
    private QuestionFragment questionFragment;

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
                        presenter.getFiles(-1,thisBook.getId(),0);
                    }catch(JSONException e){
                        mHandler.sendEmptyMessage(MessageConfig.book_http_exception_MESSAGE);
                    }
                    break;
                case MessageConfig.resource_list_standard_http_success_MESSAGE:
                    try{
                        resourceTreeList = JsonSuccessUtil.getResourceTreeList(courseStandardTree.getId(),(String)msg.obj);
                        //刷新列表数据
                        //发送消息给fragment更新数据
                        resourceFragment.updataResourceTree(resourceTreeList);
                    }catch(JSONException e){
                        mHandler.sendEmptyMessage(MessageConfig.book_http_exception_MESSAGE);
                    }
                    break;
                case MessageConfig.question_period_standard_http_success_MESSAGE:
                    try{
                        questionPeriodList = JsonSuccessUtil.getQuestionPeriodList(courseStandardTree.getId(),(String)msg.obj);
                        //刷新列表数据
                        //发送消息给fragment更新数据
                        questionFragment.updataQuestionPeriod(questionPeriodList);
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
        resourceController = new ResourceController(mContext,mHandler);
        questionController = new QuestionController(mContext,mHandler);
        checkWriteExternalStoragePermission();
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


        fileRv = (RecyclerView) findViewById(R.id.fileRv);
        fileRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter();
        presenter = new ExpandablePresenter(this);
        fileRv.setAdapter(adapter);

    //右侧内容
        viewPager=(ViewPager)findViewById(R.id.motion_view_pager);
        viewPager.setOffscreenPageLimit(1);
        ArrayList<Fragment> list = new ArrayList<>();
        resourceFragment = new ResourceFragment();
        questionFragment = new QuestionFragment();
        list.add(resourceFragment);
        list.add(questionFragment);
        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        currentIndex = 0;
        mRLResource = (LinearLayout) findViewById(R.id.id_tab_chat_ll);
        mRLQuestion = (LinearLayout) findViewById(R.id.id_tab_friend_ll);
        mTabChatTv = (TextView) findViewById(R.id.id_chat_tv);
        mTabContactsTv = (TextView) findViewById(R.id.id_friend_tv);

        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);

        initTabLineWidth();
        initListener();
    }
    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {

        screenWidth = viewPager.getWidth();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 3;
        mTabLineIv.setLayoutParams(lp);
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
            thisBook = mybooks.get(1);
            //courseStandardTreeArrayList = DatabaseService.findCourseStandardTreeList(thisBook.getId());

            //列表目录
            presenter.getFiles(-1,thisBook.getId(),0);
        }

    }
    private void refreshData(){
        //LoadingProgressDialog.show(mContext,true,true);
        //fl_no_data.setVisibility(View.VISIBLE);
        //frameLayout_content.setVisibility(View.GONE);
        //如果没获取到则要到网络上获取数据
        bookController.getBookList(mHandler,courseId);


    }


    private void initListener(){
        fileRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                CourseStandardTree entity = (CourseStandardTree) adapter.getItem(position);
                if (entity.isExpand){
                    adapter.collapse(position);
                }else {
                    if (!entity.hasGet) {//是否请求过，请求过的就放缓存里了，不需要重复去拿
                        presenter.getFiles(position, thisBook.getId(), entity.getId());
                    }else {
                        adapter.expand(position);
                    }
                }
                //这里就去访问数据库资源和题库里的内容，如果没有则访问服务器的
                //拿到数据后传递到Fragment里面去
                resourceTreeList = DatabaseService.findResourceTreeList(entity.getId());
                questionPeriodList = DatabaseService.findQuestionPeriodList(entity.getId());
                //发送消息给fragment更新数据
                resourceFragment.updataResourceTree(resourceTreeList);
                questionFragment.updataQuestionPeriod(questionPeriodList);
                //网络获取
                courseStandardTree = entity;
                resourceController.getResourceList(entity.getId());
                questionController.getQuestionPeriodList(entity.getId());
            }
        });

        mRLResource.setOnClickListener(this);
        mRLQuestion.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();

                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                }
                mTabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                //viewPager.setCurrentItem(position);
                currentIndex = position;
                resetTextView();
                updateBtnColor();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    /**
     * 重置颜色
     */
    private void resetTextView() {
        mTabChatTv.setTextColor(Color.BLACK);
        mTabContactsTv.setTextColor(Color.BLACK);
    }
    @Override
    public void fillData(int position, List<CourseStandardTree> list) {
        adapter.setNewData(list);
        if (position > -1){
            adapter.expand(position);
        }
    }


    private void updateBtnColor(){
        switch (currentIndex) {
            case 0:
                mTabChatTv.setTextColor(Color.BLUE);
                break;
            case 1:
                mTabContactsTv.setTextColor(Color.BLUE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_tab_chat_ll:
                viewPager.setCurrentItem(0);
                currentIndex = 0;
                resetTextView();
                updateBtnColor();
                break;
            case R.id.id_tab_friend_ll:
                viewPager.setCurrentItem(1);
                currentIndex = 1;
                resetTextView();
                updateBtnColor();
                break;
        }
    }

        public void checkWriteExternalStoragePermission() {
            int is_granted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (PackageManager.PERMISSION_GRANTED != is_granted) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            } else {
                mIsGrant = true;
            }
    }

        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            requestResult(mContext,requestCode,permissions,grantResults);


    }
    public void requestResult(Object obj, int requestCode, String[] permissions, int[] grantResults){
        List<String> deniedPermissions = new ArrayList<>();
        for(int i=0; i<grantResults.length; i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                deniedPermissions.add(permissions[i]);
            }
        }
        if(deniedPermissions.size() > 0){
            mIsGrant = false;
        } else {
            mIsGrant = true;
        }
    }
}
