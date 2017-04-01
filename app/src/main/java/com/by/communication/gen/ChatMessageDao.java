package com.by.communication.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.by.communication.entity.ChatMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_MESSAGE".
*/
public class ChatMessageDao extends AbstractDao<ChatMessage, Long> {

    public static final String TABLENAME = "CHAT_MESSAGE";

    /**
     * Properties of entity ChatMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Sender_id = new Property(1, long.class, "sender_id", false, "SENDER_ID");
        public final static Property Receiver_id = new Property(2, long.class, "receiver_id", false, "RECEIVER_ID");
        public final static Property Content_type = new Property(3, int.class, "content_type", false, "CONTENT_TYPE");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property Visible = new Property(5, boolean.class, "visible", false, "VISIBLE");
        public final static Property Time_stamp = new Property(6, String.class, "time_stamp", false, "TIME_STAMP");
    }


    public ChatMessageDao(DaoConfig config) {
        super(config);
    }
    
    public ChatMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"SENDER_ID\" INTEGER NOT NULL ," + // 1: sender_id
                "\"RECEIVER_ID\" INTEGER NOT NULL ," + // 2: receiver_id
                "\"CONTENT_TYPE\" INTEGER NOT NULL ," + // 3: content_type
                "\"CONTENT\" TEXT," + // 4: content
                "\"VISIBLE\" INTEGER NOT NULL ," + // 5: visible
                "\"TIME_STAMP\" TEXT);"); // 6: time_stamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_MESSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatMessage entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSender_id());
        stmt.bindLong(3, entity.getReceiver_id());
        stmt.bindLong(4, entity.getContent_type());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
        stmt.bindLong(6, entity.getVisible() ? 1L: 0L);
 
        String time_stamp = entity.getTime_stamp();
        if (time_stamp != null) {
            stmt.bindString(7, time_stamp);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatMessage entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSender_id());
        stmt.bindLong(3, entity.getReceiver_id());
        stmt.bindLong(4, entity.getContent_type());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
        stmt.bindLong(6, entity.getVisible() ? 1L: 0L);
 
        String time_stamp = entity.getTime_stamp();
        if (time_stamp != null) {
            stmt.bindString(7, time_stamp);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public ChatMessage readEntity(Cursor cursor, int offset) {
        ChatMessage entity = new ChatMessage( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // sender_id
            cursor.getLong(offset + 2), // receiver_id
            cursor.getInt(offset + 3), // content_type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
            cursor.getShort(offset + 5) != 0, // visible
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // time_stamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatMessage entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setSender_id(cursor.getLong(offset + 1));
        entity.setReceiver_id(cursor.getLong(offset + 2));
        entity.setContent_type(cursor.getInt(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setVisible(cursor.getShort(offset + 5) != 0);
        entity.setTime_stamp(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatMessage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatMessage entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
