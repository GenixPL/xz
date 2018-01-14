package pw.xz.xdd.xz.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.TimeUnit;

import pw.xz.xdd.xz.R;

public class Facebook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finishActivity(0);
    }

    public void goBackToMain_But(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finishActivity(0);
    }

}
