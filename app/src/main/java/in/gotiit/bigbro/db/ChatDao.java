package in.gotiit.bigbro.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class ChatDao {
    @Insert
    public abstract void insert(Chat chat);

    @Transaction
    @Query("SELECT * from chats where qid = :qid")
    public abstract LiveData<List<ChatWithUser>> getChat(int qid);

    @Query("SELECT * from chats where global = :globalId limit 1")
    public abstract Chat getChatGlobal(int globalId);

    @Transaction
    @Query("SELECT * from chats where global = 0")
    public abstract LiveData<List<ChatWithQ>> getChatsToUpload();

    @Query("UPDATE chats set global = :globalId, time = :time where cid = :localId")
    public abstract void updateLocal(int localId, int globalId, String time);

    @Transaction
    public void insertIfNotExist(Chat chat) {
        Chat c = getChatGlobal(chat.globalId);
        if (c == null) {
            insert(chat);
        }
    }
}
