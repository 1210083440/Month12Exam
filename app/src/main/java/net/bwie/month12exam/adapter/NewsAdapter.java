package net.bwie.month12exam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.bwie.month12exam.R;
import net.bwie.month12exam.bean.StoriesBean;
import net.bwie.month12exam.event.SendPositionAndDataEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private List<StoriesBean> mDatas;

    public NewsAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    // 添加数据的操作
    public void addDatas(List<StoriesBean> stories) {
        mDatas.addAll(stories);
        // 一定要刷新界面
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_news, parent, false);
        // 初始化itemView的点击事件
        initItemListener(itemView, parent);
        return new ViewHolder(itemView);
    }

    // 初始化itemView的点击事件
    // 点击item，咱得能知道被点击item的位置，和item对应的数据
    private void initItemListener(View itemView, ViewGroup parent) {
        // 获取item的父容器RecyclerView，用于监测被点击item的位置
        final RecyclerView parentRecyclerView = (RecyclerView) parent;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 父容器获取子控件在布局中的【位置】
                int position = parentRecyclerView.getChildLayoutPosition(v);
                StoriesBean story = mDatas.get(position);

//                // 跳转第二页，咱不这么干
//                Intent intent = new Intent(mContext, DetailActivity.class);
//                mContext.startActivity(intent);

                // 使用EventBus将数据发送到Fragment中
                EventBus.getDefault().post(new SendPositionAndDataEvent(position, story));
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StoriesBean story = mDatas.get(position);

        holder.mTitleTextView.setText(story.getTitle());

        // 创建Glide配置对象，设置圆图
        RequestOptions options = new RequestOptions()
                .circleCrop();

        Glide.with(mContext)
                .load(story.getImge())
                .apply(options)
                .into(holder.mPicImageView);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_tv)
        TextView mTitleTextView;
        @BindView(R.id.pic_iv)
        ImageView mPicImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
