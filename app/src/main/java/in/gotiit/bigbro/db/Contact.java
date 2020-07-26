package in.gotiit.bigbro.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "contacts")
public class Contact implements Serializable {

    static public final int MENTOR_UID = 5;


    @PrimaryKey()
    @ColumnInfo(name = "uid")
    public int id;//global


    @ColumnInfo(name = "username")
    public String username;

    public Contact(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public static Contact getSample() {
        return new Contact(MENTOR_UID, "mentor");
    }
}
