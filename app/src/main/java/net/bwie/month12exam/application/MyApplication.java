package net.bwie.month12exam.application;

import android.app.Application;

import net.bwie.month12exam.bean.DaoMaster;
import net.bwie.month12exam.bean.DaoSession;
import net.bwie.month12exam.retrofit.MyJSONFactory;

import org.greenrobot.greendao.database.Database;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MyApplication extends Application {

    private static Retrofit sRetrofit;

    private static DaoSession sDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        initRetrofit();

        initGreenDAO();
    }

    // 参考安卓系统原生的数据库操作方式
    // 1、准备数据库助手SQLiteOpenHelper
    // 1/1、在助手中指定了：数据库文件名，数据库版本号
    // 1/2、初始化数据库时要建表，于是就准备了表名
    // 2、使用助手帮我们创建数据库对象db
    // 3、用db去操作数据库：增删改查

    // 对比greenDAO
    // 0、不再使用数据库助手创建表，根据面向对象思想，把表 -> 类，把列名 -> 类中的属性
    // 应该先去封装数据模型类，添加必要的注解例如表，主键等，make以下工程自动生成必要代码
    // 1、也要创建数据库助手DevOpenHelper
    // 1/1、指定了数据库文件名。默认版本号为1
    // 2、用助手帮我们创建数据库对象，注意：要使用greenDAO框架封装好的数据库对象
    // 3、对比原生操作额外的一项：每次操作数据库其实就是一次和数据库交互的行为，也就是和数据库的一次会话Session
    // 4、在会话中执行具体的操作行为：增删改查（面向对象思想的操作）
    private void initGreenDAO() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "zhihu.db");
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();// greenDAO框架主干类根据db对象创建的回话
    }

    // 初始化网络请求框架Retrofit
    private void initRetrofit() {
        sRetrofit = new Retrofit.Builder()
                .baseUrl("https://news-at.zhihu.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())// 自动解析
                .addConverterFactory(new MyJSONFactory())// 我们自己定义解析工厂去解析响应中的JSON数据
                .build();
    }

    public static Retrofit getRetrofit() {
        return sRetrofit;
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}
