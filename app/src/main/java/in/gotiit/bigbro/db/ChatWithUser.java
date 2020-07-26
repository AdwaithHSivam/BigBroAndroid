package in.gotiit.bigbro.db;

import androidx.annotation.NonNull;
import androidx.room.Relation;

public class ChatWithUser extends Chat{

    @Relation(
            parentColumn = "uid",
            entityColumn = "uid"
    )
    public Contact user;

    public ChatWithUser(int qid, @NonNull String text) {
        super(qid, text);
    }
}
