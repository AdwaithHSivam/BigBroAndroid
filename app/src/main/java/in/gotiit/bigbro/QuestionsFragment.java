package in.gotiit.bigbro;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import in.gotiit.bigbro.db.QuestionWithUser;


public class QuestionsFragment extends Fragment {

    public static final String SOURCE = "src";
    public static final int SOURCE_HOME = 0;
    public static final int SOURCE_FEED = 1;

    public MainActivity mainActivity;

    public int source = SOURCE_HOME;

    public QuestionsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            source = getArguments().getInt(SOURCE);//defaults to 0
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.questions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new QuestionAdapter(this, source));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> NavHostFragment.findNavController(QuestionsFragment.this)
                .navigate(R.id.action_QuestionsFragment_to_addQuestionFragment));
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    public void onListFragmentInteraction(QuestionWithUser item) {
        Bundle args = new Bundle();
        args.putSerializable(ChatsFragment.QUESTION, item);
        NavHostFragment.findNavController(QuestionsFragment.this)
                .navigate(R.id.action_QuestionsFragment_to_ChatFragment,args);
    }

}
