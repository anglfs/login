package com.example.xinglanqianbao.tool;

import org.json.JSONObject;

/**
 * Created by 李福森 2020/03/23
 */
public class Jsonswith {
    static public String code;
    public static  void swithjson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
             code  = jsonObject.getString("code");
            System.out.println("到底做错了什么status" + code+jsonObject.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
