package com.software.eric.wechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.model.MsgContent;
import com.software.eric.wechat.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mzz on 2015/11/4.
 */
public class WeChatDB {
    public static final String DB_NAME = "WeChat";
    public static final int VERSION = 1;
    private static WeChatDB weChatDB;
    private SQLiteDatabase db;

    private WeChatDB(Context context) {
        WeCharOpenHelper weCharOpenHelper = new WeCharOpenHelper(context, DB_NAME, null, VERSION);
        db = weCharOpenHelper.getWritableDatabase();
    }

    public synchronized static WeChatDB getInstance(Context context) {
        if (weChatDB == null) {
            weChatDB = new WeChatDB(context);
        }
        return weChatDB;
    }

    public void saveMsg(Msg msg) {
        if (msg != null && msg.getMsgType() == Msg.SEND_MESSAGE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("from_id", msg.getFromLoginLogout());
            contentValues.put("time", new SimpleDateFormat("yyyy年M月d日HH:mm").format(new Date()));
            contentValues.put("content", msg.getMsg());
            db.insert("msg", null, contentValues);
        }
    }

    public List<MsgContent> loadMsg(String currentUserId) {
        List<MsgContent> list = new ArrayList<>();
        Cursor cursor = db.query("msg", null, null, null, null, null, "id");
        if (cursor.moveToFirst()) {
            for (boolean flag = true; flag; flag = cursor.moveToNext()) {
                try {
                    MsgContent msgContent = new MsgContent(cursor.getString(cursor.getColumnIndex("from_id"))
                            .equals(currentUserId) ? MsgContent.Type.TYPE_SENT : MsgContent.Type.TYPE_RECEIVED,
                            new SimpleDateFormat("yyyy年M月d日HH:mm").parse(cursor.getString(cursor.getColumnIndex("time"))),
                            cursor.getString(cursor.getColumnIndex("content")));
                    list.add(msgContent);
                } catch (ParseException e) {
                    LogUtil.e("ParseException", e.getMessage());
                }
            }
        }
        return list;
    }
}
