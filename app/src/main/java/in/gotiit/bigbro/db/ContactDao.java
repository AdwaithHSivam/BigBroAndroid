package in.gotiit.bigbro.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Contact contact);

    @Query("UPDATE contacts set username = :username where uid = :uid")
    void setUsername(int uid, String username);

    @Query("SELECT * from contacts where username is null")
    LiveData<List<Contact>> getContactsToFetch();

}
