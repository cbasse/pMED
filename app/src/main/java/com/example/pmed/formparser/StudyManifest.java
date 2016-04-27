package com.example.pmed.formparser;

/**
 * Created by calebbasse on 4/11/16.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.pmed.mindfulnessmeditation.AdminHome;
import com.example.pmed.mindfulnessmeditation.ConfirmExpParse;
import com.example.pmed.mindfulnessmeditation.JSONParser;
import com.example.pmed.mindfulnessmeditation.ManageUserAccounts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import java.io.BufferedReader;
import java.io.LineNumberReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudyManifest {
    public String studyName;
    public Form baseline;
    public int days;
    public Form formA;
    public int readingA;
    public File soundclip;
    public Form formB;
    public int readingB;
    public Form formFinal = null;
    public String audioFileName;


    JSONParser jsonParser = new JSONParser();
    String url = "http://meagherlab.co/upload_form.php";
    String TAG_SUCCESS = "success";

    private XmlPullParser xpp;

    public StudyManifest(File manifestFolder) throws Exception {
        try {
            //String fileContent = readXMLFile(formFile);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();

            //File folder = new File(manifestFolder);
            if (!manifestFolder.isDirectory()) throw new Exception("Items must be a folder containing Study documents");
            File[] files = manifestFolder.listFiles();


            xpp.setInput(new FileReader(findFile("manifest.xml", files)));

            xpp.next();

            String filename;

            studyName = parseManifestName();

            filename = parseTag("baseline", false);
            baseline = new Form(findFile(filename, files));

            days = Integer.parseInt(parseTag("days", false));

            readingA = Integer.parseInt(parseTag("reading-a", false));

            filename = parseTag("questionnaire-a", false);
            formA = new Form(findFile(filename, files));

            filename = parseTag("soundclip", false);
            soundclip = findFile(filename, files);

            filename = parseTag("questionnaire-b", false);
            formB = new Form(findFile(filename, files));

            readingB = Integer.parseInt(parseTag("reading-b", false));

            if (xpp.getEventType() != XmlPullParser.END_DOCUMENT && xpp.getName().equals("questionnaire-final")) {
                filename = parseTag("questionnaire-final", true);
                if (!filename.equals("")) {
                    formB = new Form(findFile(filename,files));
                }
            }





            audioFileName = soundclip.getName();
            /*

                Calebs code motha trucka

            */
            new AudioSync().execute("upload", soundclip.getPath(), audioFileName);

            /*

                Lincolns code motha trucka

            */
            new CreateNewStudy().execute();






            System.out.println("studyName " + studyName);
            System.out.println("baseline " + baseline.formName);
            System.out.println("days " + days);
            System.out.println("readingA " + readingA);
            System.out.println("qa " + formA.formName);
            System.out.println("soundclip " + soundclip);
            System.out.println("qb " + formB.formName);
            System.out.println("formFinal " + formFinal);
            System.out.println();


        } catch (Exception e) {
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("Problem on line: " + xpp.getLineNumber());
            e.printStackTrace();
            throw e;
        }
    }

    private String parseManifestName()
            throws Exception
    {
        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Xml file must start with <study-name> tag");
        if (!xpp.getName().equals("study-name"))
            throw new Exception("Xml file must start with <study-name> tag, right now it's " + xpp.getName());
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Xml <study-name> must have text content");
        String manifestName = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Xml <study-name> tag must end with </study-name> tag");

        xpp.next();
        checkWhitespace();

        return manifestName;
    }

    private File findFile(String filename, File[] files)
            throws Exception {
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(filename))
                return files[i];
        }
        throw new Exception("file: \"" + filename + "\" as shown in the manifest is not in the folder");
    }

    private String parseTag(String tag, Boolean allowEmpty)
            throws Exception
    {
        String content = "";

        checkWhitespace();
        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " expected <" + tag + "> tag here");
        if (!xpp.getName().equals(tag))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " expected <" + tag + ">, right now it's <" + xpp.getName() + ">");
        if (xpp.next() != XmlPullParser.TEXT && !allowEmpty)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <" + tag + "> must have text content");
        if (allowEmpty && xpp.getEventType() == XmlPullParser.END_TAG) {
            xpp.next();
            checkWhitespace();

            return "";
        } else {
            content = xpp.getText();
            if (xpp.next() != XmlPullParser.END_TAG)
                throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <" + tag + "> tag must end with </" + tag + "> tag");
            xpp.next();
            checkWhitespace();
            return content;
        }
    }

    private void checkWhitespace()
            throws XmlPullParserException, IOException
    {
        if (xpp.getEventType() == XmlPullParser.TEXT && xpp.isWhitespace())
            xpp.next();
    }






    class CreateNewStudy extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            Form[] forms = { baseline, formA, formB, formFinal };


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Log.w("create study", "test 1");

            try
            {
                JSONObject jsonStudy = new JSONObject();

                Log.w("create study", "test 2");
                params.add(new BasicNameValuePair("name", studyName));
                Log.w("create study", "test 3");
                params.add(new BasicNameValuePair("physio_duration", Integer.toString(readingA)));
                Log.w("create study", "test 4");
                params.add(new BasicNameValuePair("intervention_filename", audioFileName));
                Log.w("create study", "test 5");

                //jsonStudy.put("name", studyName);
                //jsonStudy.put("physio_duration", readingA);
                //jsonStudy.put("intervention_filename", "");

                JSONArray jsonForms = new JSONArray();
                for (Form form: forms)
                {
                    JSONObject jsonForm = new JSONObject();

                    // Do we need these?
                    //jsonForm.put("form_name", form.formName);
                    //jsonForm.put("form_desc", form.formDesc);

                    JSONArray jsonQuestions = new JSONArray();
                    Log.w("create study", "test 5.00001");
                    if (form == null)
                    {
                        Log.w("create study", "form is null");
                        continue;
                    }
                    Log.w("create study", form.formName);
                    for (Prompt prompt: form.prompts)
                    {
                        Log.w("create study", "test 5.00002");
                        ArrayList<Question> qs;
                        if(prompt.promptType.equals("likert")) // MIGHT BE FIXED
                        {
                            qs = prompt.likertQuestions;
                            Log.w("create study", "test 5.00003");
                        }
                        else
                        {
                            qs = new ArrayList<Question>(Arrays.asList(prompt.question));
                            Log.w("create study", "test 5.000035");
                        }

                        Log.w("create study", "test 6");
                        for (Question q: qs )
                        {
                            JSONObject jsonQuestion = new JSONObject();

                            jsonQuestion.put("image_filename", "the_word_blank");
                            jsonQuestion.put("question_type", prompt.promptType);
                            jsonQuestion.put("question_name", prompt.name);
                            jsonQuestion.put("likert_description", prompt.likertDescription);
                            jsonQuestion.put("text", q.getText());
                            jsonQuestion.put("is_reversed", q.isReverse().toString());
                            jsonQuestion.put("is_positive", q.isPositive().toString());


                            if(!prompt.promptType.equals("short"))
                            {
                                JSONArray jsonAnswers = new JSONArray();
                                for(Option op : prompt.options)
                                {
                                    JSONObject jsonAnswer = new JSONObject();

                                    jsonAnswer.put("text", op.choice);
                                    jsonAnswer.put("score", op.score);
                                    jsonAnswer.put("is_text", op.textBox.toString());

                                    jsonAnswers.put(jsonAnswer);
                                }
                                jsonQuestion.put("answer_choices", jsonAnswers);
                            }
                            else
                            {
                                //jsonQuestions.put(jsonQuestion);
                            }

                            jsonQuestions.put(jsonQuestion);
                        }
                    }
                    jsonForm.put("questions", jsonQuestions);

                    jsonForms.put(jsonForm);
                }

                //jsonStudy.put("questionnaires", jsonForms);


                String fixed = jsonForms.toString().replaceAll("'", "*");
                params.add(new BasicNameValuePair("questionnaires", fixed));
                //params.add(new BasicNameValuePair("study", jsonStudy.toString()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product

                    /*
                    Intent i = new Intent(AddUser.this, ManageUserAccounts.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                    */

                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}