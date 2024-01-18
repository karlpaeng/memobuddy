package mad.memobuddy.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScr extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scr);
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashScr.this, R.color.darkgray));
        getWindow().setNavigationBarColor(ContextCompat.getColor(SplashScr.this, R.color.darkgray));
        new Handler().postDelayed(() -> {
            Intent appContent = new Intent(SplashScr.this, Home.class);
            appContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appContent);
            finish();
        }, 1000);
    }
}