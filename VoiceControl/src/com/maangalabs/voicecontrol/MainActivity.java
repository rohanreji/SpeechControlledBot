package com.maangalabs.voicecontrol;

 
import java.util.ArrayList;
import java.util.Locale;
 
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
 
    private TextView txtSpeechInput,t1,t2;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        t1=(TextView) findViewById(R.id.textView2);
        t2=(TextView) findViewById(R.id.textView1);
        Typeface font = Typeface.createFromAsset(getAssets(), "demo.otf");
        t1.setTypeface(font);
        t2.setTypeface(font);
        txtSpeechInput.setTypeface(font);
        
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        
 
        // hide the action bar
     //   getActionBar().hide();
 
        btnSpeak.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
 
    }
 
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
 
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case REQ_CODE_SPEECH_INPUT: {
            if (resultCode == RESULT_OK && null != data) {
 
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtSpeechInput.setText(result.get(0));
                if(result.get(0).contains("up"))
                {
                	Toast.makeText(getApplicationContext(), "bot moving up", Toast.LENGTH_SHORT).show();
                }
                else if(result.get(0).contains("back"))
                {
                	Toast.makeText(getApplicationContext(), "bot moving down", Toast.LENGTH_SHORT).show();
                }
                else if(result.get(0).contains("right"))
                {
                	Toast.makeText(getApplicationContext(), "bot moving right", Toast.LENGTH_SHORT).show();
                }
                else if(result.get(0).contains("left"))
                {
                	Toast.makeText(getApplicationContext(), "bot moving left", Toast.LENGTH_SHORT).show();
                }
                else
                {
                	Toast.makeText(getApplicationContext(), "bot confused!", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
 
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
}
