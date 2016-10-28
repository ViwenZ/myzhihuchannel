package com.zjw.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kevin.wraprecyclerview.WrapRecyclerView;
import com.zjw.myapplication.activity.WeiXinDetailActivity;
import com.zjw.myapplication.adapter.WeiXinAdapter;
import com.zjw.myapplication.entity.WeiXin;
import com.zjw.myapplication.model.WeiXinModel;
import com.zjw.myapplication.utils.OnFinishListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.tl_custom)
    Toolbar tlCustom;
    @InjectView(R.id.recycler_main)
    WrapRecyclerView recyclerMain;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.drawerlayout)
    DrawerLayout drawerlayout;
    ViewPager mViewPager;
    LinearLayout llAdvContainer;
    TextView txtAdvTitle;
    private ActionBarDrawerToggle toggle;
    private WeiXinModel wxModel;
    private List<WeiXin> weiXins;
    private List<WeiXin> headerWeiXins;
    private List<ImageView> advIvs;
    private WeiXinAdapter wxAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int page = 2;
    private int mPreviousPosition;
    /**
     * 广告条的自动轮播效果,将页面自动切换到下一个页面
     * 实现方法之一：可以利用handler发送一条延时消息
     * 实现方法之二：可以利用定时器
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = mViewPager.getCurrentItem();//获取viewPager当前页面的位置
            mViewPager.setCurrentItem(++currentItem );

            //继续发送延时5秒的消息，类似递归的效果，使广告一直切换
            mHandler.sendEmptyMessageDelayed(0, 5000);

            //当用户触摸的时候，自动轮播就应该停止下来
            mViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            mHandler.sendEmptyMessageDelayed(0, 5000);
                            break;
                        default:
                            break;
                    }
                 /*如果这里返回true，viewPager的事件将会被消耗掉，ViewPager将会响应不了
                     所以这里要返回false，让viewPager原生的触摸效果正常运行*/
                    return false;
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initToolbar();
        initMain();
        setListener();
    }

    private void initAdvBar() {
        //获得轮播页
        wxModel.getWeiXins(1, new OnFinishListener() {
            @Override
            public Void onSuccess(Object obj) {
                headerWeiXins = (List<WeiXin>) obj;
                advIvs=new ArrayList<ImageView>();
                for (int i = 0; i < headerWeiXins.size(); i++) {
                    ImageView imageView= new ImageView(MainActivity.this);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(params);
                    Glide.with(MainActivity.this).load(headerWeiXins.get(i).getFirstImg()).into(imageView);
                    advIvs.add(imageView);
                }
                //动态添加5个小圆点
                for (int i = 0; i < headerWeiXins.size(); i++) {
                    ImageView imageView = new ImageView(MainActivity.this);
                    imageView.setImageResource(R.drawable.dot);
                    // 给小圆点之间设置间距，获取圆点的父布局的布局参数，然后给其设置左边距
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
                    //从第二个圆点开始设置左边距
                    if (i != 0) {
                        params.leftMargin = 8;
                        imageView.setEnabled(false);
                    }
                    imageView.setLayoutParams(params);
                    llAdvContainer.addView(imageView);
                }
                mViewPager.setAdapter(new MyViewPagerAdapter());
                //设置ViewPager起始页为第一页，并且可以向左滑动
                mViewPager.setCurrentItem(headerWeiXins.size() * 5000);
                txtAdvTitle.setText(headerWeiXins.get(0).getTitle());
                //延时5秒发送消息
                mHandler.sendEmptyMessageDelayed(0, 5000);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        int pos = position % headerWeiXins.size();
                        txtAdvTitle.setText(headerWeiXins.get(pos).getTitle());
                        llAdvContainer.getChildAt(pos).setEnabled(true);
                        llAdvContainer.getChildAt(mPreviousPosition).setEnabled(false);
                        mPreviousPosition = pos;

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                return null;
            }

            @Override
            public Void onError() {
                return null;
            }
        });

    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 2;
                wxModel.getWeiXins(page, new OnFinishListener() {
                    @Override
                    public Void onSuccess(Object obj) {
                        weiXins.clear();
                        weiXins.addAll((List<WeiXin>) obj);
                        wxAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        return null;
                    }

                    @Override
                    public Void onError() {
                        return null;
                    }
                });
            }
        });

        //RecyclerView滑动监听
        recyclerMain.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount() - 1) {
                    page++;
                    wxModel.getWeiXins(page, new OnFinishListener() {
                        @Override
                        public Void onSuccess(Object obj) {
                            weiXins.addAll((List<WeiXin>) obj);
                            wxAdapter.notifyDataSetChanged();
                            return null;
                        }

                        @Override
                        public Void onError() {
                            return null;
                        }
                    });
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }

    private void initMain() {
        wxModel = new WeiXinModel();
        weiXins = new ArrayList<>();
        headerWeiXins = new ArrayList<>();
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMain.setLayoutManager(linearLayoutManager);
//                recyclerMain.addItemDecoration(new DividerItemDecoration(
//                        MainActivity.this, DividerItemDcoration.HORIZONTAL_LIST));
        View headerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_recyclerview_header, null, false);
        recyclerMain.addHeaderView(headerView);
        mViewPager = (ViewPager) headerView.findViewById(R.id.vp_main_adv);
        llAdvContainer = (LinearLayout) headerView.findViewById(R.id.ll_adv_container);
        txtAdvTitle = (TextView) headerView.findViewById(R.id.txt_adv_title);
        initAdvBar();
        wxModel.getWeiXins(page, new OnFinishListener() {
            @Override
            public Void onSuccess(Object obj) {
                weiXins = (List<WeiXin>) obj;
                wxAdapter = new WeiXinAdapter(MainActivity.this, weiXins);
                recyclerMain.setAdapter(wxAdapter);
                wxAdapter.setOnItemClickLitener(new WeiXinAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, WeiXinDetailActivity.class);
                        intent.putExtra("weixin", weiXins.get(position));
                        startActivity(intent);
                    }
                });
                return null;
            }

            @Override
            public Void onError() {
                return null;
            }
        });
    }

    private void initToolbar() {
        tlCustom.setTitle("首页");
        tlCustom.setTitleTextColor(Color.WHITE);
        tlCustom.inflateMenu(R.menu.menus);//设置菜单
        setSupportActionBar(tlCustom);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        tlCustom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_item1) {
                    Toast.makeText(MainActivity.this, "item_01", Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.action_item2) {
                    Toast.makeText(MainActivity.this, "item_02", Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawerlayout, tlCustom, R.string.open, R.string.close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        toggle.syncState();
        drawerlayout.addDrawerListener(toggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);
        return true;
    }

    /**
     * PagerAdapter：ViewPager的适配器
     * ViewPager适配器必须要实现的几个方法
     * getCount
     * isViewFromObject
     * instantiateItem
     * destroyItem
     */
    class MyViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //返回图片的数量
            //如果要使viewpager循环，直接返回比mImageIds.length大的值就可以了
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // 初始化ViewPager中的View
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int pos = position % headerWeiXins.size();
            ImageView imageView= advIvs.get(pos);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WeiXinDetailActivity.class);
                    intent.putExtra("weixin", headerWeiXins.get(pos));
                    startActivity(intent);
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
