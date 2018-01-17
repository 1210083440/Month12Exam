package net.bwie.month12exam.retrofit;

import android.support.annotation.Nullable;

import net.bwie.month12exam.bean.NewsBean;
import net.bwie.month12exam.bean.StoriesBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义JSON解析工厂
 * JSON数据来自网络的响应，所以我们需要重写响应体装换器，将JSON字符串人为进行解析操作并返回
 */
public class MyJSONFactory extends Converter.Factory {

    @Nullable// 选择安卓包的注解
    // 泛型一：原始的响应体，包含JSON字符串
    // 泛型二：指定我们解析后的数据类型
    @Override
    public Converter<ResponseBody, NewsBean> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new Converter<ResponseBody, NewsBean>() {
            @Override
            public NewsBean convert(ResponseBody body) throws IOException {
                // 使用JSONObject、JSONArray解析数据并手动封装
                String json = body.string();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    NewsBean news = new NewsBean();// 根数据模型类

                    // 获取JSON对象中的JSON数组
                    JSONArray storiesArray = jsonObject.getJSONArray("stories");
                    List<StoriesBean> storiesList = new ArrayList<>();

                    // 使用for循环封装JSON数组中的全部数据
                    for (int i = 0; i < storiesArray.length(); i++) {
                        // 获取JSON数组中的大括号对象，就是storiesbean
                        JSONObject storiesObject = storiesArray.getJSONObject(i);
                        StoriesBean story = new StoriesBean();

                        // stories对象中有一个JSON数组叫images，我们要提取出这个数组中唯一的字符串
                        JSONArray imagesArray = storiesObject.getJSONArray("images");
                        String imagesString = imagesArray.getString(0);
                        // 封装至我们的storiesbean中
                        story.setImge(imagesString);

                        // title可以直接通过story对象提取出来并封装
                        String title = storiesObject.getString("title");
                        story.setTitle(title);

                        int storyId = storiesObject.getInt("id");
                        story.setId(storyId);

                        // 将每一个story封装到集合中
                        storiesList.add(story);
                    }
                    // 循环后，将sotries集合封装到根数据模型类中
                    news.setStories(storiesList);
                    return news;
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

        };
    }
}
