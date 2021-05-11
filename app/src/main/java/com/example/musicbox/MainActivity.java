package com.example.musicbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {


    // 获取界面中显示歌曲标题、作者文本框
    TextView title, author;
    // 播放/暂停按钮、停止按钮
    ImageButton play, stop;
    //上一首、下一首按钮
    ImageButton previous, next;

    ActivityReceiver activityReceiver;

    static final String CTL_ACTION = "org.zzw.action.CTL_ACTION";
    static final String UPDATE_ACTION = "org.zzw.action.UPDATE_ACTION";
    // 定义音乐的播放状态，0x11代表没有播放；0x12代表正在播放；0x13代表暂停
    int status = 0x11;
    String[] titleStrs = {"I'll Show You", "Vincent", "Versace On The Floor"};
    String[] authorStrs = {"Justin Bieber", "Don McLean", "Bruno Mars"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 获取程序界面界面中的按钮控件
        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        //获取文本组件
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);


        // 为按钮的单击事件添加监听器
        play.setOnClickListener((View.OnClickListener) this);
        stop.setOnClickListener((View.OnClickListener) this);
        previous.setOnClickListener((View.OnClickListener) this);
        next.setOnClickListener((View.OnClickListener) this);

        //获取进度条控件

        activityReceiver = new ActivityReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        // 注册BroadcastReceiver
        registerReceiver(activityReceiver, filter);

        // 启动后台Service
        startService(new Intent(this, MusicService.class));


    }

    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int update = intent.getIntExtra("update",-1);
            int current = intent.getIntExtra("current", -1);

            if (current >= 0){
                title.setText(titleStrs[current]);
                author.setText(authorStrs[current]);
            }

            switch (update){
                case 0x11:
                    play.setImageResource(R.drawable.play);
                    status = 0x11;
                    break;
                case 0x12:
                    play.setImageResource(R.drawable.pause);
                    status = 0x12;
                    break;

                case 0x13:
                    play.setImageResource(R.drawable.play);
                    status = 0x13;
                    break;

            }


        }
    }

    @Override
    public void onClick(View source)
    {
        // 创建Intent
        Intent intent = new Intent(CTL_ACTION);
        switch (source.getId())
        {
            // 按下播放/暂停按钮
            case R.id.play:
                intent.putExtra("control", 1);
                break;
            // 按下停止按钮
            case R.id.stop:
                intent.putExtra("control", 2);
                break;
            // 按下上一首
            case R.id.previous:
                intent.putExtra("control", 3);
                break;
            // 按下下一首
            case R.id.next:
                intent.putExtra("control", 4);
                break;
        }
        // 发送广播，将被Service组件中的BroadcastReceiver接收到
        sendBroadcast(intent);
    }
}