package com.zjw.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zjw.myapplication.R;
import com.zjw.myapplication.entity.WeiXin;

import java.lang.reflect.InvocationTargetException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class WeiXinDetailActivity extends AppCompatActivity {

    @InjectView(R.id.iv_weixindetail_share)
    ImageView ivWeixindetailShare;
    @InjectView(R.id.iv_weixindetail_collection)
    ImageView ivWeixindetailCollection;
    @InjectView(R.id.tl_custom)
    Toolbar tlCustom;
    @InjectView(R.id.txt_weixindetail_source)
    TextView txtWeixindetailSource;
    @InjectView(R.id.txt_weixindetail_title)
    TextView txtWeixindetailTitle;
    @InjectView(R.id.webview_weixindetail_content)
    WebView webviewWeixindetailContent;
    @InjectView(R.id.iv_weixindetail_back)
    ImageView ivWeixindetailBack;
    @InjectView(R.id.iv_weixindetail_firstimg)
    ImageView ivWeixindetailFirstimg;
    private WeiXin weiXin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_detail);
        ButterKnife.inject(this);
        initToolbar();
        initMain();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            webviewWeixindetailContent.getClass().getMethod("onPause").invoke(webviewWeixindetailContent, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            webviewWeixindetailContent.getClass().getMethod("onResume").invoke(webviewWeixindetailContent, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void initMain() {
        weiXin = (WeiXin) getIntent().getSerializableExtra("weixin");
        txtWeixindetailSource.setText(weiXin.getSource());
        txtWeixindetailTitle.setText(weiXin.getTitle());
        Glide.with(this)
                .load(weiXin.getFirstImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivWeixindetailFirstimg);
        webviewWeixindetailContent.loadUrl(weiXin.getUrl());
        webviewWeixindetailContent.getSettings().setJavaScriptEnabled(true);//支持js
        webviewWeixindetailContent.getSettings().setBlockNetworkImage(false);    //设置显示微信网页里的图片
        //设置播放微信网页里的视频
        webviewWeixindetailContent.getSettings().setPluginState(WebSettings.PluginState.ON);
        webviewWeixindetailContent.setWebChromeClient(new WebChromeClient());
        webviewWeixindetailContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initToolbar() {
        tlCustom.setTitle("");
        setSupportActionBar(tlCustom);
    }

    @OnClick({R.id.iv_weixindetail_back, R.id.iv_weixindetail_share, R.id.iv_weixindetail_collection})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_weixindetail_back:
                finish();
                break;
            case R.id.iv_weixindetail_share:
                showShare();
                break;
            case R.id.iv_weixindetail_collection:
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(weiXin.getTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(weiXin.getSource());
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(weiXin.getFirstImg());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(weiXin.getUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("真是厉害");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("viwenzhang的爱派派");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
}
