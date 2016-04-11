package com.example.pmed.formmanager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pmed.formparser.Form;
import com.example.pmed.formparser.Prompt;
import com.example.pmed.mindfulnessmeditation.R;

import android.view.LayoutInflater;

/**
 * Created by calebbasse on 4/2/16.
 */
public class FormManager {
    public Form form;
    public LayoutInflater inflater;
    public ViewGroup vg;

    public FormManager(Form f, LayoutInflater inf, ViewGroup vg) {
        form = f;
        inflater = inf;
        this.vg = vg;

        for (int i = 0; i < form.prompts.length; i++) {
            if (form.prompts[i].promptType == "mult") {
                multipleChoice(i);
            } else if (form.prompts[i].promptType == "check") {
                checkbox(i);
            }
        }

    }

    public void multipleChoice(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup mult = (ViewGroup) inflater.inflate(R.layout.prompt_mult, null);
        ((TextView)mult.getChildAt(0)).setText(p.question);

        RadioGroup radioGroup = (RadioGroup) mult.getChildAt(1);

        RadioButton checkbox;
        int id;
        EditText textField;
        for (int i = 0; i < p.options.length; i++) {
            checkbox = (RadioButton) inflater.inflate(R.layout.prompt_mult_checkbox, null);
            checkbox.setText(p.options[i].choice);
            //id = RadioButton.generateViewId();
            //checkbox.setId(id);
            radioGroup.addView(checkbox);
            if (p.options[i].textBox == true) {
                //checkbox.setId(123);
                textField = (EditText) inflater.inflate(R.layout.prompt_optional_textbox, null);
                radioGroup.addView(textField);
                textField.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            //if (!event.isShiftPressed()) {
                                // the user is done typing.
                                form.prompts[promptIndex].answerText = v.getText().toString();
                                Log.d("EnteredText", v.getText().toString());
                                return true; // consume.
                            //}
                        }
                        return false; // pass on to other listeners.
                    }
                });
            }
        }
        vg.addView(mult);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                form.prompts[promptIndex].answer = Integer.toString(checkedId);
                if (form.prompts[promptIndex].options[checkedId-1].textBox == true) {

                }
            }

        });

    }

    public void checkbox(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup checkGroup = (ViewGroup) inflater.inflate(R.layout.prompt_check, null);
        ((TextView)checkGroup.getChildAt(0)).setText(p.question);

        //RadioGroup radioGroup = (RadioGroup) mult.getChildAt(1);

        CheckBox checkbox;
        int id;
        EditText textField;
        for (int i = 0; i < p.options.length; i++) {
            checkbox = (CheckBox) inflater.inflate(R.layout.prompt_check_checkbox, null);
            checkbox.setText(p.options[i].choice);
            //id = RadioButton.generateViewId();
            //checkbox.setId(id);
            checkGroup.addView(checkbox);
            if (p.options[i].textBox == true) {
                //checkbox.setId(123);
                textField = (EditText) inflater.inflate(R.layout.prompt_optional_textbox, null);
                checkGroup.addView(textField);
                textField.setOnEditorActionListener(
                        new EditText.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                        actionId == EditorInfo.IME_ACTION_DONE ||
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    //if (!event.isShiftPressed()) {
                                    // the user is done typing.
                                    form.prompts[promptIndex].answerText = v.getText().toString();
                                    Log.d("EnteredText", v.getText().toString());
                                    return true; // consume.
                                    //}
                                }
                                return false; // pass on to other listeners.
                            }
                        });
            }
        }
        vg.addView(checkGroup);

        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                form.prompts[promptIndex].answer = Integer.toString(checkedId);
                if (form.prompts[promptIndex].options[checkedId-1].textBox == true) {

                }
            }

        });*/

    }
}
