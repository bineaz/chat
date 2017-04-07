package com.by.communication.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.by.communication.entity.ChatFile;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_FILE".
*/
public class ChatFileDao extends AbstractDao<ChatFile, Long> {

    public static final String TABLENAME = "CHAT_FILE";

    /**
     * Properties of entity ChatFile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property File_type = new Property(1, int.class, "file_type", false, "FILE_TYPE");
        public final static Property File_name = new Property(2, String.class, "file_name", false, "FILE_NAME");
        public final static Property Value = new Property(3, byte[].class, "value", false, "VALUE");
    }


    public ChatFileDao(DaoConfig config) {
        super(config);
    }
    
    public ChatFileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FILE_TYPE\" INTEGER NOT NULL ," + // 1: file_type
                "\"FILE_NAME\" TEXT," + // 2: file_name
                "\"VALUE\" BLOB);"); // 3: value
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_FILE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getFile_type());
 
        String file_name = entity.getFile_name();
        if (file_name != null) {
            stmt.bindString(3, file_name);
        }
 
        byte[] value = entity.getValue();
        if (value != null) {
            stmt.bindBlob(4, value);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getFile_type());
 
        String file_name = entity.getFile_name();
        if (file_name != null) {
            stmt.bindString(3, file_name);
        }
 
        byte[] value = entity.getValue();
        if (value != null) {
            stmt.bindBlob(4, value);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChatFile readEntity(Cursor cursor, int offset) {
        ChatFile entity = new ChatFile( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // file_type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // file_name
            cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3) // value
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatFile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFile_type(cursor.getInt(offset + 1));
        entity.setFile_name(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setValue(cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatFile entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatFile entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatFile entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
