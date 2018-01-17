package net.bwie.month12exam.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.bwie.month12exam.R;
import net.bwie.month12exam.bean.DetailBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 使用Toolbar，需要隐藏原有的Actionbar
 * 在values/styles文件中修改为NoActionBar风格
 */
public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.title_iv)
    ImageView mTitleIv;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.comments_tv)
    TextView mCommentsTv;
    @BindView(R.id.like_tv)
    TextView mLikeTv;
    private DetailBean mDetailBean;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        initView();

        initData();
        showData();
    }

    private void initView() {
        // toolbar虽然长得比actionbar好看，但功能还是延用了actionbar
        // 把toolbar当做actionbar使用，就可以使用里面的逻辑了
        setSupportActionBar(mToolbar);
        // 获取toolbar对应的actionbar，操作逻辑
        mActionBar = getSupportActionBar();

        // 设置标题栏左边是否展示返回箭头
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void showData() {
        // 给折叠toolbar布局设置标题
        mCollapsingToolbarLayout.setTitle(mDetailBean.getTitle());
        mToolbar.setTitle(mDetailBean.getTitle());
        // 标题展开背景
        Glide.with(this)
                .load(mDetailBean.getTitle_bg_url())
                .into(mTitleIv);

        mCommentsTv.setText("评论：" + mDetailBean.getComments());
        mLikeTv.setText("点赞：" + mDetailBean.getLike());

        // 加载html代码
        mWebView.loadDataWithBaseURL(null,
                mDetailBean.getHtmlCode(),
                "text/html",
                "utf-8",
                null);
    }

    private void initData() {
        mDetailBean = new DetailBean();
        mDetailBean.setTitle("美国男同学加我好友");
        mDetailBean.setTitle_bg_url("http://file31.mafengwo.net/M00/21/3C/wKgBs1bz8dmAJqSwAArv_7pK7cI03.groupinfo.w680.jpeg");
        mDetailBean.setComments(128);
        mDetailBean.setLike(256);
        String htmlCode = "";
        for (int i = 0; i < 100; i++) {
            htmlCode += "<div>adasdasdasdsdasdasdsfsddfsdfsdfsdfsdfsdfsdfsd</div><br/>";
        }
        mDetailBean.setHtmlCode(htmlCode);
    }

    // 实现菜单的点击监听，其中可实现toolbar返回按钮的点击监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
