package com.alex.com.alex.livertmppushsdk.demo;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

public class StartActivity extends Activity {


    public static final String RMTP_URL="rtmp://192.168.0.115:1935/live/12345";

    public static final String RTMPURL_MESSAGE = "com.alex.com.alex.livertmppushsdk.demo.rtmpurl";

    private Button _startRtmpPushButton = null;
    private EditText _rtmpUrlEditText = null;

    private View.OnClickListener _startRtmpPushOnClickedEvent = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            String rtmpUrl = _rtmpUrlEditText.getText().toString();

            i.putExtra(StartActivity.RTMPURL_MESSAGE, rtmpUrl);
            StartActivity.this.startActivity(i);
        }
    };

    private void InitUI(){
        _rtmpUrlEditText = (EditText)findViewById(R.id.rtmpUrleditText);
        _startRtmpPushButton = (Button)findViewById(R.id.startRtmpButton);

        _rtmpUrlEditText.setText(RMTP_URL);

        _startRtmpPushButton.setOnClickListener(_startRtmpPushOnClickedEvent);

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,SettingActivity.class));
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        InitUI();
    }

}
