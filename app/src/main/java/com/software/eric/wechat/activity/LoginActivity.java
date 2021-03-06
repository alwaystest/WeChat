package com.software.eric.wechat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.software.eric.wechat.R;
import com.software.eric.wechat.util.LogUtil;

public class LoginActivity extends AppCompatActivity {
    EditText editText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText = (EditText) findViewById(R.id.user_name);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(editText.getText().toString())) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("user_name", editText.getText().toString());
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    return true;
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    LogUtil.d("EnterKey Thread",Thread.currentThread().getId()+"");
                    loginButton.callOnClick();
                    return true;
                }
                return false;
            }
        });
    }
}
