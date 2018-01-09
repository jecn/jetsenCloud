package com.chanlin.jetsencloud.entity;

/**
 * Created by ChanLin on 2018/1/8.
 * jetsenCloud
 * TODO:
 */

public class QuestionPeriodDetail {
    private int course_standard_id;
    private String uuid;
    private String key;
    private String url;

    public int getCourse_standard_id() {
        return course_standard_id;
    }

    public void setCourse_standard_id(int course_standard_id) {
        this.course_standard_id = course_standard_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String title) {
        this.key = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "QuestionPeriodDetail{" +
                "course_standard_id=" + course_standard_id +
                ", uuid='" + uuid + '\'' +
                ", key='" + key + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
