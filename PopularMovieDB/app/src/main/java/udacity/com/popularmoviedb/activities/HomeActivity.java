package udacity.com.popularmoviedb.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import udacity.com.popularmoviedb.R;
import udacity.com.popularmoviedb.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HomeFragment())
                    .commit();
        }
    }
}
