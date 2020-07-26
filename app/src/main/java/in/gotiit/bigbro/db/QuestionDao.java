package in.gotiit.bigbro.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class QuestionDao {

    @Insert
    public abstract void insert(Question question);//only for local

    @Query("UPDATE questions set global = :globalId, mid = :mid, status = :status, time = :time where qid = :localId")
    public abstract void updateLocal(int localId, int globalId, Integer mid, int status, String time);

    @Query("UPDATE questions set mid = :mid, status = :status where global = :globalId")
    public abstract void updateGlobal(int globalId, Integer mid, int status);

    @Transaction
    public void updateOrInsertGlobal(Question question) {
        Question q = getQuestionGlobal(question.globalId);
        if (q == null) {
            insert(question);
        } else {
            updateGlobal(question.globalId, question.mentorId, question.status);
        }
    }

    @Transaction
    @Query("SELECT * from questions order by qid desc")
    public abstract LiveData<List<QuestionWithUser>> getQuestions();

    @Transaction
    @Query("SELECT * from questions where uid = 0 or mid = 0 order by qid desc")
    public abstract LiveData<List<QuestionWithUser>> getHomeQuestions();

    @Transaction
    @Query("SELECT * from questions where uid = 0 order by qid desc")
    public abstract LiveData<List<QuestionWithUser>> getLocalQuestions();

    @Transaction
    @Query("SELECT * from questions where uid != 0 order by qid desc")
    public abstract LiveData<List<QuestionWithUser>> getFeedQuestions();

    @Transaction
    @Query("SELECT * from questions where mid = 0 order by qid desc")
    public abstract LiveData<List<QuestionWithUser>> getMentorQuestions();

    //Question.STATUS_OPEN = 0
    @Query("SELECT global from questions where status = 0 and global != 0")
    public abstract List<Integer> getOpenQIds();

    @Query("SELECT * from questions where qid = :qid limit 1")
    public abstract Question getQuestionLocal(int qid);

    @Query("SELECT * from questions where global = :globalId limit 1")
    public abstract Question getQuestionGlobal(int globalId);

    @Transaction
    @Query("SELECT * from questions where qid = :qid")
    public abstract LiveData<QuestionWithUser> getLiveQuestion(int qid);

    @Query("SELECT global from questions where global in (:list)")
    public abstract List<Integer> getExistingQFrom(List<Integer> list);

    @Query("SELECT * from questions where global = 0")
    public abstract LiveData<List<Question>> getQuestionsToUpload();

    @Query("SELECT qid from questions where global = :globalId limit 1")
    public abstract Integer getLocalQid(int globalId);
}
