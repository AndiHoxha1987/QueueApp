package com.autochthonoustech.makefor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autochthonoustech.makefor.QueueActivity;
import com.autochthonoustech.makefor.R;
import com.autochthonoustech.makefor.model.Queue;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_ID;
import static com.autochthonoustech.makefor.MainActivity.INTENT_EXTRA_QUEUE_NAME;

public class MyQueueAdapter extends RecyclerView.Adapter<MyQueueAdapter.FindQueueViewHolder> {


    private final List<Queue> userSearch;
    private final Context mContext;
    private String currentUser;

    public MyQueueAdapter(List<Queue> userSearch, Context context) {
        this.userSearch = userSearch;
        this.mContext = context;
    }

    public static class FindQueueViewHolder extends RecyclerView.ViewHolder {
        final TextView userName;
        final TextView userStatus;
        final Button addFriends;

        public FindQueueViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            addFriends = itemView.findViewById(R.id.add_friend_button);
        }
    }

    @NonNull
    @Override
    public MyQueueAdapter.FindQueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.queue_display_layout, viewGroup, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return new MyQueueAdapter.FindQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyQueueAdapter.FindQueueViewHolder findFriendViewHolder, final int i) {
        final Queue queue = userSearch.get(i);
        final String id = queue.getGroupId();
        final String name = queue.getName();
        findFriendViewHolder.userName.setText(queue.getName());
        findFriendViewHolder.userStatus.setText(queue.getStatus());
        findFriendViewHolder.addFriends.setVisibility(View.GONE);
        if (currentUser.equals(queue.getAdmin())) {
            findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(mContext, QueueActivity.class);
                    chatIntent.putExtra(INTENT_EXTRA_QUEUE_ID, id);
                    chatIntent.putExtra(INTENT_EXTRA_QUEUE_NAME, name);
                    mContext.startActivity(chatIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userSearch.size();
    }


}

