package com.elegion.myfirstapplication.comments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.myfirstapplication.R;
import com.elegion.myfirstapplication.Utils;
import com.elegion.myfirstapplication.model.Comment;


public class CommentsHolder extends RecyclerView.ViewHolder {

    private TextView mAuthor;
    private TextView mText;
    private TextView mDateTime;

    public CommentsHolder(View itemView) {
        super(itemView);
        mAuthor = itemView.findViewById(R.id.tv_author);
        mText = itemView.findViewById(R.id.tv_text);
        mDateTime = itemView.findViewById(R.id.tv_date_time);
    }

    public void bind(Comment item) {
        mAuthor.setText(item.getAuthor());
        mText.setText(item.getText());
        mDateTime.setText(Utils.getFormattedDate(item.getTimestamp()));
    }


}
