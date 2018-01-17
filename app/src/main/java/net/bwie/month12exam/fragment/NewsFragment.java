package net.bwie.month12exam.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.bwie.month12exam.R;
import net.bwie.month12exam.activity.DetailActivity;
import net.bwie.month12exam.adapter.NewsAdapter;
import net.bwie.month12exam.application.MyApplication;
import net.bwie.month12exam.bean.DaoSession;
import net.bwie.month12exam.bean.NewsBean;
import net.bwie.month12exam.bean.StoriesBean;
import net.bwie.month12exam.bean.StoriesBeanDao;
import net.bwie.month12exam.event.SendPositionAndDataEvent;
import net.bwie.month12exam.httpservice.NewsHttpService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 最新消息和过往消息的数据结构相同，
 * 可以使用同一种类型的Fragment
 * <p>
 * 适配器最佳使用方式是伴随RecyclerView一起初始化，在网络数据产生后才去添加数据
 * 这么写能够保证适配器数据稳定性和扩展灵活性
 */
public class NewsFragment extends Fragment {

    // 定义两个标记代表不同的数据内容
    // 人为规定1代表最新消息，2代表过往消息
    public static final int TYPE_LATEST = 1;
    public static final int TYPE_BEFORE = 2;

    // 碎片类型
    private int mType;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private DaoSession mDaoSession;
    private StoriesBeanDao mDao;

    // 传参数创建Fragment的方式
    public static NewsFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);// 从外部传递加载数据类型
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在创建碎片是接收碎片类型，根据碎片类型判断加载最新还是过往
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt("type");
        }

        mDaoSession = MyApplication.getDaoSession();
        mDao = mDaoSession.getStoriesBeanDao();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, rootView);

        // 在数据产生之前就初始化适配器，防止后面空指针异常
        mAdapter = new NewsAdapter(getContext());
        // 绑定适配器
        mRecyclerView.setAdapter(mAdapter);

        // 加载网络数据
        loadData();
        return rootView;
    }

    // 根据接收到的数据类型加载对应的数据
    private void loadData() {
        NewsHttpService httpService = MyApplication.getRetrofit()
                .create(NewsHttpService.class);

        // RxJava的观察者
        Observable<NewsBean> observable = null;
        switch (mType) {
            case TYPE_LATEST:
                observable = httpService.getLatestObservable();
                break;
            case TYPE_BEFORE:
                observable = httpService.getBeforeObservable();
                break;
        }

        // Retrofit自动获取数据，自动将数据源设置给观察者
        // 我们只需要管理线程调度和数据变换
        // 下载数据和数据变换跳转至子线程
        observable.subscribeOn(Schedulers.io())
                // 链式调用的书写风格，map对数据进行变换
                .map(new Function<NewsBean, List<StoriesBean>>() {
                    // 将原始数据变换为我们想要的数据
                    @Override
                    public List<StoriesBean> apply(NewsBean newsBean) throws Exception {
                        return newsBean.getStories();
                    }
                })
                // 数据跳转至主线程
                .observeOn(AndroidSchedulers.mainThread())
                // 怎么用：【存储】到数据库中
                /**
                 * 我们要向数据库插入数据了，要执行insert方法
                 * 谁来执行insert呢？dao对象来执行
                 * dao从哪来？从DaoSesscion会话中得到
                 * DaoSession从哪来？从数据库对象db中得到
                 * db从哪来？从数据库助手中得到
                 * 数据库助手从哪来？在MyApplication中创建
                 */
                .subscribe(new Consumer<List<StoriesBean>>() {
                               @Override
                               public void accept(List<StoriesBean> storiesBeans) throws Exception {
                                   insertDatasInDB(storiesBeans);
                                   showDataByDB();
                               }
                           },
                        // 第二个消费者叫onError，包括断网情况也会走该操作
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                showDataByDB();
                            }
                        });
    }

    // 向数据库插入数据
    private void insertDatasInDB(List<StoriesBean> storiesBeans) {
        for (StoriesBean story : storiesBeans) {
            // 设置主键_id为时间戳
            story.set_id(System.currentTimeMillis());
            // 使用dao插入数据
            mDao.insert(story);
        }
    }

    // 从数据库加载数据并展示
    // 配合query查询类实现查询操作
    // 获取查询对象，如果需要，设置查询条件
    // 使用查询对象查询数据
    // 添加到适配器中展示
    private void showDataByDB() {
        QueryBuilder<StoriesBean> builder = mDao.queryBuilder();
        Query<StoriesBean> query = builder.build();
        // 查询全部数据
        List<StoriesBean> list = query.list();
        if (list != null && !list.isEmpty()) {
            mAdapter.addDatas(list);
        }
    }

    // 在Fragment可见时注册EventBus，这样就能接收到数据了
    // 在Fragment不可见时，解除注册，优化性能
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // 定义一个方法，接收事件
    // 方法参数要求设置为接收的事件
    @Subscribe// EventBus订阅了该方法，即可将数据发送到该方法中
    public void onReceivePositionAndData(SendPositionAndDataEvent event) {
        int position = event.getPosition();
        StoriesBean story = event.getData();

        Toast.makeText(getContext(), "位置：" + position, Toast.LENGTH_SHORT).show();

        // 接收到item点击事件这个事，我们跳转界面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

}
