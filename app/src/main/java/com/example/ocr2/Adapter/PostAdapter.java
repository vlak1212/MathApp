package com.example.ocr2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocr2.Post;
import com.example.ocr2.PostDetails;
import com.example.ocr2.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int pos) {
        Post post = postList.get(pos);
        holder.textViewPostTitle.setText(post.getTitle());
        holder.textViewPostEmail.setText(post.getEmail());
        holder.textViewPostContent.setText(post.getContent());

        if (post.getImage() != null) {
            holder.imageViewPost.setImageBitmap(com.example.ocr2.Utils.getImage(post.getImage()));
            holder.imageViewPost.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewPost.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetails.class);
            intent.putExtra("postId", post.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPostTitle, textViewPostEmail, textViewPostContent;
        ImageView imageViewPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPostTitle = itemView.findViewById(R.id.PostTitle);
            textViewPostEmail = itemView.findViewById(R.id.PostEmail);
            textViewPostContent = itemView.findViewById(R.id.PostContent);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
        }
    }
}
