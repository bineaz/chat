package com.by.communication.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Produced a lot of bug on 2017/4/5.
 */

@Entity
public class Friend {
    @Id
    private long id;

    private long   owner_id;
    private long   friend_id;
    private String timestamp;

    @Transient
    private String username;
    @Transient
    private String nickname;
    @Transient
    private String avatar;
    @Transient
    private String phone;
    @Transient
    private int    gender;

    @Generated(hash = 2078754734)
    public Friend(long id, long owner_id, long friend_id, String timestamp)
    {
        this.id = id;
        this.owner_id = owner_id;
        this.friend_id = friend_id;
        this.timestamp = timestamp;
    }

    @Generated(hash = 287143722)
    public Friend()
    {
    }

    @Override
    public String toString()
    {
        return "Friend{" +
                "id=" + id +
                ", owner_id=" + owner_id +
                ", friend_id=" + friend_id +
                ", timestamp='" + timestamp + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                '}';
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getOwner_id()
    {
        return this.owner_id;
    }

    public void setOwner_id(long owner_id)
    {
        this.owner_id = owner_id;
    }

    public long getFriend_id()
    {
        return this.friend_id;
    }

    public void setFriend_id(long friend_id)
    {
        this.friend_id = friend_id;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public User convertToUser()
    {
        return new User(
                friend_id,
                username,
                null,
                nickname,
                avatar,
                phone,
                gender
        );
    }
}
