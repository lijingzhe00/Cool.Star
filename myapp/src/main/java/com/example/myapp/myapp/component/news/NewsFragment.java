package com.example.myapp.myapp.component.news;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.myapp.R;
import com.example.myapp.myapp.base.BaseFragment;
import com.example.myapp.myapp.component.news.detail.NewsDetailFragment;
import com.example.myapp.myapp.component.news.detail.NewsDetailPresenter;
import com.example.myapp.myapp.data.bean.NewsCategoryResponse;
import com.example.myapp.myapp.data.source.news.detail.NewsDetailRepository;
import com.example.myapp.myapp.ui.adapter.FragmentAdapter;
import com.example.myapp.myapp.ui.view.ColorFlipPagerTitleView;
import com.example.myapp.myapp.utils.ToastUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yexing on 2018/10/10.
 */

public class NewsFragment extends BaseFragment implements NewsContract.View {
    private NewsContract.Presenter mPresenter;
    private ViewPager mViewPager;
    private MagicIndicator magicIndicator;
    private List<BaseFragment> mNewsFragmentList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();

    @Override
    protected boolean isNeedToBeSubscriber() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news;
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }


    @Override
    public void initView() {
        magicIndicator = getView(R.id.magic_indicator);
        mViewPager = getView(R.id.view_pager);
    }

    @Override
    public void initData() {
        mPresenter.requestCategory();
    }

    private void initIndicator() {
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), mNewsFragmentList));
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mTitleList.get(index));
                simplePagerTitleView.setNormalColor(Color.WHITE);
                simplePagerTitleView.setSelectedColor(Color.parseColor("#4CAF50"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 6));
                indicator.setLineWidth(UIUtil.dip2px(context, 10));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#00c853"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }


    @Override
    public void setPresenter(NewsContract.Presenter presenter) {
        mPresenter = presenter;
    }


    /**
     * 请求成功
     *
     * @param result
     */
    @Override
    public void requestSuccess(NewsCategoryResponse result) {
        List<NewsCategoryResponse.ResultBean> categoryList = result.getResult();
        for (int i = 0; i < categoryList.size(); i++) {
            NewsCategoryResponse.ResultBean resultBean = categoryList.get(i);
            mTitleList.add(resultBean.getName());
            NewsDetailFragment newsDetailFragment = NewsDetailFragment.newInstance(resultBean.getCid());
            new NewsDetailPresenter(newsDetailFragment, new NewsDetailRepository());
            mNewsFragmentList.add(newsDetailFragment);
        }
        initIndicator();
    }

    /**
     * 请求失败
     *
     * @param errorMsg
     */
    @Override
    public void requestFail(String errorMsg) {
        ToastUtil.showApp(errorMsg);
    }
}