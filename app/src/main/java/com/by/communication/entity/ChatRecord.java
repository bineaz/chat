package com.by.communication.entity;

import org.greenrobot.greendao.annotation.Transient;

/**
 * Produced a lot of bug on 2017/4/5.
 */

public class ChatRecord {
    long id;
    long chat_id;
    long creator;


    @Transient
    ChatMessage last_chat_message;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getChat_id()
    {
        return chat_id;
    }

    public void setChat_id(long chat_id)
    {
        this.chat_id = chat_id;
    }

    public ChatMessage getLast_chat_message()
    {
        return last_chat_message;
    }

    public void setLast_chat_message(ChatMessage last_chat_message)
    {
        this.last_chat_message = last_chat_message;
    }

    @Override
    public String toString()
    {
        return "ChatRecord{" +
                "id=" + id +
                ", chat_id=" + chat_id +
                ", last_chat_message=" + last_chat_message +
                '}';
    }
}
