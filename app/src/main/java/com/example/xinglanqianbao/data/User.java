package com.example.xinglanqianbao.data;
/**
 * Created by 李福森 2020/1/08
 */
public class User {
    private String phone;            //用户名
    private String password;        //密码

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status ;
    public User(String name, String password) {
        this.phone = name;
        this.password = password;
    }
    public String getphone() {
        return phone;
    }
    public void setphone(String name) {
        this.phone = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

