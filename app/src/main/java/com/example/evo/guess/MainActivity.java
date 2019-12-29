package com.example.evo.guess;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mooveit.library.Fakeit;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private int currentIndex=0;
    private LinearLayout wordsLinearLayout;
    private ArrayList<String> wordArrayList;
    private ArrayList<String> dbWordArrayList;
    private ArrayList<Integer> dbScoreArrayList;
    private String currentWord;
    private TextView firstLetter;
    private TextView lastLetter;
    private ArrayList<EditText> editTextArrayList;
    private at.markushi.ui.CircleButton circleButton;
    private TextView score,bScore,lastScore;
    private int scoreCount;
    private int letterVisiblePossible[] = {1,2,3,4,5,6,7,8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setScore();

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            scoreCount = savedInstanceState.getInt("score");
        } else {
            // Probably initialize members with default values for a new instance
            scoreCount = 0;
        }

        wordsLinearLayout = (LinearLayout) findViewById(R.id.wordsLinearLayout);
        circleButton = (at.markushi.ui.CircleButton) findViewById(R.id.checkBtn);
        score = (TextView) findViewById(R.id.score);
        bScore= (TextView) findViewById(R.id.bScore);
        firstLetter = (TextView) findViewById(R.id.firstLetter);
        lastLetter = (TextView) findViewById(R.id.lastLetter);


        initializeDb();
        loadDbWordArrayList();
        putGuessWord(currentIndex);
    }

    private void loadDbWordArrayList() {
        Cursor cursor = getContentResolver().query(GuessesProvider.CONTENT_URI,
                DBOpenHelper.ALL_COLUMNS, null, null, null, null);

//        String[] guesses = {DBOpenHelper.GUESS_TEXT};


        dbWordArrayList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            String guessWord;
            for (int i = 0; i < cursor.getCount(); i++) {
                guessWord = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBOpenHelper.GUESS_TEXT));
                dbWordArrayList.add(guessWord);
                cursor.moveToNext();
            }
            cursor.close();
        }


    }

    private void initializeDb() {
        wordArrayList = new ArrayList<String>();


        // Default locale is en for english data.
        Fakeit.init();

//        Log.d("MainActivity", "faker.artist() " + Fakeit.artist().name());

        int i=1;
        while (i != 20) {
            wordArrayList.add(Fakeit.artist().name());
            i++;
        }
        for (String guess : wordArrayList) {
            insertGuess(guess);
        }


    }

    private void putGuessWord(int index) {

        if (index==18)
            initializeDb();

        // Clear a layout
        if((wordsLinearLayout).getChildCount() > 0)
            ( wordsLinearLayout).removeAllViews();
        //Initialize 1st & lst letter
        currentWord = dbWordArrayList.get(index);


//        Log.d("MainActivity", "currentWord " + currentWord);


        firstLetter.setText(String.valueOf(currentWord.charAt(0)));
        lastLetter.setText(String.valueOf(currentWord.charAt(currentWord.length() - 1)));



        editTextArrayList = new ArrayList<>();

        for (int i = 1; i < currentWord.length() - 1; i++) {
            char c = currentWord.charAt(i);


            EditText editText;
            editText = new EditText(this);
            editText.setTextSize(24);



            //check -visible or not of the letter
            if (getRandomCle(letterVisiblePossible)!=i){
                editText.setEnabled(false);
                editText.setText(String.valueOf(currentWord.charAt(i)));            }


            //Save edittext
            wordsLinearLayout.addView(editText);
            //Create new guess
            editTextArrayList.add(editText);
        }



    }


    private void insertGuess(String guessText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.GUESS_TEXT, guessText);
        Uri guessUri = getContentResolver().insert(GuessesProvider.CONTENT_URI,
                values);
//        Log.d("MainActivity", "Inserted guess " + guessUri.getLastPathSegment());
    }


    public void checkResult(View view) {

        String checkedWord = firstLetter.getText().toString();
        for (EditText editText : editTextArrayList) {
            checkedWord += editText.getText().toString();
        }

        checkedWord += lastLetter.getText().toString();

//        Log.d("equalsIgnoreCase", "Inserted guess " + currentWord +"000000" +checkedWord);

        if (checkedWord.equalsIgnoreCase(currentWord)) {


            //Award
            int lastScore = Integer.valueOf(score.getText().toString());
            int newScore = lastScore + currentWord.length() - 2;

            score.setText(String.valueOf(newScore));
            //Award

            circleButton.setColor(Color.argb(110, 43, 255, 0));
            new java.util.Timer().schedule(

                    new java.util.TimerTask() {
                        @Override
                        public void run() {

                            runOnUiThread(new   Runnable() {
                                public void run() {
                                    circleButton.setColor(Color.argb(255, 206, 206, 206));
                                    putGuessWord(++currentIndex);
                                }
                            });
                        }
                    },
                    1000
            );
        } else {

            //Panishment
            int lastScore = Integer.valueOf(score.getText().toString());

            if (lastScore!=0) {
                int newScore = lastScore - currentWord.length();
                score.setText(String.valueOf(newScore));
            }
            //Panishment



            circleButton.setColor(Color.argb(255, 255, 0, 0));
            circleButton.setImageResource(R.drawable.window_close);
            new java.util.Timer().schedule(

                    new java.util.TimerTask() {
                        @Override
                        public void run() {

                            runOnUiThread(new   Runnable() {
                                public void run() {
                                    circleButton.setColor(Color.argb(255, 206, 206, 206));
                                    circleButton.setImageResource(R.drawable.check_circle);
                                }
                            });
                        }
                    },
                    1000
            );

        }
    }

    private static int getRandomCle(int letterVisiblePossible[]) {
        return letterVisiblePossible[(int) (Math.random() * letterVisiblePossible.length)];
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("score",Integer.valueOf(score.getText().toString()));
        //so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    //Score Treatement

    private void insertScore(int score) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.SCORE, score);
        Uri guessUri = getContentResolver().insert(ScoreProvider.CONTENT_URI,
                values);
//        Log.d("MainActivity", "Inserted score " + guessUri.getLastPathSegment());
    }


    private void setScore(){
        Cursor cursor = getContentResolver().query(ScoreProvider.CONTENT_URI,
                DBOpenHelper.ALL_COLUMNS1, null, null, null, null);


        dbScoreArrayList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            int score;
            for (int i = 0; i < cursor.getCount(); i++) {
                score = cursor.getInt(cursor
                        .getColumnIndexOrThrow(DBOpenHelper.SCORE));
                dbScoreArrayList.add(score);
                cursor.moveToNext();
            }
            cursor.close();
        }


        //Set last score

        int lastScore1 =  dbScoreArrayList.get(0);
        lastScore= (TextView) findViewById(R.id.lastScore);
        lastScore.setText(String.valueOf(lastScore1));


        Collections.sort(dbScoreArrayList,Collections.reverseOrder());

        //Set best score

        int bestScore =  dbScoreArrayList.get(0);
        bScore= (TextView) findViewById(R.id.bScore);
        bScore.setText(String.valueOf(bestScore));

    }

    protected void onPause(){
        super.onPause();
//        Log.d("MainActivity", "dbScoreArrayList " + Integer.valueOf(score.getText().toString()));
        insertScore(Integer.valueOf(score.getText().toString()));
    }


    public void reload(View view) {
        putGuessWord(++currentIndex);
    }
}