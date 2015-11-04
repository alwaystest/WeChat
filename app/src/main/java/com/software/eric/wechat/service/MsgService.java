package com.software.eric.wechat.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.software.eric.wechat.db.WeChatDB;
import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.util.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MsgService extends Service {

    String serverAddress;
    int serverPort;

    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    WeChatDB weChatDB;
    LocalBroadcastManager localBroadcastManager;
    Binder binder = new MsgBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverAddress = preferences.getString("server_address", "192.168.1.100");
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
                            while (true) {
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
                            //TODO:handle lost connection
                        } catch (ClassNotFoundException e) {
                            LogUtil.e("ClassNotFoundException", e.getMessage());
                        } catch (IOException e) {
                            LogUtil.e("IOException", e.toString());
                        }

                    }
                }).start();
        }

        public void close() {
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
                LogUtil.e("IOException", e.getMessage());
            }
        }

        public void send(Msg msg) {
            try {
                if (msg != null && objectOutputStream != null) {
                    LogUtil.d("sendMsg",msg.getMsg());
                    objectOutputStream.writeObject(msg);
                    objectOutputStream.flush();
                } else {
                    //TODO: make UI know message send failed .to confirm
                    throw new IOException();
                }
            } catch (IOException e) {
                LogUtil.e("IOException", e.getMessage());
            }
        }
    }
}
