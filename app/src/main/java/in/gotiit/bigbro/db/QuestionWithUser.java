package in.gotiit.bigbro.db;

import androidx.annotation.NonNull;
import androidx.room.Relation;

import java.io.Serializable;

public class QuestionWithUser extends Question implements Serializable {
    @Relation(
            parentColumn = "uid",
            entityColumn = "uid"
    )
    public Contact user; // use uid == 0 before this

    public QuestionWithUser(@NonNull String title) {
        super(title);
    }
}
