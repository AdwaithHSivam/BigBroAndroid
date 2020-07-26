package in.gotiit.bigbro.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity(tableName = "questions")
public class Question implements Serializable {

    static public final int SAMPLE_QID = 1;

    static public final int STATUS_OPEN = 0;
    static public final int STATUS_CLOSED = 1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "qid")
    public int id;//local

    @ColumnInfo(name = "global")
    public int globalId;//global

    @ColumnInfo(name = "uid")
    public int uid;//0 for local else global

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "mid")
    public Integer mentorId;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "time")
    public String timeUploaded;//   in UTC



    public Question(@NonNull String title) {// for local questions
        this.title = title;
        timeUploaded = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public Question(int localId, int globalId, int uid, @NonNull String title,
            int status, Integer mid, @NonNull String time) { //for global questions
        this.id = localId;
        this.globalId = globalId;
        this.uid = uid;
        this.title = title;
        this.status = status;
        this.mentorId = mid;
        this.timeUploaded = time;
    }

    public static Question getSample() {
        Question question = new Question(
                0,-1, 0,
                "Sample Question",
                STATUS_CLOSED,
                Contact.MENTOR_UID,
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        question.id = SAMPLE_QID;
        return question;
    }


    @NonNull
    public String getTitle(){return title;}

    public String getTime() {
        return timeUploaded;
    }

}
