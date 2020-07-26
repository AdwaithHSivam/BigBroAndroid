package in.gotiit.bigbro;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import in.gotiit.bigbro.db.QuestionWithUser;
import in.gotiit.bigbro.util.App;
import in.gotiit.bigbro.util.Util;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<QuestionWithUser> mQuestions;
    QuestionsFragment fragment;

    public QuestionAdapter(QuestionsFragment fragment, int source) {
        this.fragment = fragment;
        mQuestions = new ArrayList<>();
        App.instance().getRepository().getQuestions(source).observe(fragment, questions -> {
            mQuestions = questions;
            notifyDataSetChanged();
        });
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mQuestions.get(position);
        holder.mTitleView.setText(mQuestions.get(position).getTitle());
        holder.mContentView.setText("I haven't decided what to put here " + mQuestions.get(position).globalId);
        String time = Util.instantToText(holder.mItem.getTime());
        holder.mTimeView.setText(time);

        holder.mView.setOnClickListener(v -> {
            if (null != fragment) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                fragment.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final TextView mTimeView;
        public QuestionWithUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.q_title);
            mContentView = view.findViewById(R.id.q_content);
            mTimeView = view.findViewById(R.id.time);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
