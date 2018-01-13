package com.chanlin.jetsencloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chanlin.jetsencloud.controller.BookController;
import com.chanlin.jetsencloud.controller.CourseStandardController;
import com.chanlin.jetsencloud.controller.ResourceController;
import com.chanlin.jetsencloud.database.DatabaseService;
import com.chanlin.jetsencloud.entity.Book;
import com.chanlin.jetsencloud.entity.CourseStandardTree;
import com.chanlin.jetsencloud.entity.QuestionPeriod;
import com.chanlin.jetsencloud.entity.ResourceTree;
import com.chanlin.jetsencloud.expandable.ExpandView;
import com.chanlin.jetsencloud.expandable.ExpandablePresenter;
import com.chanlin.jetsencloud.expandable.FileAdapter;
import com.chanlin.jetsencloud.fragment.QuestionFragment;
import com.chanlin.jetsencloud.fragment.ResourceFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/12.
 */
public class JetsenSendExerciseActivity extends FragmentActivity implements ExpandView, View.OnClickListener {

    private TextView send_exercise;
    private RelativeLayout mRLBack;

    private Context mContext;
    private String courseId;
   // private BookController bookController;//获取教材controller
    //private CourseStandardController courseStandardController;//获取课标树controller
    //private ResourceController resourceController;//获取资源列表的controller

    private ListView exercise_listview;
    private int period_click_item = -1; // 点击item位置
    private ArrayList<String> list = new ArrayList<String>();
   // ArrayList periodlist = new ArrayList();

    private FrameLayout fl_no_data, frameLayout_content;
    ArrayList<Book> mybooks = null;//教材列表
    Book thisBook = null;//当前选中的教材项
    CourseStandardTree courseStandardTree = null;//当前选中的课标树
    ArrayList<CourseStandardTree> courseStandardTreeArrayList = new ArrayList<>();//课标树、数据源

    private RecyclerView fileRv;
    private FileAdapter adapter;
    private ExpandablePresenter presenter;

    //popupWindow弹出框
    private RelativeLayout relative_booklist;
    private TextView text_book_name;
    private ListView mlistview;
    private View view;
    private BooklistViewAdapter booklistViewAdapter;
    private PopupWindow popupWindow;
    private int popX; // 横坐标

    private int course_id;
    private int book_id;
    private String book_name;

    private ExerciseListViewAdapter exerciseListViewAdapter;

    //定义发送消息的接口
    //private UpdateData updateFragment;
    //private ResourceFragment resourceFragment;
    //private ArrayList<ResourceTree> resourceTreeList = new ArrayList<>();
    private ArrayList<QuestionPeriod> questionPeriodList = new ArrayList<>();
    //private QuestionFragment questionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetsen_sendexercise);

        initView();
        initData();
        setlistview();
        initPop();
    }

    private void initView() {
        mRLBack = (RelativeLayout) findViewById(R.id.title_back);
        mRLBack.setOnClickListener(this);

        fl_no_data = (FrameLayout) findViewById(R.id.fl_no_data);
        frameLayout_content = (FrameLayout) findViewById(R.id.frameLayout_content);

        fileRv = (RecyclerView) findViewById(R.id.fileRv);
        fileRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter();
        presenter = new ExpandablePresenter(this);
        fileRv.setAdapter(adapter);

        relative_booklist = (RelativeLayout) findViewById(R.id.relative_booklist);
        text_book_name = (TextView) findViewById(R.id.tv_book_name);
        text_book_name.setOnClickListener(this);

        initListener();
    }

    private void initListener() {
        fileRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                CourseStandardTree entity = (CourseStandardTree) adapter.getItem(position);
                if (entity.isExpand) {
                    adapter.collapse(position);
                } else {
                    if (!entity.hasGet) {//是否请求过，请求过的就放缓存里了，不需要重复去拿
                        presenter.getFiles(position, thisBook.getId(), entity.getId());
                    } else {
                        adapter.expand(position);
                    }
                }
                //这里就去访问数据库资源和题库里的内容，如果没有则访问服务器的
                //拿到数据后传递到Fragment里面去
                //resourceTreeList = DatabaseService.findResourceTreeList(entity.getId());
                questionPeriodList = DatabaseService.findQuestionPeriodList(entity.getId());
                list.clear();
                for (int i = 0; i< questionPeriodList.size(); i++){
//                    QuestionPeriod period = new QuestionPeriod();
//                    period.setTitle("课时1-" + i);
//                    questionPeriodList.add(period);
                    list.add("0");
                }
                //发送消息给fragment更新数据
                //resourceFragment.updataResourceTree(resourceTreeList);
                //questionFragment.updataQuestionPeriod(questionPeriodList);
                //网络获取
                courseStandardTree = entity;
                //resourceController.getResourceList(entity.getId());
                // questioncon

                //刷新 listview
                exerciseListViewAdapter.notifyDataSetChanged();

            }
        });
    }

    private void initData() {
        Intent it = getIntent();
        courseId = it.getStringExtra("courceId");
        courseId = courseId == null ? "0" : courseId;
        //数据库中获取数据
        mybooks = DatabaseService.findBookList(Integer.parseInt(courseId));
        if (mybooks != null && mybooks.size() > 0) {
            fl_no_data.setVisibility(View.GONE);
            frameLayout_content.setVisibility(View.VISIBLE);
            thisBook = mybooks.get(1);
            // courseStandardTreeArrayList = DatabaseService.findCourseStandardTreeList(thisBook.getId());

            //列表目录
            presenter.getFiles(-1, thisBook.getId(), 0);
        }

    }

    private void setlistview() {
        //右侧内容
        send_exercise = (TextView) findViewById(R.id.sendexercise);
        send_exercise.setOnClickListener(this);
        exercise_listview = (ListView) findViewById(R.id.exe_listview);
        exerciseListViewAdapter = new ExerciseListViewAdapter(this);
        exercise_listview.setAdapter(exerciseListViewAdapter);
        exercise_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                period_click_item = position;
                if (list.get(position).equals("0")) {
                    list.set(position, "1");
                } else {
                    list.set(position, "0");
                }

                exerciseListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initPop() {
        view = LayoutInflater.from(this).inflate(R.layout.booklistview, null);
        mlistview = (ListView) view.findViewById(R.id.book_listview);
        booklistViewAdapter = new BooklistViewAdapter(this);
        mlistview.setAdapter(booklistViewAdapter);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                course_id = mybooks.get(position).getCourse_id();
                book_id = mybooks.get(position).getId();
                book_name = mybooks.get(position).getName();
                text_book_name.setText(book_name);
                Log.i("onActivityResult", " course_id=" + course_id + " book_id=" + book_id + " book_name" + book_name);
            }
        });
        popupWindow = new PopupWindow(view, getScreenWidth(this) / 4, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable()); // 解决PopupWindow 设置setOutsideTouchable无效
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popX = getScreenWidth(this) / 8;
    }

    @Override
    public void fillData(int position, List<CourseStandardTree> list) {
        adapter.setNewData(list);
        if (position > -1) {
            adapter.expand(position);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.sendexercise:
                String s = "";
                String str = "";
                for (int i = 0;i < list.size(); i++){
                    if (list.get(i).equals("1")){
                        s = questionPeriodList.get(i).getTitle();
                        str = str + " ," + s;
                    }
                }

                Intent intent = getIntent();
                setResult(Activity.RESULT_OK, intent);//返回页面1
                Bundle bundle = intent.getExtras();
                bundle.putString("aaa", str);//添加要返回给页面1的数据
                intent.putExtras(bundle);
                finish();
                break;
            case R.id.tv_book_name: // popupWindow弹框
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(relative_booklist, popX, 5);
//                    popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
        }
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private class ExerciseListViewAdapter extends BaseAdapter{

        private LayoutInflater layoutInflater;

        public ExerciseListViewAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return questionPeriodList.size();
        }

        @Override
        public Object getItem(int position) {
            return questionPeriodList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.exercise_list_item, null);
                holder.period_name = (TextView) convertView.findViewById(R.id.period_name);
                holder.imgcheck = (ImageView) convertView.findViewById(R.id.imgbtn_check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (list.get(position).equals("1")){
                holder.imgcheck.setImageResource(R.mipmap.period_check_on);
            } else {
                holder.imgcheck.setImageResource(R.mipmap.period_check_off);
            }
            holder.period_name.setText(questionPeriodList.get(position).getTitle());

            return convertView;
        }

        private class ViewHolder {
            private TextView period_name;
            private ImageView imgcheck;
        }
    }

    private class BooklistViewAdapter extends BaseAdapter{

        private LayoutInflater layoutInflater;

        public BooklistViewAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mybooks.size();
        }

        @Override
        public Object getItem(int position) {
            return mybooks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.booklist_item, null);
                holder.book_name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.book_name.setText(mybooks.get(position).getName());

            return convertView;
        }

        private class ViewHolder {
            private TextView book_name;
        }
    }
}
