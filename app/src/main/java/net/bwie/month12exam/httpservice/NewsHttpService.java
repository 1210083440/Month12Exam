package net.bwie.month12exam.httpservice;

import net.bwie.month12exam.bean.NewsBean;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 该接口定义了Retrofit请求网络的方法
 * 配合RxjAVA时，定义的方法返回值类型要求为RxJava的观察者Observable
 * 泛型就是JSON解析后的数据模型类
 */
public interface NewsHttpService {

    // 最新消息的方法
    @GET("api/4/news/latest")
    Observable<NewsBean> getLatestObservable();

    // 过往消息的方法
    @GET("api/4/news/before/20131119")
    Observable<NewsBean> getBeforeObservable();

}
