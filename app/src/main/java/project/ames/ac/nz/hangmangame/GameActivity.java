package project.ames.ac.nz.hangmangame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //Declare variables, objects for target word
    private String[] wordList;
    private String[] words;
    private String[] hints;
    private Random rand;
    private String currWord;
    private LinearLayout wordLayout;
    private TextView[] charViews;
    private TextView hintTxt;
    private ImageView gallows;

    //Add an instance variable for the gridView and the Adapter
    private GridView letters;
    private LetterButtonsCreator letterAdapter;

    //Declare instance variables for User Interaction
    //Body part images
    private Bitmap gallows_rope;
    private Bitmap[] bodyParts;
    //Number of body parts. If you'd like to add several levels of difficulty to the game, change this number
    private int numParts = 6;
    //Current part  will increment when wrong answer are chosen/guessed.
    private int currentPart;
    //Number of characters in current words
    private int numberChars;
    //Number right guesses (correctly guessed) to keep track of the player's progress in the current
    //game. We periodically check if the player has won or lost the game.
    private int numberCorrect;

    //Add an instance variable for the "help" information
    private AlertDialog helpAlert;

    //Score function
    private TextView score;
    private TextView listCorrectWords;

    //Timer
    private TextView timer;
    private boolean timer_condition = true;
    private boolean win = false;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelayout);

        /////////////////////////////////////////////////////////
        //Read the collection of words and store them into the wordList instance variable
        Resources res = getResources();
        wordList = res.getStringArray(R.array.words);
        words = new String[wordList.length / 2];
        hints = new String[wordList.length / 2];
        //Split wordList into words array and hints array
        for (int i = 0; i < wordList.length / 2; i++) {
            words[i] = wordList[2 * i];
            hints[i] = wordList[2 * i + 1];
        }

        //Initialize the rand object and currentWord string
        rand = new Random();
        currWord = "";

        //Get a reference to the layout area we created for the answer letters
        wordLayout = (LinearLayout) findViewById(R.id.wordlayout);
        //Get a reference to the TextView - hints
        hintTxt = (TextView) findViewById(R.id.wordhint);
        //Get a reference to the gallows ImageView
        gallows = (ImageView) findViewById(R.id.gallows);


        /////////////////////////////////////////////////////////
        //Get a reference to the GridView
        letters = (GridView) findViewById(R.id.letters);

        //////////////////////////////////////////////////////////////
        //User Interaction
        //Initialize the bodyParts array
        bodyParts = new Bitmap[numParts];
        //Split the hangman.png into 7 sub-images
        Bitmap hangmanImage = BitmapFactory.decodeResource(res, R.drawable.hangedman);
        int hangmanImageWidth = hangmanImage.getWidth();
        int hangmanImageHeight = hangmanImage.getHeight();

        //Extract the first sub-image
        gallows_rope = Bitmap.createBitmap(hangmanImage, 0, 0, hangmanImageWidth / 7, hangmanImageHeight);
        bodyParts[0] = Bitmap.createBitmap(hangmanImage, hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);
        //Extract the second sub-image
        bodyParts[1] = Bitmap.createBitmap(hangmanImage, 2 * hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);
        //Extract the third sub-image
        bodyParts[2] = Bitmap.createBitmap(hangmanImage, 3 * hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);
        //Extract the fourth sub-image
        bodyParts[3] = Bitmap.createBitmap(hangmanImage, 4 * hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);
        //Extract the fifth sub-image
        bodyParts[4] = Bitmap.createBitmap(hangmanImage, 5 * hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);
        //Extract the sixth sub-image
        bodyParts[5] = Bitmap.createBitmap(hangmanImage, 6 * hangmanImageWidth / 7, 0, hangmanImageWidth / 7, hangmanImageHeight);

        //Score function
        score = (TextView) findViewById(R.id.score);
        listCorrectWords = (TextView) findViewById(R.id.listCorrectWords);
        //Initialize the score to 0
        score.setText("Score: 0");

        //Timer
        timer = (TextView) findViewById(R.id.timer);


        //////////////////////////////////////////////////////////////
        //Call playGame() method
        playGame();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void playGame() {
        //play a new game
        //Set Timer
        timerCount(true, 15000);

        //Choose a random word from the array
        int wordRandomID;
        wordRandomID = rand.nextInt(words.length);

        String newWord = words[wordRandomID];
        //because the playGame method is invoked when the user chooses to play again after winning or
        //losing a game, it's important that we make sure we don't pick the same word two times in a row
        while (newWord.equals(currWord)) {
            wordRandomID = rand.nextInt(words.length);
            newWord = words[wordRandomID];
        }
        //Update the current word instance variable with the new target word
        currWord = newWord;
        //Display the hints to screen
        hintTxt.setText(hints[wordRandomID]);

        /////////////////////////////////////////////////
        //Create a new array to store the TextViews for the letters of the target word
        charViews = new TextView[currWord.length()];
        //Remove any TextViews from the wordLayout layout
        wordLayout.removeAllViews();
        //Use a simple for loop to iterate over each letter of the answer, create a TextView for each
        //letter, and set the TextView's text to current letter
        for (int c = 0; c < currWord.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + currWord.charAt(c));

            //Set display properties on the TextView and add it to the layout
            charViews[c].setLayoutParams(new LinearLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT));
            //Set gravity property
            charViews[c].setGravity(Gravity.CENTER);
            //Set TextColor to WHITE so that player will not be able to see it against the WHITE background
            charViews[c].setTextColor(Color.WHITE);
            //Set BackgroundResource property
            charViews[c].setBackgroundResource(R.drawable.letter_bg);
            //Add to layout
            wordLayout.addView(charViews[c]);
        }

        //
        //Instantiate the adapter and set it on the gridView
        letterAdapter = new LetterButtonsCreator(this);
        letters.setAdapter(letterAdapter);


        //////////////////////////////////////////////////
        //User Interaction
        //Initialize all these variables
        currentPart = 0;
        numberChars = currWord.length();
        numberCorrect = 0;
        //Before we start the game, the body parts need to be hidden, showing only gallows
        gallows.setImageResource(R.drawable.gallows);
        //gallows.setImageBitmap(gallows_rope);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //This method will be executed when a letter button on GridView has been clicked
    public void letterPressed(View myView) {
        //User has pressed a letter to guess, this method receives a reference to the view. This
        //allows us to figure out which letter the player has been chosen.
        String letter = ((TextView) myView).getText().toString();
        //We get the character from the string
        char letterChar = letter.charAt(0);
        //Disable the letter button and update the background drawable to show the player that the
        //letter has been already played
        myView.setEnabled(false);
        myView.setBackgroundResource(R.drawable.letter_down);

        //Loop through the characters of the target word to verify whether the player's guess is in it.
        boolean correct = false;
        for (int k = 0; k < currWord.length(); k++) {
            if (currWord.charAt(k) == letterChar) {
                correct = true;
                numberCorrect++;
                charViews[k].setTextColor(Color.BLACK);
            }
        }

        //Check timer
        if (timer_condition) {

        }


        //Check whether the player has won or lost the game after their guess, or made a wrong guess
        //but still continue.
        if (correct) {
            //Player made a correct guess
            //Check if player's guessed all the letters of the target word
            if (numberCorrect == numberChars) {
                //Player has won: firstly disable the letter buttons by invoking the method disableBtn()
                //that will be implemented below. Secondly, display and AlertDialog to the user. In the
                //AlertDialog, we also ask the player if they want to play another game.
                //Disable Letter Buttons
                disableBtns();
                //Display AlertDialog
                AlertDialog.Builder windBuild = new AlertDialog.Builder(this);
                windBuild.setTitle("Congratulation");
                windBuild.setMessage("You win!\n\nThe answer was:\n" + currWord);
                windBuild.setPositiveButton("Continue to play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Update score and list of correctly guessed words
                        String exScoreString = score.getText().toString();
                        int exScore = Integer.parseInt(exScoreString.substring(exScoreString.lastIndexOf(" ") + 1));
                        score.setText("Score: "+ (exScore + 1));
                        //
                        String correctWords = listCorrectWords.getText().toString();
                        listCorrectWords.setText(correctWords + currWord + "\n");
                        //
                        playGame();
                    }
                });
                windBuild.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GameActivity.this.finish();
                    }
                });
                windBuild.show();
            }
        }
        else if (currentPart < numParts) {
            //Incorrect guess but have some guesses left
            gallows.setImageBitmap(bodyParts[currentPart]);
            currentPart++;
        }
        else {
            //Incorrect guess and no guess left -> Lost
            //Play has lost
            disableBtns();
            //Display an AlertDialog
            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
            loseBuild.setTitle("SORRY");
            loseBuild.setMessage("You lose!\n\nThe answer was:\n" + currWord);
            loseBuild.setPositiveButton("Continue to play", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    playGame();
                }
            });
            loseBuild.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GameActivity.this.finish();
                }
            });
            loseBuild.show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    public void disableBtns() {
        //Disable the letter buttons when player has won the game.
        int numberLetters = letters.getChildCount();
        //Loop through the Views via the Adapter and disable each button.
        for (int i = 0; i < numberLetters; i++) {
            letters.getChildAt(i).setEnabled(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //Inflate your menu resource here
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create an inflater object
        MenuInflater inflater = getMenuInflater();
        //Call method inflate() to populate all items into the menu
        inflater.inflate(R.menu.my_option_menu, menu);
        //return value
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to the menu item selected
        switch (item.getItemId()) {
            case R.id.action_help:
                //"Help" item has been clicked
                AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);
                helpBuild.setTitle("Help");
                helpBuild.setMessage("Guess the word by selecting the letters.\n\n"
                                   + "You only have 6 wrong selections then it's game over!");
                helpBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        helpAlert.dismiss();
                    }
                });
                helpAlert = helpBuild.create();
                helpBuild.show();
                return true;

            default:
                //Other cases and ERROR
                //Do nothing
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    public void timerCount(boolean condition, int timeDuration) {
        if (condition) {
            //Timer is setting RUN
            CountDownTimer timerCount = new CountDownTimer(timeDuration, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //When timer is running
                    String currentTimeString = DateFormat.getTimeInstance().format(new Date());
                    timer.setText("Timer: " + currentTimeString);
                    timer_condition = true;
                }

                @Override
                public void onFinish() {
                    //when time is over
                    timer.setText("TIME IS OVER");
                    timer_condition = false;
                }
            }.start();
        }
        else {
            //Timer is setting STOP
            timer.setText("Timer: STOP");
            timer_condition = false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //



}
