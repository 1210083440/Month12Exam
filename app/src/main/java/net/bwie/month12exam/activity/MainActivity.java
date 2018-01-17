package net.bwie.month12exam.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import net.bwie.month12exam.R;
import net.bwie.month12exam.bean.VersionBean;
import net.bwie.month12exam.fragment.NewsFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 12月月考
 * 0、导包 + 布局等初始化操作
 * 1、使用OkHttp + Retrofit + RxJava + Gson下载网络数据
 * 1/1、使用OkHttp获取最新apk版本信息
 * 1/2、请求最新消息和过往消息这2页数据，各自封装在Fragment中，点击不同按钮切换
 * 2、升级版操作：下载的数据直接存入数据库，无论是否有网，展示的数据都从数据库获取，无需判断数据来源
 * 2/1、如果数据模型类格式有难度，我们就手动解析JSON，这样就就可以得到我们想要的数据格式了
 * 3、从数据库读取数据并展示
 * 4、Fresco/Glide展示图片
 * 5、RecyclerView点击事件（以前用interface实现）
 * 5/1、使用EventBus模仿ListView的点击事件：在adapter类中实现了item的点击事件，
 *  使用EventBus将被点击item的位置和对应数据传到fragment/activity中，我们在fragment中接收位置等信息，
 *  并实现跳转界面
 *  5/2、EventBus使用流程：
 *  1、先创建一个事件类，事件中可以封装要传递的数据
 *  2、发送事件携带数据：从适配器的item点击事件中发送
 *  3、实现一个方法，用于接收事件中携带的数据：在fragment中注册EventBus，实现一个方法接收事件
 * 6、点击item，传送item相关数据使用Eventbus
 * 7、跳转详情页，再次获取详情页的数据
 * 8、向上滑动，标题收缩渐变：Coordinator+AppBarLayout+CollapsingToolbarLayout + Toolbar
 * 9、详情页数据？
 * 10、查看新闻真实点赞数？
 * 11、所有UI控件都使用ButterKnife加载
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mLatestBtn;
    protected Button mBeforeBtn;
    protected LinearLayout mTopContainer;
    protected FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        // 获取最新APK版本信息
        getAPKVersionInfo();
        initView();
    }

    private void initView() {
        mLatestBtn = (Button) findViewById(R.id.latest_btn);
        mLatestBtn.setOnClickListener(MainActivity.this);
        mBeforeBtn = (Button) findViewById(R.id.before_btn);
        mBeforeBtn.setOnClickListener(MainActivity.this);
        mTopContainer = (LinearLayout) findViewById(R.id.top_container);
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        // 展示碎片
        showFragment(NewsFragment.TYPE_LATEST);// 默认展示最新消息碎片
    }

    // 获取最新APK版本信息
    private void getAPKVersionInfo() {
        String newVersionUrl = "https://news-at.zhihu.com/api/4/version/android/2.3.0";

        // 创建OkHttp网络请求客户端
        OkHttpClient client = new OkHttpClient();
        // 创建网络请求对象并设置请求地址、请求方式等操作
        Request request = new Request.Builder()
                .url(newVersionUrl)
                .get()// 不写也行，默认就是get请求方式
                .build();
        // 根据请求对象新建请求任务，执行请求
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d("1510", "成功获取数据，线程：" + Thread.currentThread().getName());
                // 坑！OkHttp同样具有Call的请求操作，但数据是在子线程中获取到
                // Retrofit框架封装了OkHttp并进行了线程跳转处理，所以Retrofit获取网络数据在主线程

                // 从响应体中获取json字符串
                ResponseBody body = response.body();
                String json = body.string();

                final VersionBean version = new Gson().fromJson(json, VersionBean.class);

                // 手动跳转到主线程，即可更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("1510", "成功获取数据，线程：" + Thread.currentThread().getName());
                        Toast.makeText(MainActivity.this, version.getLatest() + ", " + version.getMsg(), Toast.LENGTH_LONG)
                                .show();

                        Log.d("1510", version.getLatest());
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.latest_btn) {
            showFragment(NewsFragment.TYPE_LATEST);
        } else if (view.getId() == R.id.before_btn) {
            showFragment(NewsFragment.TYPE_BEFORE);
        }
    }

    // 切换最新消息碎片
    private void showFragment(int type) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, NewsFragment.newInstance(type))
                .commit();
    }

}
