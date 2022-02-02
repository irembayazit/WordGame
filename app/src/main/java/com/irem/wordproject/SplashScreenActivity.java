package com.irem.wordproject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
//this = getApplicationContext()
public class SplashScreenActivity extends AppCompatActivity {

    FirebaseDatabase db;
    private TextView mTextView;
    private float maksimumProgres = 100f, artacakProgress, progresMiktari = 0;
    static public HashMap<String, String> sorularHashmap;
    private MediaPlayer gameTheme;

    private SharedPreferences preferences;
    private boolean muzikDurumu;
    private FirebaseFirestore mFirestore;
    private ProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mTextView = (TextView)findViewById(R.id.splash_screen_activity_textViewState);
        mProgress = (ProgressBar)findViewById(R.id.splash_screen_activity_progressBar);
        sorularHashmap = new HashMap<>();
        gameTheme = MediaPlayer.create(this, R.raw.gametheme);
        gameTheme.setLooping(true);

        preferences = this.getSharedPreferences("package com.example.wordgame", MODE_PRIVATE);
        muzikDurumu = preferences.getBoolean("muzikDurumu", true);

        mProgress.setProgress(100);

        mTextView.setText("Sorular Alındı, Uygulama Başlatılıyor...");

        new CountDownTimer(1100, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (muzikDurumu)
            gameTheme.start();
    }



}