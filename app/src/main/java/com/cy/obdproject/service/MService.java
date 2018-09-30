package com.cy.obdproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.cy.obdproject.tools.LogTools;

import java.lang.reflect.Method;

public class MService extends Service {

    private TelephonyManager telephonyManager;
    private MService.MyPhoneStateListener myPhoneStateListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("cyf", "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("cyf", "onCreate");
        // 监听电话状态
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        // 参数1:监听
        // 参数2:监听的事件
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("cyf", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            Log.e("cyf", "onCallStateChanged  " + state);
//            if (((MyApp) getApplication()).getActivityList().size() > 0) {
//                // 如果是响铃状态,检测拦截模式是否是电话拦截,是挂断
//                if (state == TelephonyManager.CALL_STATE_RINGING) {
//                    // 获取拦截模式
//                    // 挂断电话 1.5
//                    endCall();
//                    // 删除通话记录
//                    // 1.获取内容解析者
//                    final ContentResolver resolver = getContentResolver();
//                    // 2.获取内容提供者地址 call_log calls表的地址:calls
//                    // 3.获取执行操作路径
//                    final Uri uri = Uri.parse("content://call_log/calls");
//                    // 4.删除操作
//                    // 通过内容观察者观察内容提供者内容,如果变化,就去执行删除操作
//                    // notifyForDescendents : 匹配规则,true : 精确匹配 false:模糊匹配
//                    resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
//                        // 内容提供者内容变化的时候调用
//                        @Override
//                        public void onChange(boolean selfChange) {
//                            super.onChange(selfChange);
//                            // 删除通话记录
//                            resolver.delete(uri, "number=?", new String[]{incomingNumber});
//                            // 注销内容观察者
//                            resolver.unregisterContentObserver(this);
//                        }
//                    });
//                }
//            } else {
//                stopSelf();
//            }
        }
    }

    /**
     * 挂断电话
     */
    public void endCall() {
        Log.e("cyf", "endCall");
        //通过反射进行实现
        try {
            //1.通过类加载器加载相应类的class文件
            //Class<?> forName = Class.forName("android.os.ServiceManager");
            Class<?> loadClass = MService.class.getClassLoader().loadClass("android.os.ServiceManager");
            //2.获取类中相应的方法
            //name : 方法名
            //parameterTypes : 参数类型
            Method method = loadClass.getDeclaredMethod("getService", String.class);
            //3.执行方法,获取返回值
            //receiver : 类的实例
            //args : 具体的参数
            IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            //aidl
            ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
            //挂断电话
            iTelephony.endCall();
        } catch (Exception e) {
            LogTools.errLog(e);
        }
        Log.e("cyf", "endCall ok");
    }

}
