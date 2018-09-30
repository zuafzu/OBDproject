package com.cy.obdproject.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cy.obdproject.R;
import com.cy.obdproject.activity.LoginActivity;
import com.cy.obdproject.app.MyApp;
import com.cy.obdproject.base.BaseActivity;
import com.cy.obdproject.bean.BaseBean;
import com.cy.obdproject.constant.Constant;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class NetTools {

    public static void net(String url, final Activity context, final MyCallBack myCallBack) {
        net(new HashMap<String, String>(), url, context, myCallBack, "正在加载...");
    }

    public static void net(Map<String, String> map, String url, final Activity context, final MyCallBack myCallBack) {
        net(map, url, context, myCallBack, "正在加载...");
    }

    public static void net(Map<String, String> map, String url, final Activity context, final MyCallBack myCallBack, final String msg) {
        net(map, url, context, myCallBack, msg, true, true);
    }

    public static void net(Map<String, String> map, String url, final Activity context, final MyCallBack myCallBack, final String msg, final boolean isShow, final boolean isDismiss) {
        String s = new Gson().toJson(map);
        net(s, url, context, myCallBack, msg, isShow, isDismiss);
    }

    public static void net(String json, String url, final Activity context, final MyCallBack myCallBack) {
        net(json, url, context, myCallBack, "正在加载...");
    }

    public static void net(String json, String url, final Activity context, final MyCallBack myCallBack, final String msg) {
        net(json, url, context, myCallBack, msg, true, true);
    }

    public static void net(final String json, final String url, final Activity context, final MyCallBack myCallBack, final String msg, final boolean isShow, final boolean isDismiss) {
        Log.e("zj", "net json = " + json);
        Log.e("zj", "net url = " + url);
        Log.e("zj", "net token = " + SPTools.INSTANCE.get(context, Constant.TOKEN, ""));

        RequestCall call = OkHttpUtils.postString().url(url)
                .addHeader(Constant.TOKEN, (String) SPTools.INSTANCE.get(context, Constant.TOKEN, ""))
                .mediaType(MediaType.parse("application/json"))
                .content(json)
                .build();

        call.execute(new Callback<BaseBean>() {
            @Override
            public void onBefore(Request request, int id) {
                if (context != null && isShow) {
                    ((BaseActivity) context).showProgressDialog();
                }
            }

            @Override
            public BaseBean parseNetworkResponse(Response response, int id) throws Exception {
                String json = response.body().string();
                Log.e("cyf7", "response : " + json);
                JSONObject jsonObject = new JSONObject(json);
                BaseBean bean = new BaseBean();
                bean.setCode(jsonObject.optString("code"));
                bean.setMsg(jsonObject.optString("msg"));
                String json2 = jsonObject.optString("data");
                if (!"".equals(json2) && !"{}".equals(json2) && !"{ }".equals(json2)) {
                    bean.setData(json2);
                }
                return bean;
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                // 无数据布局隐藏(后期可做网络错误显示)
//                ((BaseActivity) context).setListToastView(0, "", 0, false);
                if (context.getLocalClassName().contains("LoginActivity")) {
                    // 登录界面
                    Toast.makeText(context, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                    Log.e("cyf", "http Exception ：" + e.toString());
                    ((BaseActivity) context).dismissProgressDialog();
                } else if (context.getLocalClassName().contains("WriteDataActivity")) {
                    // 下载刷写文件界面
                    TextView textView = context.findViewById(R.id.tv_notice);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("获取生产文件失败，请重试");
                    }
                    if (context.findViewById(R.id.listView) != null) {
                        context.findViewById(R.id.listView).setVisibility(View.GONE);
                    }
                } else if (context.getLocalClassName().contains("ResponseListActivity")) {
                    // 专家列表界面
                    TextView textView = context.findViewById(R.id.tv_notice);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("获取可协助的专家列表失败，请重试");
                    }
                    if (context.findViewById(R.id.listView) != null) {
                        context.findViewById(R.id.listView).setVisibility(View.GONE);
                    }
                } else if (context.getLocalClassName().contains("WelcomeActivity")) {
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("初始化异常")
                            .setCancelable(false)
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    context.finish();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                    Log.e("cyf", "http Exception ：" + e.toString());
                } else {
                    // Toast.makeText(context, "连接服务器失败，请重试", Toast.LENGTH_SHORT).show();
                    Log.e("cyf", "http Exception ：" + e.toString());
                }
                ((BaseActivity) context).dismissProgressDialog();
            }

            @Override
            public void onResponse(BaseBean baseBean, int id) {
                Log.e("zj", "bean = " + baseBean.toString());
                if ("0".equals(baseBean.getCode())) {
                    if (myCallBack != null) {
                        myCallBack.getData(baseBean);
                    }
                    if (isDismiss) {
                        ((BaseActivity) context).dismissProgressDialog();
                    }
                } else if ("1002".equals(baseBean.getCode())) {
                    // 登录信息失效
                    Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    SPTools.INSTANCE.put(context, Constant.ISLOGIN, "");
                    for (int i = 0; i < ((MyApp) context.getApplication()).getActivityList().size(); i++) {
                        ((MyApp) context.getApplication()).getActivityList().get(i).finish();
                    }
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    if (context.getLocalClassName().contains("WelcomeActivity")) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("提示")
                                .setMessage(baseBean.getMsg())
                                .setCancelable(false)
                                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        context.finish();
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                        ((BaseActivity) context).dismissProgressDialog();
                    }
                }
            }

            @Override
            public void onAfter(int id) {

            }

        });
    }

    public interface MyCallBack {
        void getData(BaseBean response);
    }

}
