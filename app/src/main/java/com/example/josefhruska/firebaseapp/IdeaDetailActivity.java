package com.example.josefhruska.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import de.hdodenhof.circleimageview.CircleImageView;

public class IdeaDetailActivity extends AppCompatActivity {

    private static final String CONTRIBUTORS_CHILD = "contributors";

    private RecyclerView contributorsRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Contributor, ContributorViewHolder> mFirebaseAdapter;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private String key;
    private String mUsername;
    private String mPhotoUrl;

    private CircleImageView authorImage;
    private TextView authorName;
    private TextView ideaTitle;
    private TextView ideaDescription;

    public static class ContributorViewHolder extends RecyclerView.ViewHolder {
        public TextView contributorNameTV;
        public CircleImageView contributorImageView;

        public ContributorViewHolder(View v) {
            super(v);
            contributorImageView = (CircleImageView) itemView.findViewById(R.id.contributorImage);
            contributorNameTV = (TextView) itemView.findViewById(R.id.contributorName);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_detail);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if ( mFirebaseUser == null ) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        authorImage = (CircleImageView) findViewById(R.id.authorImage);
        authorName = (TextView) findViewById(R.id.ideaAuthor);
        ideaTitle = (TextView) findViewById(R.id.ideaTitle);
        ideaDescription = (TextView) findViewById(R.id.ideaDescription);

        if (mPhotoUrl == null) {
            authorImage.setImageDrawable(ContextCompat
                            .getDrawable(IdeaDetailActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(IdeaDetailActivity.this)
                    .load(mPhotoUrl)
                    .into(authorImage);
        }

        authorName.setText(mUsername);

        key = getIntent().getStringExtra(MainActivity.IDEA_ID);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("ideas").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Idea idea = dataSnapshot.getValue(Idea.class);
                ideaTitle.setText(idea.getTitle());
                ideaDescription.setText(idea.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        contributorsRecyclerView = (RecyclerView) findViewById(R.id.contributorsList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        contributorsRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Contributor, ContributorViewHolder>(
                Contributor.class,
                R.layout.item_contributor,
                ContributorViewHolder.class,
                mFirebaseDatabaseReference.child("ideas").child(key).child(CONTRIBUTORS_CHILD)) {

            @Override
            protected void populateViewHolder(ContributorViewHolder viewHolder,
                                              Contributor contributor, final int position) {
                //ProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.contributorNameTV.setText(contributor.getName());
                if (contributor.getPhotoUrl() == null) {
                    viewHolder.contributorImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(IdeaDetailActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(IdeaDetailActivity.this)
                            .load(contributor.getPhotoUrl())
                            .into(viewHolder.contributorImageView);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    contributorsRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        contributorsRecyclerView.setLayoutManager(mLinearLayoutManager);
        contributorsRecyclerView.setAdapter(mFirebaseAdapter);


        /*mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mIdeaRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mIdeaRecyclerView.setLayoutManager(mLinearLayoutManager);*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_idea_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.action_join_idea:
                Contributor con = new Contributor(mUsername, mPhotoUrl);
                mFirebaseDatabaseReference.child(MainActivity.IDEAS_CHILD)
                        .child(key).child(CONTRIBUTORS_CHILD).push().setValue(con);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
