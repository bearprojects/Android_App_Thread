package com.project.thread;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;
    int UPDATE_DIALOG = 0; //自訂訊息碼
    ProgressDialog dialog;
    int Max = 100; //ProgressDialog進度最大值
    int currentProgress = 0; //ProgressDialog目前進度值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Thread");

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(btnClick);
    }

    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            currentProgress = 0;
            dialog = new ProgressDialog(MainActivity.this); //產生一個ProgressDialog
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("下載中...");
            dialog.setProgress(currentProgress);
            dialog.setMax(Max);
            dialog.setCancelable(false); //對話視窗不可按返回取消
            dialog.show(); //顯示對話視窗
            UpdateThread thread = new UpdateThread();
            thread.start(); //啟動更新執行緒
        }
    };

    //新增一個Handler
    //特別注意要import的是android.os.Handler
    Handler handler = new Handler() {
        //接收到訊息
        public void handleMessage(android.os.Message msg){
            if(msg.what == UPDATE_DIALOG)
                if(msg.arg1 == Max)
                    dialog.dismiss(); //下載進度完成關閉ProgressDialog
                else
                    dialog.setProgress(msg.arg1); //更新ProgressDialog
        }
    };

    //新增UpdateThread類別， 並實作其執行的方法
    class UpdateThread extends Thread{
        @Override
        public void run() {
            while (currentProgress < Max){
                //當目前進度小於ProgressDialog最大值則繼續執行工作
                try {
                    Thread.sleep(100); //停頓0.1秒後通知Handle更新ProgressDialog
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                currentProgress++;
                Message msg = handler.obtainMessage(); //產生Message訊息
                msg.what = UPDATE_DIALOG;
                msg.arg1 = currentProgress;
                handler.sendMessage(msg); //發送訊息到訊息佇列中
            }
            super.run();
        }
    }
}
