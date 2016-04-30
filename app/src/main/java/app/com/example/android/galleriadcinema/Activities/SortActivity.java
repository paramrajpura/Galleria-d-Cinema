package app.com.example.android.galleriadcinema.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.com.example.android.galleriadcinema.Fragments.SortFragment;

public class SortActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SortFragment())
                .commit();
    }
}
