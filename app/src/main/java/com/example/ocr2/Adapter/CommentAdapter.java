package com.example.ocr2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocr2.Comment;
import com.example.ocr2.R;
import com.example.ocr2.Utils;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.commentHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public commentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new commentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textViewCommentEmail.setText(comment.getEmail());
        holder.textViewCommentContent.setText(comment.getContent());

        if (comment.getImage() != null) {
            holder.imageViewComment.setImageBitmap(Utils.getImage(comment.getImage()));
            holder.imageViewComment.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewComment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class commentHolder extends RecyclerView.ViewHolder {
        TextView textViewCommentEmail, textViewCommentContent;
        ImageView imageViewComment;

        public commentHolder(@NonNull View itemView) {
            super(itemView);
            textViewCommentEmail = itemView.findViewById(R.id.textCommentEmail);
            textViewCommentContent = itemView.findViewById(R.id.textCommentContent);
            imageViewComment = itemView.findViewById(R.id.imageComment);
        }
    }
}
