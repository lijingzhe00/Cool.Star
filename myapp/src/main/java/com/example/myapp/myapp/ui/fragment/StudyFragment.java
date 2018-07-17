package com.example.myapp.myapp.ui.fragment;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapp.R;
import com.example.myapp.myapp.component.study.StudyFragmentContract;
import com.example.myapp.myapp.ui.activity.MainActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import com.example.myapp.myapp.ui.adapter.HomeAdapter;
import com.example.myapp.myapp.ui.adapter.SpaceItemDecoration;
import com.example.myapp.myapp.base.BaseFragment;
import com.example.myapp.myapp.data.bean.BannerBean;
import com.example.myapp.myapp.data.bean.HomeItemBean;
import com.example.myapp.myapp.data.http.HttpContext;

import com.example.myapp.myapp.utils.SPUtils;


/**
 * Created by yexing on 2018/3/28.
 */

public class StudyFragment extends BaseFragment implements StudyFragmentContract.View {


    private RecyclerView mRecylerview;
    private RelativeLayout llTitleContainer;
    private int sumY = 0;

    //色值渐变工具类
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private float duration = 350f;
    private HomeAdapter homeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private ViewPager mViewPager;

    private int color = -1;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingAttention;
    private FloatingActionButton floatingPost;
    private FloatingActionButton floatingSwitch;
    private FloatingActionButton floatingRefresh;
    private StudyFragmentContract.Presenter mPresenter;

    /**
     * 默认页码数
     */
    private static final int PAGE_NUMBER_DEFAULT = 1;

    /**
     * 页码
     */
    private int mPageNum = PAGE_NUMBER_DEFAULT;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        int currentPosition = mViewPager.getCurrentItem();
                        mViewPager.setCurrentItem(++currentPosition);
                        handler.sendEmptyMessageDelayed(1, 3500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.fragment_study;
    }


    public static StudyFragment newInstance() {
        return new StudyFragment();
    }

    @Override
    public void initData() {
        mPresenter.requestBannerAndStutyInfo(mPageNum);

    }


    @Override
    public void initView() {

        swipeRefreshLayout = getView(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNum = PAGE_NUMBER_DEFAULT;
                mPresenter.requestBannerAndStutyInfo(mPageNum);
            }
        });
        mRecylerview = getView(R.id.rv);
        llTitleContainer = getView(R.id.title_bar);
        //大菜单
        floatingActionMenu = getView(R.id.floatingMenu);
        floatingAttention = getView(R.id.floatingAttention);
        floatingPost = getView(R.id.floatingPost);
        floatingSwitch = getView(R.id.floatingSwitch);
        floatingRefresh = getView(R.id.floatingRefresh);

        floatingActionMenu.setClosedOnTouchOutside(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCtx, LinearLayoutManager.VERTICAL, false);
        mRecylerview.setLayoutManager(linearLayoutManager);
        mRecylerview.addItemDecoration(new SpaceItemDecoration(22));
        homeAdapter = new HomeAdapter(getActivity());
        mRecylerview.setAdapter(homeAdapter);
        mRecylerview.setItemAnimator(new DefaultItemAnimator());
        mRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //监听y轴,或者x轴上的偏移量
                sumY += dy;
                int bgColor = 0X553190E8;
                if (sumY <= 0) {
                    //顶部title显示的是透明颜色
                    bgColor = Color.TRANSPARENT;   // 0X553190E8
                    llTitleContainer.setVisibility(View.INVISIBLE);
                } else if (sumY > 350) {
                    //顶部显示蓝色
                    //终点颜色
                    if (color == -1) {
                        bgColor = 0XFF3190E8;
                    } else {
                        bgColor = color;
                    }

                    llTitleContainer.setVisibility(View.VISIBLE);
                } else {
                    //滚动过程中修改顶部的颜色值
                    bgColor = (int) argbEvaluator.evaluate(sumY / duration, Color.TRANSPARENT, bgColor);
                    llTitleContainer.setVisibility(View.VISIBLE);
                }
                llTitleContainer.setBackgroundColor(bgColor);
                super.onScrolled(recyclerView, dx, dy);


                //控制底部导航栏隐藏或战士
                if (dy > 50) {//up -> hide

                    ((MainActivity) getActivity()).tab_layout.setVisibility(View.GONE);

                } else if (dy < -50) {//down -> show
                    ((MainActivity) getActivity()).tab_layout.setVisibility(View.VISIBLE);
                }


            }


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //滚动状态变化
//                RecyclerView.SCROLL_STATE_SETTLING    惯性滑动
//                RecyclerView.SCROLL_STATE_IDLE        滚动空闲(滚动---->停止)
//                RecyclerView.SCROLL_STATE_DRAGGING    拖拽recyclerView滚动
                super.onScrollStateChanged(recyclerView, newState);
                //设置什么布局管理器,就获取什么的布局管理器
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition ,角标值
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    //所有条目,数量值
                    int totalItemCount = manager.getItemCount();
                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        //加载更多功能的代码
                        mPresenter.requestStudyInfo(++mPageNum);
                    }
                }

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sumY = 0;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(Message msg) {
        if (msg.what == 1) {
            StudyFragment.this.mViewPager = (ViewPager) msg.obj;
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(1, 3500);

        } else if (msg.what == 2) {

        }
    }


    @Override
    public void setPresenter(StudyFragmentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setBannerInfo() {

    }

    @Override
    public void setStudyInfo(HomeItemBean result) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        homeAdapter.addHomeInfo(result.getData().getDatas(), false);
    }

    @Override
    public void setBannerAndStudyInfo(Object result) {
        if (result instanceof BannerBean) {
            BannerBean bannerBean = (BannerBean) result;
            List<BannerBean.DataBean> datas = bannerBean.getData();
            checkDatas(datas);
            homeAdapter.addBanner(datas);

        } else {
            HomeItemBean homeItem = (HomeItemBean) result;
            HomeItemBean.DataBean data = homeItem.getData();
            List<HomeItemBean.DataBean.DatasBean> datas = data.getDatas();
            homeAdapter.addHomeInfo(datas, true);
        }
    }

    @Override
    public void showLoading() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
//            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideLoading() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {

            swipeRefreshLayout.setRefreshing(false);
        }
    }
}