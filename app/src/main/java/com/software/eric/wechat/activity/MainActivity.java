package com.software.eric.wechat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.software.eric.wechat.R;
import com.software.eric.wechat.model.Msg;
import com.software.eric.wechat.model.MsgAdapter;
import com.software.eric.wechat.model.MsgContent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private ListView msgListView;
    private EditText inputText;
    private MsgAdapter adapter;
    private List<MsgContent> msgContentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                if(!"".equals(msgContent)) {
                    MsgContent msgContentToShow = new MsgContent(MsgContent.Type.TYPE_SENT, msgContent);
                    msgContentList.add(msgContentToShow);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgContentList.size());
                    inputText.setText("");
                    Msg msgPacket= Msg.createMsgPacket("User","User",msgContent);
                    //TODO:sendMSG
                }
            }
        });
    }

    //TODO:just for test, remove
    private void initMsgContents() {
        MsgContent msgContent1 = new MsgContent(MsgContent.Type.TYPE_RECEIVED, "Hello");
        msgContentList.add(msgContent1);
        MsgContent msgContent2 = new MsgContent(MsgContent.Type.TYPE_SENT, "Hello");
        msgContentList.add(msgContent2);
    }
}
