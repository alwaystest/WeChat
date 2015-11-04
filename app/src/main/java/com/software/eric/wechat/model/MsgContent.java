package com.software.eric.wechat.model;

import java.util.Date;

/**
 * Created by Mzz on 2015/11/3.
 */
public class MsgContent {

    public enum Type {TYPE_RECEIVED, TYPE_SENT}

    private Date time;
    private String content;
    private Type type;

    public MsgContent(Type type, Date time, String content) {
        this.type = type;
        this.time = time;
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }
}
