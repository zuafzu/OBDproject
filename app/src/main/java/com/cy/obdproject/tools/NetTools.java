package com.cy.obdproject.tools;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

    public static void net(String json, final String url, final Activity context, final MyCallBack myCallBack, final String msg, final boolean isShow, final boolean isDismiss) {
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
                bean.setMsg(jsonObject.optString("message"));
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
                if (e.getClass().getSimpleName().equals("ConnectException")) {
                    // 无法连接网络
                    Toast.makeText(context, "无法连接服务器", Toast.LENGTH_SHORT).show();
                } else if (e.getClass().getSimpleName().equals("SocketTimeoutException")) {
                    // 网络连接超时
                    Toast.makeText(context, "服务器连接超时", Toast.LENGTH_SHORT).show();
                } else {
                    // 其它异常
                    Log.e("Exception gson：", e.toString());
                }
                ((BaseActivity) context).dismissProgressDialog();
            }

            @Override
            public void onResponse(BaseBean baseBean, int id) {
                // 无数据布局隐藏
//                ((BaseActivity) context).setListToastView(0, "", 0, false);
                Log.e("zj", "bean = " + baseBean.toString());
                if ("0".equals(baseBean.getCode())) {

//                    Log.e("zj", "url = " + url);
//                    Log.e("zj", "baseBean = " + baseBean.toString());

//                    if (null == baseBean.getData() || "".equals(baseBean.getData())) {
//                        Toast.makeText(context, "返回data为null", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    if (myCallBack != null) {
                        myCallBack.getData(baseBean);
                    }
                    if (isDismiss) {
                        ((BaseActivity) context).dismissProgressDialog();
                    }
                } else if ("1002".equals(baseBean.getCode())) {
                    // 登录信息失效
                    Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < ((MyApp) context.getApplication()).getActivityList().size(); i++) {
                        ((MyApp) context.getApplication()).getActivityList().get(i).finish();
                    }
                    context.startActivity(new Intent(context, LoginActivity.class));
                } else {
                    Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
                    ((BaseActivity) context).dismissProgressDialog();
                }
            }

            @Override
            public void onAfter(int id) {

            }

        });
    }

    /**
     * 上传文件
     */
//    public static void netFile(Map<String, File> map, final Activity context, final MyCallBack myCallBack) {
//        Log.e("zj", "netFile = file_upload_url = " + Urls.file_upload_url);
////        PostFormBuilder builder = OkHttpUtils.post().url("http://192.168.13.9:8180/files/upload_file");
//        PostFormBuilder builder = OkHttpUtils.post().url(Urls.file_upload_url);
//        boolean isImgs = true;
//        for (int i = 0; i < map.size(); i++) {
//            if (!FileTools.isImgFile(map.get("file" + i).getName())) {
//                isImgs = false;
//            }
//            builder.addFile("file" + i, map.get("file" + i).getName(), map.get("file" + i));
//            Log.e("zj", "file = " + map.get("file" + i));
//        }
//        builder.addHeader("ticket", SecretKey.getKey(context));
//
//        builder.addParams("work", "" + SPTools.INSTANCE.get(context, Constant.WORK, ""));//
//        builder.addParams("transaction", "1");
//        builder.addParams("org", SPTools.INSTANCE.get(context, Constant.SCHOOLID, "").toString());
//        if (isImgs) {
//            builder.addParams("thumbnail", "1");
//        } else {
//            builder.addParams("thumbnail", "0");
//        }
//
//        RequestCall call = builder.build();
//        call.execute(new Callback<BaseBean>() {
//            @Override
//            public void onBefore(Request request, int id) {
//                if (context != null) {
//                    ((BaseActivity) context).showProgressDialog("正在上传...");
//                }
//            }
//
//            @Override
//            public BaseBean parseNetworkResponse(Response response, int id) throws Exception {
//                String json = response.body().string();
//                Log.e("cyf7", "response : " + json);
//                JSONObject jsonObject = new JSONObject(json);
//                BaseBean bean = new BaseBean();
//                bean.setCode(jsonObject.optString("code"));
//                bean.setMsg(jsonObject.optString("msg"));
//                // bean.setData(jsonObject.optString("data"));
//                bean.setData(jsonObject.optString("data")
//                        .replace("originalName", "originalFileName")
//                        .replace("filePath", "accessPath")
//                        .replace("fileSize", "size")
//                        .replace("thumbPath", "thumbnailPath"));
//                return bean;
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                if (e.getClass().getSimpleName().equals("ConnectException")) {
//                    // 无法连接网络
//                    Toast.makeText(context, "无法连接服务器", Toast.LENGTH_SHORT).show();
//                } else if (e.getClass().getSimpleName().equals("SocketTimeoutException")) {
//                    // 网络连接超时
//                    Toast.makeText(context, "服务器连接超时", Toast.LENGTH_SHORT).show();
//                } else {
//                    // 其它异常
//                    Log.e("Exception gson：", e.toString());
//                }
//                ((BaseActivity) context).dismissProgressDialog();
//            }
//
//            @Override
//            public void onResponse(BaseBean baseBean, int id) {
//                if ("0".equals(baseBean.getCode())) {
//                    myCallBack.getData(baseBean);
//                } else {
//                    ((BaseActivity) context).dismissProgressDialog();
//                    Toast.makeText(context, baseBean.getMsg(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });
//    }

    public interface MyCallBack {
        void getData(BaseBean response);
    }

}
