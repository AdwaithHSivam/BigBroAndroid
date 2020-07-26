package in.gotiit.bigbro.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Question.class, Chat.class, Contact.class}, version = 2, exportSchema = false)
public abstract class RoomDb extends RoomDatabase {

    public abstract QuestionDao questionDao();
    public abstract ChatDao chatDao();
    public abstract ContactDao contactDao();

    private static volatile RoomDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static RoomDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDb.class, "main_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void firstTime() {
        INSTANCE.clearAllTables();
    }

}
