package com.gjmetal.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.gjmetal.app.base.App;
import com.gjmetal.app.api.Constant;
import com.gjmetal.app.model.market.Future;
import com.gjmetal.star.R;
import com.gjmetal.star.log.XLog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Description：数据存储
 * Author: star
 * Email: guimingxing@163.com
 * Date: 2018-3-28  17:20
 */
public class SharedUtil {


    public static void put(String modelName, String name, String value) {
        SharedPreferences.Editor readSp = App.getContext().getSharedPreferences(modelName, 0).edit();
        readSp.putString(name, value);
        readSp.commit();
    }

    public static String get(String modelName, String name) {
        SharedPreferences getsp = App.getContext().getSharedPreferences(modelName, 0);
        return getsp.getString(name, null);
    }


    public static void put(String name, boolean value) {
        SharedPreferences.Editor readSp = App.getContext().getSharedPreferences(ValueUtil.getString(R.string.app_name), 0).edit();
        readSp.putBoolean(name, value);
        readSp.commit();
    }


    public static Boolean getBoolean(String name) {
        SharedPreferences getsp = App.getContext().getSharedPreferences(ValueUtil.getString(R.string.app_name), 0);
        return getsp.getBoolean(name, true);
    }

    public static void putInt(String name,int i){
        SharedPreferences.Editor sharedPreferences = App.getContext().getSharedPreferences(name, 0).edit();
        sharedPreferences.putInt(name,i);
        sharedPreferences.commit();

    }

    public static int getInt(String name){
        SharedPreferences getspint = App.getContext().getSharedPreferences(name,0);
        return getspint.getInt(name,0);

    }

    public static String get(String key) {
        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(ValueUtil.getString(R.string.app_name), Context.MODE_PRIVATE);
        String cString = sharedPreferences.getString(key, "");
        if (ValueUtil.isStrEmpty(cString)) {
            cString = "";
        }
        return cString;
    }

    public static void put(String key, String content) {
        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(ValueUtil.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, content);
        edit.commit();
    }

    //清除本地用户信息
    public static void clearData() {
        SharedUtil.put(Constant.TOKEN, "");//清空用户token
        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(ValueUtil.getString(R.string.app_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
    public static void clearData(String modelName) {
        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences(modelName, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }
    public static class ListDataSave {
        /**
         * 保存List
         *
         * @param tag
         * @param datalist
         */
        public static <T> void setDataList( String tag, List<T> datalist,String perferenceName) {
            SharedPreferences preferences = App.getContext().getSharedPreferences(perferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (null == datalist || datalist.size() <= 0)
                return;
            editor.clear();
            Gson gson = new Gson();
            //转换成json数据，再保存
            if(ValueUtil.isListNotEmpty(datalist)){
                String strJson = gson.toJson(datalist);
                editor.putString(tag, strJson);
            }
            editor.commit();

        }

        public static <T> List<T> getDataList(String tag,String perferenceName, Class<T> t) {
            SharedPreferences preferences = App.getContext().getSharedPreferences(perferenceName, Context.MODE_PRIVATE);
            String strJson = preferences.getString(tag, null);
            List<T> list = new ArrayList<>();
            if(null==strJson){
                return list;
            }
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jsonarray = parser.parse(strJson).getAsJsonArray();
            for (JsonElement element : jsonarray) {
                list.add(gson.fromJson(element, t));
            }
            return list;
        }

        public static List<Future>getMarketDataList(String tag,String perferenceName){
            SharedPreferences preferences = App.getContext().getSharedPreferences(perferenceName, Context.MODE_PRIVATE);
            String strJson = preferences.getString(tag, null);
            Gson gson=new Gson();
            List<Future> list = gson.fromJson(strJson, new TypeToken<List<Future>>() {
            }.getType());
            return list;
        }


        public static void clean(String tag,String perferenceName){
            SharedPreferences preferences = App.getContext().getSharedPreferences(perferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }

    }
}
