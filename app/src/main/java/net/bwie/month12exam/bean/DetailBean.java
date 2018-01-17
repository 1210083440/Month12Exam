package net.bwie.month12exam.bean;

/**
 * 详情页的假数据
 */
public class DetailBean {

    private String title;
    private String title_bg_url;
    private String htmlCode;

    private int comments;// 评论数

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_bg_url() {
        return title_bg_url;
    }

    public void setTitle_bg_url(String title_bg_url) {
        this.title_bg_url = title_bg_url;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public void setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    private int like;// 点赞数

}
