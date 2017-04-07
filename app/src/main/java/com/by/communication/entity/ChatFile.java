package com.by.communication.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Produced a lot of bug on 2017/4/7.
 */

@Entity
public class ChatFile {
    @Id(autoincrement = true)
    private Long id;

    private int    file_type;
    private String file_name;
    private byte[] value;


    public ChatFile(int file_type, String file_name, byte[] value)
    {
        this.file_type = file_type;
        this.file_name = file_name;
        this.value = value;
    }

    @Generated(hash = 1330691015)
    public ChatFile()
    {
    }

    @Generated(hash = 661822897)
    public ChatFile(Long id, int file_type, String file_name, byte[] value) {
        this.id = id;
        this.file_type = file_type;
        this.file_name = file_name;
        this.value = value;
    }

    public Long getId()
    {
        return id;
    }

    public int getFile_type()
    {
        return file_type;
    }

    public String getFile_name()
    {
        return file_name;
    }

    public void setFile_name(String file_name)
    {
        this.file_name = file_name;
    }

    public byte[] getValue()
    {
        return value;
    }

    public void setValue(byte[] value)
    {
        this.value = value;
    }

    public void setFile_type(int file_type)
    {
        this.file_type = file_type;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ChatFile{" +
                "id=" + id +
                ", file_type=" + file_type +
                ", file_name='" + file_name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }
}
