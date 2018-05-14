package com.cy.obdproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.base.BaseActivity;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.socket.WebSocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;
import com.cy.obdproject.worker.StartWorker;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class MainTestActivity extends BaseActivity implements BaseActivity.ClickMethoListener {

    private Intent mIntent1, mIntent2;
    private StartWorker startWorker;

    private Button btn_1, btn_2, btn_3, btn_4;
    private TextView tv_1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        initView();
        initConnector();
        initData();
    }

    private void initView() {
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        tv_1 = findViewById(R.id.tv_1);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initConnector() {
        mIntent1 = new Intent(this, WebSocketService.class);
        mIntent2 = new Intent(this, SocketService.class);
        startWorker = new StartWorker();
    }

    private void initData() {
        setClickMethod(btn_1);
        setClickMethod(btn_2);
        setClickMethod(btn_3);
        setClickMethod(btn_4);
    }

    @Override
    public void doMethod(String string) {
        switch (string) {
            case "btn_1":
                startService(mIntent1);
                break;
            case "btn_2":

                break;
            case "btn_3":
//                18 da 00 fa ,08 , 03 22 f1 90       vin
//                18 da 00 fa ,08 ,03 22 f1 a6     硬件版本号
//                18 da 00 fa ,08 ,03 22 f1 a5    软件版本号
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(ECUagreement.
                        a("10", "18da00fa", "0003", "22F190")), new MySocketClient.ConnectLinstener() {
                    @Override
                    public void onReceiveData(final String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = ECUTools.getData(data, 3, "62F190");
                                tv_1.setText(msg);
                            }
                        });
                    }
                });
                break;
            case "btn_4":// 下载文件
                OkHttpUtils.get().url("http://p.gdown.baidu.com/60332a6f13574428559b663d5adad887219fa2e6070da7e66162d8d25561f8ffe6e9c60e6c68be502a45303ce87f36228a02825f92b8d9e5426f684e60de79128148cede56f7b1bc1a1bf927b5342d1c387caa1a980a0b4a1ed8501d01d454b35a151cde03b4abe3261361f02d9a87b531e25ce730a2c48b3a1253258e892f01bdc4ca836f83f382052d694050d08dbb1f80f193d1f58286ebb23d0a9e9b5e9199106b7d77cb43008bc4c220733a85e56e1d1101ac59f1a1541243c5a95e16e171b5bc1df2529ab2501183ae227166a9dc61601287ed5f695c9c5ae3eceebaf8ba791b3302edaef3c58f29b5df2eeb110c4fc32710c7b78263f4c6253581d960e8a1edb3d0229517ec1f579b1aa73b31eef65ecadba96d67c13cbbd216039347342b1370a7273a125009e4f902c86523aa14265001ac9f263b33cd0e8ba7daf69bc3fdf7e85ac556").
                        build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "测试.apk") {

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        progressBar.setProgress((int) (100 * progress));
                        Log.e("cyf", "progress : $progress");
                        if (WebSocketService.Companion.getIntance() != null) {
                            WebSocketService.Companion.getIntance().sendMsg("" + (100 * progress));
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mIntent1);
        stopService(mIntent2);
    }
}
