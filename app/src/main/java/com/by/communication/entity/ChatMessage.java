package com.by.communication.entity;

import com.by.communication.widgit.adapter.entity.MultiItemEntity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Produced a lot of bug on 2017/3/31.
 */

@Entity
public class ChatMessage implements MultiItemEntity {

    @Id
    private long    id;
    private long    sender_id;
    private long    receiver_id;
    private int     content_type;
    private String  content;
    private boolean visible;
    private String  time_stamp;


    @Generated(hash = 135759098)
    public ChatMessage(long id, long sender_id, long receiver_id, int content_type,
            String content, boolean visible, String time_stamp) {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content_type = content_type;
        this.content = content;
        this.visible = visible;
        this.time_stamp = time_stamp;
    }


    @Generated(hash = 2271208)
    public ChatMessage() {
    }


    @Override
    public int getItemType()
    {
        return 0;
    }


    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public long getSender_id() {
        return this.sender_id;
    }


    public void setSender_id(long sender_id) {
        this.sender_id = sender_id;
    }


    public long getReceiver_id() {
        return this.receiver_id;
    }


    public void setReceiver_id(long receiver_id) {
        this.receiver_id = receiver_id;
    }


    public int getContent_type() {
        return this.content_type;
    }


    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public boolean getVisible() {
        return this.visible;
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public String getTime_stamp() {
        return this.time_stamp;
    }


    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
