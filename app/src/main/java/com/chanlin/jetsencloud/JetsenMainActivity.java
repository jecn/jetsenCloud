package com.chanlin.jetsencloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanlin.jetsencloud.adapter.GridViewAdapter;
import com.chanlin.jetsencloud.util.Constant;
import com.chanlin.jetsencloud.util.LogUtil;
import com.chanlin.jetsencloud.util.SystemShare;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class JetsenMainActivity extends AppCompatActivity {
    private static final String TAG = "JetsenMainActivity";
    /**\
     * 测试账号密码：
     *  szcsx     707851
     */
    private Context mContext;
    private String name;
    private String school_name;
    private static String[][] course = new String[][]{{"1","语文"},{"2","数学"},{"3","英语"}};
    /*
    *    {"code":0,"data":{"teacher_id":10040,"name":"深圳向","sex":1,"avatar":"","school_name":"K12开发学校01（高中）",
    *       "class":[{"id":3,"name":"(3)班","grade_name":"高一年级","course":[{"id":1,"name":"语文"}]},
    *                {"id":505,"name":"(34)班","grade_name":"高一年级","course":[{"id":1,"name":"语文"}]}]},"msg":"success"}
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_jetsen_main);
        this.mContext = this;
        Intent userIntent = getIntent();
        Bundle bd = userIntent.getExtras();
        //Constant.k12tokenValue = bd.getString(Constant.k12token);
        //name = bd.getString("name");
        //school_name = bd.getString("school_name");
        school_name = "K12开发学校01（高中）";
        name = "张飞";
        String k12tokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBsaWNhdGlvbiI6InRvYl9wYWQiLCJzY2hvb2xfY29kZSI6ImsxMnNjaG9vbF8wMV9kZXYiLCJ1c2VyX2lkIjoxMTA1MywiZXhwIjoxNTE1OTgyMTUwLCJsb2dpbl90eXBlIjoxfQ.CZSudEzg8ThSxDUIlD33d2-mzaRqz1nvkEtGq6vrV3o";
        String schoolCode = "k12school_01_dev";
        SystemShare.setSettingString(mContext,Constant.k12token,k12tokenValue);
        SystemShare.setSettingString(mContext,Constant.k12code,schoolCode);
        initView();
        initData();
        initListener(this);

    }

    private ImageView user_head_iv;
    private TextView tv_user_name;
    private TextView tv_user_school;
    private GridView gv_course;
    private TextView tv_sendexercise;
    private CourseAdapter courseAdapter;

    private void initView(){
        user_head_iv = (ImageView) findViewById(R.id.user_head_iv);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_school = (TextView) findViewById(R.id.tv_user_school);
        gv_course = (GridView) findViewById(R.id.gv_course);
//        GridViewAdapter gridViewAdapter = new GridViewAdapter(mContext,course);
        courseAdapter = new CourseAdapter(this);
        gv_course.setAdapter(courseAdapter);

        tv_sendexercise = (TextView) findViewById(R.id.tv_sendexercise);
        tv_sendexercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击后跳转 传递值到另外一个页面
                Intent it  = new Intent(JetsenMainActivity.this,JetsenSendExerciseActivity.class);
                it.putExtra("courceId", 1 + "");
                JetsenMainActivity.this.startActivityForResult(it, 1001);
            }
        });
    }

    private void initData(){
        LogUtil.showInfo(TAG,"init data");
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).build();
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515480046265&di=cfb78a8d2e029c15e8ba0e138991587f&imgtype=0&src=http%3A%2F%2Favatar.wshang.com%2F151214%2F1450077753_x.jpg";
        ImageLoader.getInstance().displayImage(url,user_head_iv,options);
    }
    private static void initListener(final JetsenMainActivity jetsenMainActivity){
        jetsenMainActivity.gv_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.showWarn(TAG, course[position][1]);

                //点击后跳转 传递值到另外一个页面
                Intent it  = new Intent(jetsenMainActivity.mContext,JetsenResourceActivity.class);
                it.putExtra("courceId", course[position][0]);
                jetsenMainActivity.startActivity(it);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String string = bundle.getString("aaa");
            Toast.makeText(this, "发送..." + string, Toast.LENGTH_SHORT).show();
        }
    }

    private class CourseAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public CourseAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return course.length;
        }

        @Override
        public Object getItem(int position) {
            return course[position];
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
                convertView = layoutInflater.inflate(R.layout.course_item, null);
                holder.course_name = (TextView) convertView.findViewById(R.id.tv_course_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.course_name.setText(course[position][1].toString());
            return convertView;
        }

        private class ViewHolder {
            private TextView course_name;
        }
    }
}
