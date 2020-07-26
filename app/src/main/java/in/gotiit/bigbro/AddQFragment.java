package in.gotiit.bigbro;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import in.gotiit.bigbro.db.Question;
import in.gotiit.bigbro.util.App;


public class AddQFragment extends Fragment {

    View view;
    private EditText titleEt;
    private MainActivity mainActivity;

    public AddQFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_add_q, container, false);
        titleEt = view.findViewById(R.id.et_q_title);
        TextView btnEnter = view.findViewById(R.id.tv_btn_add_q);
        ProgressBar mProgress = view.findViewById(R.id.add_q_progress);
        mProgress.setVisibility(View.GONE);
        btnEnter.setOnClickListener(v -> addNewQuestion());
        return view;
    }

    public void addNewQuestion(){
        String title = titleEt.getText().toString();
        Question q = new Question(title);
        App.instance().getRepository().insertQuestion(q);
        //return back (and progress)
        if (mainActivity != null) {
            try {
                InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (Exception ignored) {}

            NavHostFragment.findNavController(AddQFragment.this).navigate(R.id.action_addQuestionFragment_to_QuestionsFragment);
        }
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

}