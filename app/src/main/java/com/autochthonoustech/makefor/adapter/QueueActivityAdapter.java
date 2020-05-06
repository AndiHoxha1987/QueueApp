package com.autochthonoustech.makefor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.autochthonoustech.makefor.R;
import com.autochthonoustech.makefor.model.People;
import java.util.List;

public class QueueActivityAdapter extends RecyclerView.Adapter<QueueActivityAdapter.FindQueueViewHolder> {
    private final List<People> usersInQueue;

    public QueueActivityAdapter(List<People> usersInQueue) {
        this.usersInQueue = usersInQueue;
    }

    public static class FindQueueViewHolder extends RecyclerView.ViewHolder {
        final TextView userName;
        final TextView userStatus;
        final TextView numberInQueue;


        public FindQueueViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_first_position_name);
            userStatus = itemView.findViewById(R.id.user_first_position_status);
            numberInQueue = itemView.findViewById(R.id.user_first_position);
        }
    }

    @NonNull
    @Override
    public QueueActivityAdapter.FindQueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_display_layout, viewGroup, false);
        return new QueueActivityAdapter.FindQueueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QueueActivityAdapter.FindQueueViewHolder findFriendViewHolder, final int i) {
        final People users = usersInQueue.get(i);
        findFriendViewHolder.userName.setText(users.getName());
        findFriendViewHolder.userStatus.setText(users.getStatus());
        int j = getItemCount();
        String number = (i+1)+" /" +j;
        findFriendViewHolder.numberInQueue.setText(number);
    }


    @Override
    public int getItemCount() {
        return usersInQueue.size();
    }


    public People getItemsAtPosition(int position) {
        return usersInQueue.get(position);
    }

}

