package com.example.myapp.myapp.data.api;

/**
 * Created by daixiankade on 2018/3/28.
 */

public class AppUrl {

    public static int index=1;



    public static final String BASEURL="http://www.wanandroid.com/";

    public static final String BASEURL2="https://api.douban.com/";

    /**
     * banner
     */
    public static final String BANNER=BASEURL+"banner/json";

    /**
     * 首页
     */
    public static  String HOMELIST=BASEURL+"article/list/"+String.valueOf(index)+"/json";

    public  static String getHOMELIST(int i) {
        return BASEURL+"article/list/"+String.valueOf(i)+"/json";
    }
}