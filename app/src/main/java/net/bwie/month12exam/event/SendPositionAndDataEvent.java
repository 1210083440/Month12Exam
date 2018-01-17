package net.bwie.month12exam.event;

import net.bwie.month12exam.bean.StoriesBean;

public class SendPositionAndDataEvent {

    private int mPosition;
    private StoriesBean mData;

    public SendPositionAndDataEvent(int position, StoriesBean data) {
        mPosition = position;
        mData = data;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public StoriesBean getData() {
        return mData;
    }

    public void setData(StoriesBean data) {
        mData = data;
    }
}
