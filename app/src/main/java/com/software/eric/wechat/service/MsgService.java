package com.software.eric.wechat.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.software.eric.wechat.db.WeChatDB;
import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.util.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MsgService extends Service {

    public static final int TIME_OUT = 1;

    private String serverAddress;
    private int serverPort;

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private WeChatDB weChatDB;
    private LocalBroadcastManager localBroadcastManager;
    private Binder binder = new MsgBinder();
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_OUT:
                    if(socket!=null) {
                        Toast.makeText(MsgService.this, "Time Out", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverAddress = preferences.getString("server_address", "192.168.1.113");
//        serverAddress = preferences.getString("server_address", "10.25.81.97");
        serverPort = preferences.getInt("server_port", 6666);
        weChatDB = WeChatDB.getInstance(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MsgBinder extends Binder {
        public void connectServer() {
                if (socket != null && !socket.isClosed()) {
                    return;
                }
                LogUtil.d("MsgService", "connecting server");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket(InetAddress.getByName(serverAddress), serverPort);
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            while (!socket.isClosed()) {
                                Msg msg = (Msg) objectInputStream.readObject();
                                int msgType = msg.getMsgType();
                                //TODO:save in DB and notify UI
                                switch (msgType) {
                                    case Msg.SEND_MESSAGE:
                                        weChatDB.saveMsg(msg);
                                        Intent i = new Intent("com.software.eric.wechat.NEW_MESSAGE");
                                        i.putExtra("msg", msg);
                                        localBroadcastManager.sendBroadcast(i);
                                        break;
                                    case Msg.ONLINE_USERLIST:
                                        break;
                                    case Msg.USER_LOGIN:
                                        break;
                                    case Msg.USER_LOGOUT:
                                        break;
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            LogUtil.e("ClassNotFoundException", e.getMessage());
                        } catch (IOException e) {
                            LogUtil.e("Receive IOException", e.toString());
                            Message message = new Message();
                            message.what = TIME_OUT;
                            handler.sendMessage(message);
//                            Toast.makeText(MsgService.this, "网络超时", Toast.LENGTH_SHORT);
                        }

                    }
                }).start();
        }

        public void close() {
            LogUtil.d("MsgService", "close ......");
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                    objectInputStream = null;
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                    objectOutputStream = null;
                }
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("Close IOException", e.getMessage());
            }
        }

        public boolean send(Msg msg) {
            boolean flag = false;
            try {
                if (msg != null && objectOutputStream != null) {
                    LogUtil.d("sendMsg",msg.getMsg());
                    objectOutputStream.writeObject(msg);
                    objectOutputStream.flush();
                    flag = true;
                } else {
                    connectServer();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("Send IOException", e.toString());
            }
            return flag;
        }
    }
}
