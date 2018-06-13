package project.ames.ac.nz.hangmangame;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

public class LetterButtonsCreator extends BaseAdapter {
    //////////////////////////////////////////////////////////////////
    //Declare variables and objects
    private String[] letters;//letters array will store the letters of the alphabet
    private LayoutInflater letterInflater;//letterinflater will apply the button layout to view

    //////////////////////////////////////////////////////////////////
    //Constructor
    public LetterButtonsCreator(Context context) {
        //Setup Adapter
        //Instantiate the alphabet array and assign the letters A-Z to each position
        letters = new String[26];
        for (int i = 0; i < letters.length; i++) {
            //Each character is represented as a number so that we can set the letter A to Z in a loop
            //starting at zero by adding the value of the character A to each array index
            letters[i] = "" + (char) ('A' + i);//'A' - 'B' - .....= 'Z'
        }
        //Specify the context in which we want to inflate the layout
        letterInflater = LayoutInflater.from(context);
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public int getCount() {
        return letters.length;
    }

    //////////////////////////////////////////////////////////////////
    //Update the implementation of getView() method
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Create a button for the letter at the position in the alphabet
        Button letterBtn;
        if (convertView == null) {
            //Inflate the button layout
            letterBtn = (Button) letterInflater.inflate(R.layout.letter, parent, false);
        } else {
            letterBtn = (Button) convertView;
        }

        //Set the text to this letter
        letterBtn.setText(letters[position]);
        return letterBtn;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public Object getItem(int arg0) {
        return null;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}
