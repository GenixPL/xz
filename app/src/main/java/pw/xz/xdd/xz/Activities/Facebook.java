package pw.xz.xdd.xz.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import pw.xz.xdd.xz.R;

public class Facebook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e){

        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
