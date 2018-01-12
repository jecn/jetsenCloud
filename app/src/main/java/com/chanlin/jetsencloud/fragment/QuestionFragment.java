package com.chanlin.jetsencloud.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanlin.jetsencloud.R;
import com.chanlin.jetsencloud.entity.QuestionPeriod;
import com.chanlin.jetsencloud.entity.ResourceTree;

import java.util.ArrayList;

/**
 * Created by ChanLin on 2018/1/11.
 * jetsenCloud
 * TODO:
 */

public class QuestionFragment extends Fragment implements UpdateData{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View resourceView = inflater.inflate(R.layout.fragment_question,container,false);
        return resourceView;
    }

    @Override
    public void updataResourceTree(ArrayList<ResourceTree> resourceTrees) {

    }

    /**
     * 只实现更新问题列表的方法
     * @param questionPeriods
     */
    @Override
    public void updataQuestionPeriod(ArrayList<QuestionPeriod> questionPeriods) {

    }
}
