package com.software.eric.wechat.model;

/**
 * Created by Mzz on 2015/11/3.
 */
public class MsgContent {

    public enum Type {TYPE_RECEIVED, TYPE_SENT}

    private String content;
    private Type type;

    public MsgContent(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }
}
