package in.gotiit.bigbro.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity(tableName = "chats")
public class Chat {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cid")
    public int id;//local

    @ColumnInfo(name = "global")
    public int globalId;//global

    @ColumnInfo(name = "qid")
    public int qid;//local

    @ColumnInfo(name = "uid")
    public int uid;//0 for local, else global

    @ColumnInfo(name = "type")
    public int type;//0 default 1 attachment

    @NonNull
    @ColumnInfo(name = "text")
    private String text;

    @NonNull
    @ColumnInfo(name = "time")
    public String timeUploaded;//   in UTC


    public Chat(int qid, @NonNull String text) {
        this.qid = qid;
        this.text = text;
        timeUploaded = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public Chat(int localId, int globalId, int qid, int uid, @NotNull String text, @NotNull String time) {
        this.id = localId;
        this.globalId = globalId;
        this.qid = qid;
        this.uid = uid;
        this.text = text;
        this.timeUploaded = time;
    }

    public static Chat[] getSamples() {
        return new Chat[]{
                new Chat(0,-2, Question.SAMPLE_QID, 0, "What is 1+1",
                        Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
                ),
                new Chat(0,-1, Question.SAMPLE_QID, Contact.MENTOR_UID, "1 + 1 = 2",
                        Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
                )
        };
    }

    @NotNull
    public String getText(){return this.text;}

    public String getTime() {
        return timeUploaded;
    }

    public int getType() { return type;}

    public int getUid() { return uid;}
}
