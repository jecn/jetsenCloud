package com.chanlin.jetsencloud.entity;

/**
 * Created by ChanLin on 2018/1/8.
 * jetsenCloud
 * TODO:
 */

public class CourseStandardTree {
    private int book_id;
    private int id;
    private String name;
    private String child;

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "CourseStandardTree{" +
                "book_id=" + book_id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", child='" + child + '\'' +
                '}';
    }
}
