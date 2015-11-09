package com.software.eric.wechat.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.software.eric.wechat.R;
import com.software.eric.wechat.db.WeChatDB;
import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.model.MsgAdapter;
import com.software.eric.wechat.model.MsgContent;
import com.software.eric.wechat.service.MsgService;
import com.software.eric.wechat.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private ListView msgListView;
    private EditText inputText;
    private MsgAdapter adapter;
    private List<MsgContent> msgContentList = new ArrayList<>();

    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private MsgService.MsgBinder msgBinder;

    private String userName;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgBinder = (MsgService.MsgBinder) service;
            msgBinder.connectServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //only be called at service stop accident
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = getIntent().getStringExtra("user_name");
        LogUtil.d("login", userName);
        LogUtil.d("MainClick Thread",Thread.currentThread().getId()+"");
        Intent serviceIntent = new Intent(this, MsgService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        adapter = new MsgAdapter(this, R.layout.msg_item, msgContentList);
        sendButton = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        inputText = (EditText) findViewById(R.id.input_text);
        msgListView.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("Click Thread",Thread.currentThread().getId()+"");
                String msgContent = inputText.getText().toString();
                if (!"".equals(msgContent)) {
                    MsgContent msgContentToShow = new MsgContent(MsgContent.Type.TYPE_SENT, new Date(), msgContent);
                    Msg msgPacket = Msg.createMsgPacket(userName, null, msgContent);
                    if (msgBinder.send(msgPacket)) {
                        saveMsg(msgPacket);
                        msgContentList.add(msgContentToShow);
                        adapter.notifyDataSetChanged();
                        msgListView.setSelection(msgContentList.size());
                        inputText.setText("");
                    }
                }
            }
        });
        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    return true;
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    LogUtil.d("EnterKey Thread",Thread.currentThread().getId()+"");
                    sendButton.callOnClick();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (intentFilter == null) {
            intentFilter = new IntentFilter();
            intentFilter.addAction("com.software.eric.wechat.NEW_MESSAGE");
        }
        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        if (localBroadcastManager == null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
        }
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        initMsgContents();
    }

    private void saveMsg(final Msg msgPacket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("saveMsg Thread",Thread.currentThread().getId()+"");
                WeChatDB weChatDB = WeChatDB.getInstance(MainActivity.this);
                weChatDB.saveMsg(msgPacket);
            }
        }).start();
    }

    private void initMsgContents() {
        msgContentList.clear();
        WeChatDB weChatDB = WeChatDB.getInstance(MainActivity.this);
        List<MsgContent> list = weChatDB.loadMsg(userName);
        for (MsgContent msgContent : list) {
            msgContentList.add(msgContent);
        }
        adapter.notifyDataSetChanged();
        msgListView.setSelection(msgContentList.size()-1);
        LogUtil.d("Size", msgContentList.size()+"");
        LogUtil.d("Current Selected", msgListView.getSelectedItemPosition() + "");


//        MsgContent msgContent1 = new MsgContent(MsgContent.Type.TYPE_RECEIVED, new Date(), "Hello");
//        msgContentList.add(msgContent1);
//        MsgContent msgContent2 = new MsgContent(MsgContent.Type.TYPE_RECEIVED, new Date(), "Hello");
//        msgContentList.add(msgContent2);
//        MsgContent msgContent3 = new MsgContent(MsgContent.Type.TYPE_SENT, new Date(), "Hello");
//        msgContentList.add(msgContent3);
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d("WeChat", "onDestroy");
        super.onDestroy();
        if (msgBinder != null) {
            LogUtil.d("service", "binder.close()");
            msgBinder.close();
        }
        unbindService(serviceConnection);
        //TODO:if need stopService?
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                msgListView.setSelection(msgContentList.size());
            }
        });
    }

    class LocalReceiver extends BroadcastReceiver {
        //This method is always called within the main thread of its process, unless you explicitly asked for it to be scheduled on a different thread
        @Override
        public void onReceive(Context context, Intent intent) {
            Msg msg = (Msg) intent.getSerializableExtra("msg");
            MsgContent msgContent = new MsgContent(MsgContent.Type.TYPE_RECEIVED, new Date(), msg.getMsg());
            Log.d("receiveMsg", msg.getMsg());
            msgContentList.add(msgContent);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgContentList.size());
        }
    }
}
