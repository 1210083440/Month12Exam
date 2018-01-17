package net.bwie.month12exam.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class StoriesBean {

    // 下面的id表达的含义是内容，也就是文章id，还需要创建真正意义的主键_id
    @Id(autoincrement = true)
    private long _id;

    private int id;// 文章id
    private String title;
//    private List<String> images;// 原始数据格式，使用List集合会产生2张表的管理操作，有点难

    private String imge;// 即将使用手动JSON解析方式，可以产生我们要求的数据格式：只有1个url地址就够了

    @Generated(hash = 1675582365)
    public StoriesBean(long _id, int id, String title, String imge) {
        this._id = _id;
        this.id = id;
        this.title = title;
        this.imge = imge;
    }

    @Generated(hash = 929118848)
    public StoriesBean() {
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImge() {
        return this.imge;
    }

    public void setImge(String imge) {
        this.imge = imge;
    }

}