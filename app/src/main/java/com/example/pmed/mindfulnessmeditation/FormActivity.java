package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pmed.formmanager.FormResultsManager;
import com.example.pmed.formparser.Form;
import com.example.pmed.formparser.Option;
import com.example.pmed.formparser.Prompt;
import com.example.pmed.formparser.Question;

import java.io.File;

public class FormActivity extends AppCompatActivity {
    public Form form;
    public LayoutInflater inflater;
    public ViewGroup vg;
    public FormResultsManager results;

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String formName = getIntent().getStringExtra("com.example.pmed.FORM_NAME");
        String formsDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Experiments";

        try {
            form = new Form(new File(formsDirectoryPath + "/" + formName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }
        vg = (LinearLayout) findViewById(R.id.prompts);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        results = new FormResultsManager(form);

        TextView description = (TextView) findViewById(R.id.form_description);
        description.setText(form.formDesc);



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

        Button nextButton = (Button) inflater.inflate(R.layout.prompt_next_button, null);
        this.vg.addView(nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = getIntent();
                Integer pos_affects = 0;
                Integer neg_affects = 0;
                for (Prompt p : form.prompts) {
                    if (p.promptType.equals("likert")) {
                        for (Question q : p.likertQuestions) {
                            if (q.isNeutral())
                                continue;
                            else if (q.isPositive() && !results.getValue(p.name + "_" + q.getText()).equals(""))
                                pos_affects += Integer.valueOf(results.getValue(p.name + "_" + q.getText()));
                            else if (!q.isPositive() && !results.getValue(p.name + "_" + q.getText()).equals(""))
                                neg_affects += Integer.valueOf(results.getValue(p.name + "_" + q.getText()));
                        }
                    }
                }

                results.setValue("likert_pos_affects", pos_affects.toString());
                results.setValue("likert_neg_affects", neg_affects.toString());
                System.out.println(results);
                Intent i = new Intent();
                i.putExtra("com.example.pmed.FORM_RESULTS", results);
                setResult(1, i);
                finish();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }




    public void multipleChoice(final int promptIndex) {

        final Prompt p = form.prompts[promptIndex];

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
            checkbox.setId(i+1);
            radioGroup.addView(checkbox);
            if (p.options[i].textBox == true) {
                textField = (EditText) inflater.inflate(R.layout.prompt_optional_textbox, null);
                textField.setFocusable(false);
                radioGroup.addView(textField);
                textField.setOnEditorActionListener(
                        new EditText.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                        actionId == EditorInfo.IME_ACTION_DONE ||
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    results.setValue(p.name + "_text", v.getText().toString());
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

                results.setValue(p.name, Integer.toString(checkedId));
                if (form.prompts[promptIndex].options[checkedId - 1].textBox == true) {
                    EditText optional = (EditText) group.getChildAt(checkedId);
                    optional.setFocusableInTouchMode(true);
                } else {
                    for (int i = 0; i < group.getChildCount(); i++) {
                        if (group.getChildAt(i).getId() == R.id.optional_textbox && group.getChildAt(i).isFocused()) {
                            EditText et = (EditText) group.getChildAt(i);
                            et.setText("");
                            et.setFocusable(false);
                            et.clearFocus();
                            results.setValue(p.name + "_text", "");
                        }
                    }
                }
            }

        });

    }

    public void checkbox(final int promptIndex) {
        final Prompt p = form.prompts[promptIndex];
        ViewGroup checkGroup = (ViewGroup) inflater.inflate(R.layout.prompt_check, null);
        ((TextView)checkGroup.getChildAt(0)).setText(p.question.getText());

        CheckBox checkbox;
        EditText textField;
        for (int i = 0; i < p.options.length; i++) {
            checkbox = (CheckBox) inflater.inflate(R.layout.prompt_checkbox, null);
            checkbox.setText(p.options[i].choice);
            checkbox.setId(i + 1);
            checkGroup.addView(checkbox);
            if (p.options[i].textBox == true) {
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

                                    ViewGroup checkParent = (ViewGroup) v.getParent();
                                    for (int i = 0; i < checkParent.getChildCount(); i++) {
                                        if (checkParent.getChildAt(i).getId() == v.getId())
                                            results.setValue(p.name + "_" + p.options[checkParent.getChildAt(i-1).getId()-1].choice + "_text", v.getText().toString());
                                    }
                                }
                                return false; // pass on to other listeners.
                            }
                        });
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int checkId = buttonView.getId();
                    String key = p.name + "_" + p.options[checkId-1].choice;
                    if (isChecked)
                        results.setValue(key, "1");
                    else
                        results.setValue(key, "");
                    ViewGroup parent = (ViewGroup)buttonView.getParent();

                    if (form.prompts[promptIndex].options[checkId - 1].textBox == true && isChecked) {
                        CheckBox optional = (CheckBox) parent.getChildAt(checkId);
                        optional.setFocusableInTouchMode(true);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i).getId() == R.id.optional_textbox && parent.getChildAt(i).isFocused()) {
                                EditText et = (EditText) parent.getChildAt(i);
                                et.setText("");
                                et.setFocusable(false);
                                et.clearFocus();
                                results.setValue(p.name + "_" + p.options[parent.getChildAt(i-1).getId()-1].choice + "_text", "");
                            }
                        }
                    }
                }
            });
        }
        vg.addView(checkGroup);
    }

    public void shortAnswer(final int promptIndex) {
        Prompt p = form.prompts[promptIndex];
        ViewGroup shortGroup = (ViewGroup) inflater.inflate(R.layout.prompt_short, null);
        ((TextView)shortGroup.getChildAt(0)).setText(p.question.getText());

        EditText textField = (EditText)shortGroup.getChildAt(1);

        textField.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            results.setValue(form.prompts[promptIndex].name, v.getText().toString());
                        }
                        return false; // pass on to other listeners.
                    }
                });


        vg.addView(shortGroup);
    }

    public void likert(final int promptIndex) {
        final Prompt p = form.prompts[promptIndex];
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

            ArrayAdapter<Option> adapter = new ArrayAdapter<Option>(this, android.R.layout.simple_spinner_item, p.options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            likertRowSpinner.setAdapter(adapter);
            likertRowSpinner.setId(i);
            likertGroup.addView(likertRow);

            likertRowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ArrayAdapter<Option> adapter = (ArrayAdapter<Option>) parent.getAdapter();
                    String score = adapter.getItem(pos).score;
                    int qIndex = parent.getId();
                    String key = p.name + "_" + p.likertQuestions[qIndex].getText();
                    if (p.likertQuestions[qIndex].isReverse()) {
                        for (int j = 0; j < p.options.length; j++) {
                            if (score.equals(p.options[j].score)) {
                                score = p.options[p.options.length - (j + 1)].score;
                                break;
                            }
                        }
                    }
                    results.setValue(key, score);
                }

                @Override
                public void onNothingSelected(AdapterView<?> av) {

                }
            });

        }

        vg.addView(likertGroup);
    }
}
