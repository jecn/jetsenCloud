package com.chanlin.jetsencloud.util;

import com.chanlin.jetsencloud.database.DatabaseService;
import com.chanlin.jetsencloud.entity.Book;
import com.chanlin.jetsencloud.entity.CourseStandardTree;
import com.chanlin.jetsencloud.http.MessageConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChanLin on 2018/1/10.
 * jetsenCloud
 * TODO:
 */

public class JsonSuccessUtil {
    /**
     * 解析教材版本
     * @param courseId
     * @param jstr
     * @return
     * @throws JSONException
     */
    public static ArrayList<Book> getBookList(int courseId, String jstr) throws JSONException {
        ArrayList<Book> books = new ArrayList<>();
        JSONObject dataJson = new JSONObject(jstr);
        JSONArray resultsArrayJson = dataJson.getJSONArray("list");
        for (int a = 0; a < resultsArrayJson.length(); a++) {
            JSONObject resultsJson = resultsArrayJson.getJSONObject(a);
            Book book = new Book();

            book.setCourse_id(courseId);
            book.setId(resultsJson.getInt("id"));
            book.setName(resultsJson.getString("name"));
            books.add(book);
            //入库
            DatabaseService.createBook(courseId,book.getId(),book.getName());
        }
        return books;
    }

    public static  ArrayList<CourseStandardTree> getCourseStandardTree(int bookId,String jstr)throws JSONException {
        ArrayList<CourseStandardTree> courseStandardTreeList = new ArrayList<>();
        JSONObject dataJson = new JSONObject(jstr);
        JSONArray resultsArrayJson = dataJson.getJSONArray("list");
        for (int a = 0; a < resultsArrayJson.length(); a++) {
            JSONObject resultsJson = resultsArrayJson.getJSONObject(a);
            CourseStandardTree courseStandardTree = new CourseStandardTree();
            courseStandardTree.setBook_id(bookId);
            courseStandardTree.setId(resultsJson.getInt("id"));
            courseStandardTree.setDescription(resultsJson.getString("description"));
            courseStandardTree.setChild(resultsJson.getString("child"));
            courseStandardTreeList.add(courseStandardTree);
            //入库
            DatabaseService.createCourseTree(bookId,courseStandardTree.getId(),courseStandardTree.getDescription(),courseStandardTree.getChild());
        }
        return courseStandardTreeList;
    }
}
