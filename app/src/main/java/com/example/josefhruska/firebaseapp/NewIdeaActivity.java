package com.example.josefhruska.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class NewIdeaActivity extends AppCompatActivity {

    public static final String TITLE_EXTRA = "titleExtra";
    public static final String DESCRIPTION_EXTRA = "descriptionExtra";

    private TextView mIdeaTitle;
    private TextView mIdeaDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);

        mIdeaTitle = (TextView) findViewById(R.id.ideaTitle);
        mIdeaDescription = (TextView) findViewById(R.id.ideaDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_idea, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.add_idea:
                Intent data = getIntent();
                data.putExtra(TITLE_EXTRA, mIdeaTitle.getText().toString());
                data.putExtra(DESCRIPTION_EXTRA, mIdeaDescription.getText().toString());
                setResult(RESULT_OK, data);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
