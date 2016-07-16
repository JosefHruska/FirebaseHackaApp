package com.example.josefhruska.firebaseapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static final String IDEA_ID = "ideaID";
    private static final String TAG = "MainActivity";
    public static final String IDEAS_CHILD = "ideas";
    private static final int REQUEST_INVITE = 1;
    public static final int RC_NEW_IDEA = 21;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String IDEA_SAVED = "idea_saved";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private RecyclerView mIdeaRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Idea, IdeaViewHolder> mFirebaseAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static class IdeaViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ideaLinearLayout;
        public TextView ideaAuthorTextView;
        public TextView ideaTitleTextView;
        public TextView ideaDescriptionTextView;
        public CircleImageView ideaAuthorImageView;

        public IdeaViewHolder(View v) {
            super(v);
            ideaAuthorTextView = (TextView) itemView.findViewById(R.id.ideaAuthor);
            ideaTitleTextView = (TextView) itemView.findViewById(R.id.ideaTitleTextView);
            ideaDescriptionTextView = (TextView) itemView.findViewById(R.id.ideaDescriptionTextView);
            ideaAuthorImageView = (CircleImageView) itemView.findViewById(R.id.ideaAuthorImageView);

            ideaLinearLayout = (LinearLayout) itemView.findViewById(R.id.itemLinearLayout);
            /*ideaLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent(App.get(), IdeaDetailActivity.class);
                    detailIntent.putExtra(IDEA_ID, idea)
                    detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.get().startActivity(detailIntent);
                }
            });*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIdeaIntent = new Intent(MainActivity.this, NewIdeaActivity.class);
                startActivityForResult(newIdeaIntent, RC_NEW_IDEA);
            }
        });

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mIdeaRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mIdeaRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Idea, IdeaViewHolder>(
                Idea.class,
                R.layout.item_idea,
                IdeaViewHolder.class,
                mFirebaseDatabaseReference.child(IDEAS_CHILD)) {

            @Override
            protected void populateViewHolder(IdeaViewHolder viewHolder,
                                              Idea idea, final int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.ideaAuthorTextView.setText(idea.getAuthorName());
                viewHolder.ideaDescriptionTextView.setText(idea.getDescription());
                viewHolder.ideaTitleTextView.setText(idea.getTitle());
                if (idea.getPhotoUrl() == null) {
                    viewHolder.ideaAuthorImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(MainActivity.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(MainActivity.this)
                            .load(idea.getPhotoUrl())
                            .into(viewHolder.ideaAuthorImageView);
                }

                viewHolder.ideaLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String key = mFirebaseAdapter.getRef(position).getKey();
                        Intent detailIntent = new Intent(MainActivity.this, IdeaDetailActivity.class);
                        detailIntent.putExtra(IDEA_ID, key);
                        startActivity(detailIntent);
                    }
                });
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
                    mIdeaRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mIdeaRecyclerView.setLayoutManager(mLinearLayoutManager);
        mIdeaRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_NEW_IDEA:
                if ( resultCode == RESULT_OK ) {
                    //String title = data.getStringExtra(NewIdeaActivity.TITLE_EXTRA);
                    String title = data.getStringExtra(NewIdeaActivity.TITLE_EXTRA);
                    String description = data.getStringExtra(NewIdeaActivity.DESCRIPTION_EXTRA);

                    Idea idea = new Idea(title, description, mUsername, mPhotoUrl );
                    mFirebaseDatabaseReference.child(IDEAS_CHILD).push().setValue(idea);
                }
                break;
        }
    }
}
