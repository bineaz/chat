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

    public static final int TEXT  = 11;
    public static final int IMAGE = 12;
    public static final int VOICE = 13;
    @Id
    private long   id;
    private long   chat_id;
    private long   sender_id;
    private long   receiver_id;
    private int    content_type;
    private String content;
    private int    visible;
    private String time_stamp;


    @Generated(hash = 2271208)
    public ChatMessage()
    {
    }


    @Generated(hash = 190037583)
    public ChatMessage(long id, long chat_id, long sender_id, long receiver_id,
                       int content_type, String content, int visible, String time_stamp)
    {
        this.id = id;
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content_type = content_type;
        this.content = content;
        this.visible = visible;
        this.time_stamp = time_stamp;
    }


    @Override
    public int getItemType()
    {
        return 0;
    }


    public long getId()
    {
        return this.id;
    }


    public void setId(long id)
    {
        this.id = id;
    }


    public long getSender_id()
    {
        return this.sender_id;
    }


    public void setSender_id(long sender_id)
    {
        this.sender_id = sender_id;
    }


    public long getReceiver_id()
    {
        return this.receiver_id;
    }


    public void setReceiver_id(long receiver_id)
    {
        this.receiver_id = receiver_id;
    }


    public int getContent_type()
    {
        return this.content_type;
    }


    public void setContent_type(int content_type)
    {
        this.content_type = content_type;
    }


    public String getContent()
    {
        return this.content;
    }


    public void setContent(String content)
    {
        this.content = content;
    }


    public String getTime_stamp()
    {
        return this.time_stamp;
    }


    public void setTime_stamp(String time_stamp)
    {
        this.time_stamp = time_stamp;
    }


    public int getVisible()
    {
        return this.visible;
    }


    public void setVisible(int visible)
    {
        this.visible = visible;
    }

    @Override
    public String toString()
    {
        return "ChatMessage{" +
                "id=" + id +
                ", sender_id=" + sender_id +
                ", receiver_id=" + receiver_id +
                ", content_type=" + content_type +
                ", content='" + content + '\'' +
                ", visible=" + visible +
                ", time_stamp='" + time_stamp + '\'' +
                '}';
    }


    public long getChat_id()
    {
        return this.chat_id;
    }


    public void setChat_id(long chat_id)
    {
        this.chat_id = chat_id;
    }
}
