package com.software.eric.wechat.model;

import java.io.Serializable;

/**
 * Created by Mzz on 2015/11/3.
 */
public class Msg implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int USER_LOGIN = 1;
    public static final int USER_LOGOUT = 2;
    public static final int USER_RENAME = 3;
    public static final int SEND_MESSAGE = 4;
    public static final int ONLINE_USERLIST = 5;

    private int msgType;
    private String msg;
    private String fromLoginLogout;
    private String to;
    private String[] onlineList;

    private Msg(int msgType,String fromLoginLogout,String to,String[] onlineList,String msg){
        this.msgType = msgType;
        this.fromLoginLogout = fromLoginLogout;
        this.to = to;
        this.onlineList = onlineList;
        this.msg = msg;
    }

    public static Msg createLoginPacket(String login) {
        return new Msg(USER_LOGIN, login, null, null, null);
    }

    public static Msg createLogoutPacket(String logout) {
        return new Msg(USER_LOGOUT, logout, null, null, null);
    }

    public static Msg createMsgPacket(String from, String to, String msg) {
        return new Msg(SEND_MESSAGE, from, to, null, null);
    }

    public static Msg createOnlineListPacket(String[] onlineList) {
        return new Msg(ONLINE_USERLIST, null, null, onlineList, null);
    }

    public int getMsgType() {
        return msgType;
    }

    public String getMsg() {
        return msg;
    }

    public String getFromLoginLogout() {
        return fromLoginLogout;
    }

    public String getTo() {
        return to;
    }

    public String[] getOnlineList() {
        return onlineList;
    }
}
