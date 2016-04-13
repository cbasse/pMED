package com.example.pmed.formmanager;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.pmed.formparser.Form;
import com.example.pmed.formparser.Option;
import com.example.pmed.formparser.Prompt;
import com.example.pmed.formparser.Question;
import com.example.pmed.mindfulnessmeditation.R;

import android.view.LayoutInflater;

import org.w3c.dom.Text;

/**
 * Created by calebbasse on 4/2/16.
 */
public class FormManager {
    public Form form;
    public LayoutInflater inflater;
    public ViewGroup vg;
    public Context c;
    public FormManager(Form f, LayoutInflater inf, ViewGroup vg, Context c) {
        form = f;
        inflater = inf;
        this.vg = vg;
        this.c = c;

        for (int i = 0; i < form.prompts.length; i++) {
            if (form.prompts[i].promptType.equals("mult")) {
                multipleChoice(i);
            } else if (form.prompts[i].promptType.equals("check")) {
                checkbox(i);
            } else if (form.prompts[i].promptType.equals("short")) {
                shortAnswer(i);
            } else if (form.prompts[i].promptType.equals("likert")) {
                likert(i);
            }
        }

    }

    public void multipleChoice(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup mult = (ViewGroup) inflater.inflate(R.layout.prompt_mult, null);
        ((TextView)mult.getChildAt(0)).setText(p.question.getText());

        RadioGroup radioGroup = (RadioGroup) mult.getChildAt(1);

        RadioButton checkbox;
        int id;
        EditText textField;
        for (int i = 0; i < p.options.length; i++) {
            checkbox = (RadioButton) inflater.inflate(R.layout.prompt_radiobutton, null);
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
                form.prompts[promptIndex].answerText = Integer.toString(checkedId);
                if (form.prompts[promptIndex].options[checkedId-1].textBox == true) {

                }
            }

        });

    }

    public void checkbox(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup checkGroup = (ViewGroup) inflater.inflate(R.layout.prompt_check, null);
        ((TextView)checkGroup.getChildAt(0)).setText(p.question.getText());

        //RadioGroup radioGroup = (RadioGroup) mult.getChildAt(1);

        CheckBox checkbox;
        int id;
        EditText textField;
        for (int i = 0; i < p.options.length; i++) {
            checkbox = (CheckBox) inflater.inflate(R.layout.prompt_checkbox, null);
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

    public void shortAnswer(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup shortGroup = (ViewGroup) inflater.inflate(R.layout.prompt_short, null);
        ((TextView)shortGroup.getChildAt(0)).setText(p.question.getText());

        vg.addView(shortGroup);
    }

    public void likert(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        LinearLayout likertGroup = (LinearLayout) inflater.inflate(R.layout.prompt_likert, null);

        TextView descText = (TextView) likertGroup.findViewById(R.id.likert_desc_text);
        descText.setText(p.likertDescription);

        Question question;
        LinearLayout likertRow;
        TextView likertRowText;
        Spinner likertRowSpinner;
        for (int i = 0; i < p.likertQuestions.length; i++) {
            question = p.likertQuestions[i];
            likertRow = (LinearLayout) inflater.inflate(R.layout.prompt_likert_row, null);
            likertRowText = (TextView) likertRow.findViewById(R.id.likert_row_text);
            likertRowText.setText(question.getText());

            likertRowSpinner = (Spinner) likertRow.findViewById(R.id.likert_row_spinner);
            ArrayAdapter<Option> adapter = new ArrayAdapter<Option>(c, android.R.layout.simple_spinner_item, p.options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            likertRowSpinner.setAdapter(adapter);
            likertGroup.addView(likertRow);
        }



        //TableRow likertRow = (TableRow) inflater.inflate(R.layout.prompt_likert_row, null);








        vg.addView(likertGroup);
    }


}
