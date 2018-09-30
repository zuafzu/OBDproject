package com.qiming.eol_message;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InitClass {

    private  final int maxSize = 1000;

    private  String readLiveItems;
    private  String readFreezeItems;
    private  Handler handler;
    private List<String> stringList = new ArrayList<>();

    private com.qiming.eol_public.InitClass publicUnit;
    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit)
    {
        this.publicUnit = publicUnit;
    }
    private Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private List<String> getStringList() {
        return stringList;
    }


    /**
     * 显示文本
     *
     * @param inputdata
     * @return
     */
    public String AddMessage(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "FAULT");
            jsonObject.put("DESC", "没设置初始化handler");
            JSONObject jsonObject1 = new JSONObject(inputdata);
            String mjson = jsonObject1.optString("DATA");
            if (getStringList().size() == maxSize) {
                ClearMessage();
            }
            Message message = new Message();
            message.what = 0;
            message.obj = mjson;
            getHandler().sendMessage(message);
            getStringList().add(mjson);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 显示文本(错误信息)
     *
     * @param inputdata
     * @return
     */
    public String AddMessage2(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "FAULT");
            jsonObject.put("DESC", "没设置初始化handler");
            JSONObject jsonObject1 = new JSONObject(inputdata);
            String mjson = jsonObject1.optString("DATA");
            if (getStringList().size() == maxSize) {
                ClearMessage();
            }
            Message message = new Message();
            message.what = 0;
            message.arg1 = 1;
            message.obj = mjson;
            getHandler().sendMessage(message);
            getStringList().add(mjson);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 文本过长时自动清除
     *
     * @return
     */
    public String ClearMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            Message message = new Message();
            message.what = -1;
            getHandler().sendMessage(message);
            getStringList().clear();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 通知更改进度条
     *
     * @return
     */
    public void SetFlashPos(int pos) {
        Message message = new Message();
        message.what = 1;
        message.obj = pos;
        getHandler().sendMessage(message);
    }

    /**
     * 显示listView
     *
     * @param inputdata
     * @return
     */
    public String ShowDTC(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "FAULT");
            jsonObject.put("DESC", "没设置初始化handler");
            JSONObject jsonObject1 = new JSONObject(inputdata);
            String mjson = jsonObject1.optString("DATA");
            Message message = new Message();
            message.what = 2;
            message.obj = mjson;
            getHandler().sendMessage(message);
            getStringList().add(mjson);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 显示界面预先效果
     *
     * @param inputdata
     * @return
     */
    public String SetBaseInfo(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "FAULT");
            jsonObject.put("DESC", "没设置初始化handler");
            JSONObject jsonObject1 = new JSONObject(inputdata);
            String mjson = jsonObject1.optString("DATA");
            Message message = new Message();
            message.what = 3;
            message.obj = mjson;
            getHandler().sendMessage(message);
            getStringList().add(mjson);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 显示动态数据初始界面
     *
     * @param inputdata
     * @return
     */
    public String SetLiveInfo(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "FAULT");
            jsonObject.put("DESC", "没设置初始化handler");
            JSONObject jsonObject1 = new JSONObject(inputdata);
            String mjson = jsonObject1.optString("DATA");
            Message message = new Message();
            message.what = 4;
            message.obj = mjson;
            getHandler().sendMessage(message);
            getStringList().add(mjson);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 设置选择的动态数据
     *
     * @param inputdata
     * @return
     */
    public String SetReadLiveItems(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            readLiveItems = inputdata;
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 返回选择的动态数据
     *
     * @param inputdata
     * @return
     */
    public String GetReadLiveItems(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DATA", readLiveItems);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 设置冻结帧数据
     *
     * @param inputdata
     * @return
     */
    public String SetReadFreezeItems(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            readFreezeItems = inputdata;
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    /**
     * 返回冻结帧数据
     *
     * @param inputdata
     * @return
     */
    public String GetReadFreezeItems(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DATA", readFreezeItems);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

}
