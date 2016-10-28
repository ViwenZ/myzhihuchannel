package com.zjw.myapplication.urls;

/**
 * Created by Administrator on 2016/10/21.
 */

public class URLs {
    //http://v.juhe.cn/weixin/query?key=您申请的KEY
    public static final String getWeiXinUrl(int page){
        String url="http://v.juhe.cn/weixin/query?key=7bf9c73f7db932e245ca1f2664745d4a&ps=10&pno="+page;
        return  url;
    }
}
