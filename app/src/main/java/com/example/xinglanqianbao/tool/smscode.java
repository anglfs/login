package com.example.xinglanqianbao.tool;

import org.json.JSONObject;
/**
 * Created by 李福森 2020/03/23
 */
public class smscode {
    static public String getsms(Integer codetype ,String phoneNumber, String sign)throws Exception {
        JSONObject jsonsms = new JSONObject();
        jsonsms.put("codeType", codetype);
        jsonsms.put("mobile", phoneNumber);
        jsonsms.put("regSource ", 1);
        jsonsms.put("sign", sign);
        return jsonsms.toString().trim();
    }
}
