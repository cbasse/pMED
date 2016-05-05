package com.example.pmed.mindfulnessmeditation;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormActivity extends AppCompatActivity {
    public Form form;
    public LayoutInflater inflater;
    public ViewGroup vg;
    public FormResultsManager results;

    // Lincolns Code
    JSONParser jParser = new JSONParser();
    String questionnaireId;
    String experimentId;
    String userId;
    Integer pos_affects;
    Integer neg_affects;
    JSONParser jsonParser = new JSONParser();
    String TAG_SUCCESS = "success";
    JSONParser jsParser = new JSONParser();
    String resultId;
    // ^^^^^^^^^^^^^^^^^^

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

        Intent intent = getIntent();

        String formName = intent.getStringExtra("com.example.pmed.FORM_NAME");
        String formsDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Experiments";

        this.userId = intent.getStringExtra("com.example.pmed.USER_ID");
        this.experimentId = intent.getStringExtra("com.example.pmed.EXPERIMENT_ID");
        this.questionnaireId = intent.getStringExtra("com.example.pmed.QUESTIONNAIRE_ID");

        try {
            //form = new Form(new File(formsDirectoryPath + "/" + formName));
            if(getIntent().getStringExtra("com.example.pmed.UPDATE_FORM").equals("true"))
            {
                Update();
            }
            else
            {
                BuildFormFromDatabase();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            System.exit(-1);
        }

        // moved all this code to onPostExecute function in class RetrieveForm below !!!!!
    }




    public void multipleChoice(final int promptIndex) {

        final Prompt p = form.prompts.get(promptIndex);

        ViewGroup mult = (ViewGroup) inflater.inflate(R.layout.prompt_mult, null);
        ((TextView)mult.getChildAt(0)).setText(p.question.getText());

        RadioGroup radioGroup = (RadioGroup) mult.getChildAt(1);

        RadioButton checkbox;
        int id;
        EditText textField;
        for (int i = 0; i < p.options.size(); i++) {
            checkbox = (RadioButton) inflater.inflate(R.layout.prompt_radiobutton, null);
            checkbox.setText(p.options.get(i).choice);
            //id = RadioButton.generateViewId();
            checkbox.setId(i + 1);
            radioGroup.addView(checkbox);
            if (p.options.get(i).textBox == true) {
                textField = (EditText) inflater.inflate(R.layout.prompt_optional_textbox, null);
                textField.setFocusable(false);
                radioGroup.addView(textField);
                textField.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        results.setValue(p.question.id, s.toString());
                    }

                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
        vg.addView(mult);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                form.prompts.get(promptIndex).answerText = Integer.toString(checkedId);



                //results.setValue(p.name, Integer.toString(checkedId));
                results.setValue(p.question.id, p.options.get(checkedId - 1).choice);




                if (form.prompts.get(promptIndex).options.get(checkedId - 1).textBox == true) {
                    EditText optional = (EditText) group.getChildAt(checkedId);
                    optional.setFocusableInTouchMode(true);
                } else {
                    for (int i = 0; i < group.getChildCount(); i++) {
                        if (group.getChildAt(i).getId() == R.id.optional_textbox && group.getChildAt(i).isFocused()) {
                            EditText et = (EditText) group.getChildAt(i);
                            et.setText("");
                            et.setFocusable(false);
                            et.clearFocus();
                            //results.setValue(p.name + "_text", "");
                        }
                    }
                }
            }

        });

    }

    public void checkbox(final int promptIndex) {
        final Prompt p = form.prompts.get(promptIndex);
        ViewGroup checkGroup = (ViewGroup) inflater.inflate(R.layout.prompt_check, null);
        ((TextView)checkGroup.getChildAt(0)).setText(p.question.getText());

        CheckBox checkbox;
        EditText textField;
        for (int i = 0; i < p.options.size(); i++) {
            checkbox = (CheckBox) inflater.inflate(R.layout.prompt_checkbox, null);
            checkbox.setText(p.options.get(i).choice);
            checkbox.setId(i + 1);
            checkGroup.addView(checkbox);
            if (p.options.get(i).textBox == true) {
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
                                        {
                                            String answer = results.getValue(p.question.id);
                                            if(answer.length() > 1)
                                            {
                                                answer = answer + " : ";
                                            }

                                            answer = answer + p.options.get(checkParent.getChildAt(i-1).getId()-1).choice;

                                            if(v.getText().toString().length() > 1)
                                            {
                                                results.setValue(p.question.id, answer + ":text = " + v.getText().toString());
                                            }
                                        }
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
                    //String key = p.name + "_" + p.options.get(checkId-1).choice;
                    String key = p.question.id;
                    String answer = results.getValue(key);
                    if (isChecked)
                    {
                        if(answer.length() > 1)
                        {
                            answer = answer + " : ";
                        }
                        answer = answer + p.options.get(checkId-1).choice;
                    }
                    else
                    {
                        answer = answer.replace(p.options.get(checkId-1).choice, "");
                    }
                    results.setValue(key, answer);

                    ViewGroup parent = (ViewGroup)buttonView.getParent();

                    if (form.prompts.get(promptIndex).options.get(checkId - 1).textBox == true && isChecked) {
                        CheckBox optional = (CheckBox) parent.getChildAt(checkId);
                        optional.setFocusableInTouchMode(true);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i).getId() == R.id.optional_textbox && parent.getChildAt(i).isFocused()) {
                                EditText et = (EditText) parent.getChildAt(i);
                                et.setText("");
                                et.setFocusable(false);
                                et.clearFocus();
                                //results.setValue(p.name + "_" + p.options.get(parent.getChildAt(i-1).getId()-1).choice + "_text", "");
                                results.setValue(key, answer.split(":text = ")[0] );
                            }
                        }
                    }
                }
            });
        }
        vg.addView(checkGroup);
    }

    public void shortAnswer(final int promptIndex) {
        final Prompt p = form.prompts.get(promptIndex);
        ViewGroup shortGroup = (ViewGroup) inflater.inflate(R.layout.prompt_short, null);
        ((TextView)shortGroup.getChildAt(0)).setText(p.question.getText());

        EditText textField = (EditText)shortGroup.getChildAt(1);

        textField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                results.setValue(p.question.id, s.toString());
            }

            public void afterTextChanged(Editable s) {

            }
        });


        vg.addView(shortGroup);
    }

    public void likert(final int promptIndex) {
        final Prompt p = form.prompts.get(promptIndex);
        LinearLayout likertGroup = (LinearLayout) inflater.inflate(R.layout.prompt_likert, null);

        TextView descText = (TextView) likertGroup.findViewById(R.id.likert_desc_text);
        descText.setText(p.likertDescription);
        Question question;
        LinearLayout likertRow;
        TextView likertRowText;
        Spinner likertRowSpinner;
        for (int i = 0; i < p.likertQuestions.size(); i++) {
            question = p.likertQuestions.get(i);
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
                    String key = p.name + "_" + p.likertQuestions.get(qIndex).getText();
                    if (p.likertQuestions.get(qIndex).isReverse()) {
                        for (int j = 0; j < p.options.size(); j++) {
                            if (score.equals(p.options.get(j).score)) {
                                score = p.options.get(p.options.size() - (j + 1)).score;
                                //System.out.println("is reverse: " + score);
                                break;
                            }
                        }
                    }

                    results.setValue(p.likertQuestions.get(qIndex).id, score);
                    //System.out.println(p.likertQuestions.get(qIndex).id + " " + p.likertQuestions.get(qIndex).getText() + " " + score);
                    /*if (p.likertQuestions.get(qIndex).positive == null)
                    {
                        Log.w("positive", "happa");
                    }
                    else if(p.likertQuestions.get(qIndex).positive == true)
                    {
                        results.setValue("likert_pos_affects", Integer.toString(Integer.parseInt(results.getValue("likert_pos_affects")) + Integer.parseInt(score))); //NEED TO FIX
                    }
                    else if(p.likertQuestions.get(qIndex).positive == false)
                    {
                        results.setValue("likert_neg_affects", Integer.toString(Integer.parseInt(results.getValue("likert_neg_affects")) + Integer.parseInt(score))); //NEED TO FIX
                    }*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> av) {

                }
            });

        }

        vg.addView(likertGroup);
    }

    public ArrayList<String> SortIds()
    {
        ArrayList<Integer> sorted = new ArrayList<Integer>();

        for(Map.Entry<String, String> entry : results.results.entrySet())
        {
            if(isInteger(entry.getKey()))
            {
                sorted.add(Integer.parseInt(entry.getKey()));
            }
        }

        Collections.sort(sorted);

        ArrayList<String> answer = new ArrayList<>();
        for(int i = 0; i < sorted.size(); i++)
        {
            answer.add(Integer.toString(sorted.get(i)));
        }

        return answer;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


    private void BuildFormFromDatabase() {
        String url = "";


        form = new Form();

        new RetrieveForm().execute();
    }

    private void Update()
    {
        new UpdateQuestionnaireNumber().execute();
    }

    class RetrieveForm extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            Log.w("get questionnaire", "id is " + questionnaireId);
            // Building Parameters
            String url = "http://meagherlab.co/read_questions_and_answer_choices_for_questionnaire.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", questionnaireId));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url, "GET", params);

            form.prompts = new ArrayList<Prompt>();

            // Check your log cat for JSON reponse
            //Log.d("All Products: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");

                if (success == 1) {
                    JSONArray JQs = json.getJSONArray("questions");

                    Prompt likert = new Prompt();
                    likert.options = new ArrayList<Option>();
                    likert.likertQuestions = new ArrayList<Question>();
                    likert.promptType = "likert";
                    Boolean addedLikert = false;
                    // looping through All Products
                    for (int i = 0; i < JQs.length(); i++) {

                        // Set up prompt
                        JSONObject q = JQs.getJSONObject(i);
                        if(i == 0)
                        {
                            form.questionnaireId = q.getString("questionnaire_id");
                        }
                        //System.out.println("hekll " + q);

                        // Create question
                        String txt = q.getString("text");
                        Boolean rev = Boolean.parseBoolean(q.getString("is_reversed"));


                        Boolean pos = null;
                        if (!q.getString("is_positive").equals("null"))
                            pos = Boolean.parseBoolean(q.getString("is_positive"));
                        //Boolean pos = Boolean.parseBoolean(q.getString("is_positive"));
                        //System.out.println(pos);
                        Question question = new Question(txt, rev, pos);
                        question.id = q.getString("id");

                        // Create prompt
                        String type = q.getString("question_type");
                        Prompt prompt;
                        if(type.equals("likert"))
                        {
                            prompt = likert;
                            prompt.likertDescription = q.getString("likert_description");
                            prompt.likertQuestions.add(question);
                        }
                        else
                        {
                            prompt = new Prompt();
                            prompt.promptType = type;
                            prompt.question = question;
                            prompt.options = new ArrayList<Option>();
                        }

                        // Create answer options

                        //Log.w("test", Integer.toString(prompt.options.size()));
                        //Log.w("test2", Integer.toString(JOps.length()));

                        // check to make sure likert options arent add more than once
                        if(q.has("answer_choices") && prompt.options.size() < 1)
                        {
                            JSONArray JOps = q.getJSONArray("answer_choices");
                            for(int j = 0; j < JOps.length(); j++)
                            {
                                JSONObject JOp = JOps.getJSONObject(j);
                                Option op = new Option();
                                op.choice = JOp.getString("text");
                                op.id = JOp.getString("id");
                                op.score = JOp.getString("score");
                                op.textBox = Boolean.parseBoolean(JOp.getString("is_text"));

                                prompt.options.add(op);
                            }
                        }

                        if(!type.equals("likert") || !addedLikert)
                        {
                            form.prompts.add(prompt);
                            if(type.equals("likert"))
                            {
                                addedLikert = true;
                            }
                        }
                    }

                } else {
                    /*
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    vg = (LinearLayout) findViewById(R.id.prompts);
                    inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    results = new FormResultsManager(form);

                    TextView description = (TextView) findViewById(R.id.form_description);
                    description.setText(form.formDesc);



                    for (int i = 0; i < form.prompts.size(); i++) {
                        if (form.prompts.get(i).promptType.equals("mult")) {
                            multipleChoice(i);
                        } else if (form.prompts.get(i).promptType.equals("check")) {
                            checkbox(i);
                        } else if (form.prompts.get(i).promptType.equals("short")) {
                            shortAnswer(i);
                        } else if (form.prompts.get(i).promptType.equals("likert")) {
                            likert(i);
                        }
                    }

                    Button nextButton = (Button) inflater.inflate(R.layout.prompt_next_button, null);
                    vg.addView(nextButton);

                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent data = getIntent();
                            Integer pos_affects = 0;
                            Integer neg_affects = 0;
                            /*
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
                            */

                            //results.setValue("likert_pos_affects", pos_affects.toString());
                            //results.setValue("likert_neg_affects", neg_affects.toString());
                            //System.out.println(results);


                            for (Prompt p : form.prompts) {
                                if (p.promptType == "likert") {
                                    for (Question q : p.likertQuestions) {
                                        if (q.positive == null) {
                                            continue;
                                        } else if (q.positive == true) {
                                            results.setValue("likert_pos_affects",
                                                    Integer.toString(Integer.parseInt(results.getValue("likert_pos_affects")) + Integer.parseInt(results.getValue(q.id))));
                                        } else {
                                            results.setValue("likert_neg_affects",
                                                    Integer.toString(Integer.parseInt(results.getValue("likert_neg_affects")) + Integer.parseInt(results.getValue(q.id))));
                                        }
                                    }
                                }
                            }
                            Log.w("results", results.results.toString());

                            new PostResults().execute();
                        }
                    });

                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            });

        }

    }

    class PostResults extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            String url = "http://meagherlab.co/record_questionnaire_results.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            try {
                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("positive", results.results.get("likert_pos_affects")));
                params.add(new BasicNameValuePair("negative", results.results.get("likert_neg_affects")));
                System.out.println("THESE ARE THE RESULTS: " + results.results.get("likert_neg_affects"));
                JSONArray responses = new JSONArray();
                ArrayList<String> sortedIds = SortIds();
                for(int i = 0; i < sortedIds.size(); i++)
                {
                    JSONObject response = new JSONObject();
                    response.put("question_id", sortedIds.get(i));
                    response.put("text", results.results.get(sortedIds.get(i)));
                    response.put("score", "");
                    //System.out.println(sortedIds.get(i) + " " + results.results.get(sortedIds.get(i)));
                    //System.out.println(response);
                    responses.put(response);
                }

                params.add(new BasicNameValuePair("responses", responses.toString()));
                //System.out.println("asdfasdf " + params.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);



            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    resultId = json.getString("questionnaire_result_id");

                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    Log.w("on post exec", "post execute");

                    Intent i = new Intent();

                    i.putExtra("com.example.pmed.FORM_RESULTS", results);
                    i.putExtra("com.example.pmed.RESULT_ID", resultId);
                    if (getParent() == null) {
                        setResult(1, i);
                    } else {
                        getParent().setResult(1, i);
                    }
                    finish();

                    //setResult(1, i);
                    //finish();

                }
            });

        }


    }


    class UpdateQuestionnaireNumber extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", userId));
            // getting JSON string from URL
            JSONObject json = jsParser.makeHttpRequest("http://meagherlab.co/read_questionnaire_id_for_user.php", "GET", params);

            // Check your log cat for JSON reponse

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt("success");
                if (success == 1) {

                    questionnaireId = json.getString("questionnaire_id");

                } else {
                    /*
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    BuildFormFromDatabase();
                }
            });

        }


    }

}
