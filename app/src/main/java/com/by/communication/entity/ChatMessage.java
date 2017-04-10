package com.by.communication.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.by.communication.App;
import com.by.communication.util.TimeUtil;
import com.by.communication.widgit.adapter.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Produced a lot of bug on 2017/3/31.
 */

@Entity
public class ChatMessage implements MultiItemEntity, Parcelable {

    public static final int TEXT  = 11;
    public static final int IMAGE = 12;
    public static final int VOICE = 13;

    public static final int TEXT_SELF  = 101;
    public static final int IMAGE_SELF = 102;
    public static final int VOICE_SELF = 103;

    public static final int TEXT_OTHER  = 201;
    public static final int IMAGE_OTHER = 202;
    public static final int VOICE_OTHER = 203;

    public static final int SENDING      = 21;
    public static final int SEND_SUCCESS = 22;
    public static final int SEND_FAILED  = 23;

    @Id
    @SerializedName("message_id")
    private Long id;

    private long   chat_id;   //聊天组用到
    private long   sender_id;   //发送者id
    private long   receiver_id;  //接受者id
    private int    content_type; //内容类型
    private String content;  //内容
    private String path;
    private int visible = 1;
    private String timestamp;

    private int status = SEND_SUCCESS;  //发送状态

    protected ChatMessage(Parcel in)
    {
        id = in.readLong();
        chat_id = in.readLong();
        sender_id = in.readLong();
        receiver_id = in.readLong();
        content_type = in.readInt();
        content = in.readString();
        visible = in.readInt();
        timestamp = in.readString();
        status = in.readInt();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in)
        {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size)
        {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeLong(id);
        parcel.writeLong(chat_id);
        parcel.writeLong(sender_id);
        parcel.writeLong(receiver_id);
        parcel.writeInt(content_type);
        parcel.writeString(content);
        parcel.writeInt(visible);
        parcel.writeString(timestamp);
        parcel.writeInt(status);
    }

    @IntDef({SENDING, SEND_SUCCESS, SEND_FAILED})
    @interface SendStatus {

    }


    public ChatMessage(long id, long sender_id, long receiver_id, int content_type, String content, String file_name, int status)
    {
        this.id = id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content_type = content_type;
        this.content = content;
        this.path = file_name;
        this.status = status;
        timestamp = TimeUtil.getCurrentTimeString();
    }


    @Generated(hash = 96499247)
    public ChatMessage(Long id, long chat_id, long sender_id, long receiver_id, int content_type, String content, String path,
            int visible, String timestamp, int status) {
        this.id = id;
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content_type = content_type;
        this.content = content;
        this.path = path;
        this.visible = visible;
        this.timestamp = timestamp;
        this.status = status;
    }

    @Generated(hash = 2271208)
    public ChatMessage()
    {
    }


    @Override
    public int getItemType()
    {
        long userId = App.getInstance().getUserId();
        if (userId == sender_id) {
            switch (content_type) {
                case TEXT:
                    return TEXT_SELF;
                case IMAGE:
                    return IMAGE_SELF;
                case VOICE:
                    return VOICE_SELF;
            }
        } else {
            switch (content_type) {
                case TEXT:
                    return TEXT_OTHER;
                case IMAGE:
                    return IMAGE_OTHER;
                case VOICE:
                    return VOICE_OTHER;
            }
        }
        return TEXT_SELF;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(@SendStatus int status)
    {
        this.status = status;
    }

    public Long getId()
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


    public String getTimestamp()
    {
        return this.timestamp;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
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
                ", chat_id=" + chat_id +
                ", sender_id=" + sender_id +
                ", receiver_id=" + receiver_id +
                ", content_type=" + content_type +
                ", content='" + content + '\'' +
                ", path='" + path + '\'' +
                ", visible=" + visible +
                ", timestamp='" + timestamp + '\'' +
                ", status=" + status +
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

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
