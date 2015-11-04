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
import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.model.MsgAdapter;
import com.software.eric.wechat.model.MsgContent;
import com.software.eric.wechat.service.MsgService;

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

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgBinder = (MsgService.MsgBinder) service;
            msgBinder.connectServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(msgBinder != null){
                msgBinder.close();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(this, MsgService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        //TODO:just for test, remove
        initMsgContents();
        adapter = new MsgAdapter(this, R.layout.msg_item, msgContentList);
        sendButton = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        inputText = (EditText) findViewById(R.id.input_text);
        msgListView.setAdapter(adapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgContent = inputText.getText().toString();
                if (!"".equals(msgContent)) {
                    MsgContent msgContentToShow = new MsgContent(MsgContent.Type.TYPE_SENT, new Date(), msgContent);
                    msgContentList.add(msgContentToShow);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgContentList.size());
                    inputText.setText("");
                    Msg msgPacket = Msg.createMsgPacket("User", "User", msgContent);
                    //TODO:sendMSG
                    msgBinder.send(msgPacket);
                }
            }
        });
        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    sendButton.callOnClick();
                    //TODO: 事件调用顺序有点问题，如果回车键不截断，会触发两次发送事件
                    return true;
                } else
                    return false;
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.software.eric.wechat.NEW_MESSAGE");
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        Intent intent = new Intent(this, MsgService.class);
        startService(intent);
    }

    //TODO:just for test, remove
    private void initMsgContents() {
        MsgContent msgContent1 = new MsgContent(MsgContent.Type.TYPE_RECEIVED,new Date(), "Hello");
        msgContentList.add(msgContent1);
        MsgContent msgContent2 = new MsgContent(MsgContent.Type.TYPE_RECEIVED,new Date(), "Hello");
        msgContentList.add(msgContent2);
        MsgContent msgContent3 = new MsgContent(MsgContent.Type.TYPE_SENT,new Date(), "Hello");
        msgContentList.add(msgContent3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    class LocalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Msg msg = (Msg) intent.getSerializableExtra("msg");
            MsgContent msgContent = new MsgContent(MsgContent.Type.TYPE_RECEIVED, new Date(), msg.getMsg());
            Log.d("receiveMsg",msg.getMsg());
            msgContentList.add(msgContent);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgContentList.size());
        }
    }
}
