package in.gotiit.bigbro.db;

import androidx.annotation.NonNull;
import androidx.room.Relation;

public class ChatWithQ extends Chat{

    @Relation(
            parentColumn = "qid",
            entityColumn = "qid"
    )
    public Question question;

    public ChatWithQ(int qid, @NonNull String text) {
        super(qid, text);
    }
}
