package in.gotiit.bigbro;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import in.gotiit.bigbro.db.Chat;
import in.gotiit.bigbro.db.ChatWithUser;
import in.gotiit.bigbro.util.App;
import in.gotiit.bigbro.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatWithUser> mChats;
    ChatsFragment fragment;
    int qid;

    static final int MY_CHAT = 0;
    static final int THEIR_CHAT = 1;

    public ChatAdapter(ChatsFragment fragment, int qid) {
        this.qid = qid;
        this.fragment = fragment;
        mChats = new ArrayList<>();
        App.instance().getRepository().getChats(qid).observe(fragment, chats -> {
            mChats = chats;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = mChats.get(position);
        if(chat.getUid() == 0) {
            return MY_CHAT;
        }
        return THEIR_CHAT;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = viewType == MY_CHAT ? R.layout.my_message : R.layout.their_message;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChatWithUser item = mChats.get(position);
        holder.mItem = item;
        holder.messageBody.setText(item.getText());
        holder.tvTime.setText(Util.instantToText(item.timeUploaded));
        if (item.uid != 0) {
            if (item.user == null || item.user.username == null) {
                holder.tvName.setText("...");
            } else {
                holder.tvName.setText(Util.atWrapUsername(item.user.username));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView messageBody;
        public Chat mItem;
        public final TextView tvTime;
        public final TextView tvName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            messageBody = view.findViewById(R.id.message_body);
            tvTime = view.findViewById(R.id.time);
            tvName = view.findViewById(R.id.name);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + messageBody.getText() +"'";
        }
    }
}