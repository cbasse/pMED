package com.example.pmed.formparser;

import com.example.pmed.formparser.Prompt;

import java.io.IOException;
import java.io.StringReader;

import java.io.BufferedReader;
import java.io.LineNumberReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class Form
{
    public ArrayList<Prompt> prompts;
    public String formName;
    public String formDesc;
    private XmlPullParser xpp;

    public String questionnaireId;
    public Form() { }

    public Form(File formFile)
        throws Exception{
        try {
            prompts = new ArrayList<Prompt>();
            //String fileContent = readXMLFile(formFile);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(new FileReader(formFile));
            xpp.next();
            //System.out.println(XmlPullParser.START_TAG + " " + XmlPullParser.END_TAG + " " + XmlPullParser.END_DOCUMENT + " " + XmlPullParser.START_DOCUMENT + XmlPullParser.TEXT);

            formName = parseFormName();

            formDesc = parseTag("desc", true);
            System.out.println(formDesc);

            ArrayList<Prompt> temp_prompts = new ArrayList<Prompt>();

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                temp_prompts.add(parsePrompt());
            }
            Prompt[] temp_prompts_holder = temp_prompts.toArray(new Prompt[1]);
            prompts = new ArrayList<Prompt>(Arrays.asList(temp_prompts_holder));

            /*System.out.println("\n\nquestionnaire name: " + formName + "\n");
            for (int i = 0; i < prompts.length; i++)
            {
                if (prompts[i].promptType == "likert")
                {
                    System.out.println("question type: " + prompts[i].promptType);
                    System.out.println("name: " + prompts[i].name);
                    for (int j = 0; j < prompts[i].options.length; j++)
                    {
                        System.out.println("option: " + prompts[i].options[j].choice);
                        System.out.println("value: " + prompts[i].options[j].score);
                    }
                    for (int j = 0; j < prompts[i].likertQuestions.length; j++)
                    {
                        System.out.println("option: " + prompts[i].likertQuestions[j].getText());
                        System.out.println("reverse: " + prompts[i].likertQuestions[j].isReverse());
                        System.out.println("positive: " + prompts[i].likertQuestions[j].isPositive());
                    }
                    System.out.println();
                    continue;
                }


                System.out.println("question type: " + prompts[i].promptType);
                System.out.println("name: " + prompts[i].name);
                System.out.println("question: " + prompts[i].question.getText());
                if (prompts[i].promptType != "short")
                {
                    for (int j = 0; j < prompts[i].options.length; j++)
                    {
                        System.out.println("option: " + prompts[i].options[j].choice);
                        if (prompts[i].options[j].textBox) System.out.println("\t[textbox]");
                    }
                }
                System.out.println();
            }*/


        } catch (Exception e) {
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("Problem on line: " + xpp.getLineNumber());
            e.printStackTrace();
            System.out.println(formName);
            throw new Exception("Problem in " + formFile.getName() + "\n" + e.getMessage());
        }
    }

    private Prompt parsePrompt()
            throws Exception
    {
        String promptType = xpp.getName();
        Prompt prompt;

        if (promptType.equals("likert"))
            prompt = parsePromptLikert();
        else if (promptType.equals("mult"))
            prompt = parsePromptStandard("mult");
        else if (promptType.equals("check"))
            prompt = parsePromptStandard("check");
        else if (promptType.equals("short"))
            prompt = parsePromptStandard("short");
        else
            throw new Exception("Tag on line: " + (xpp.getLineNumber()+1) + " must be 'likert', 'short', 'mult', or 'check'");

        xpp.next();
        checkWhitespace();

        return prompt;
    }

    private Prompt parsePromptStandard(String promptType)
            throws Exception
    {
        xpp.next();
        checkWhitespace();
        String promptName = parsePromptName();
        Question question = parseQuestion();
        if (!promptType.equals("short"))
        {
            ArrayList<Option> options = new ArrayList<Option>();

            while (xpp.getEventType() != XmlPullParser.END_TAG)
            {
                options.add(parseOption());
            }

            return new Prompt(promptType, promptName, question, options.toArray(new Option[1]));
        }
        else
        {
            return new Prompt(promptType, promptName, question, null);
        }
    }

    private Prompt parsePromptLikert()
            throws Exception
    {
        xpp.next();
        checkWhitespace();
        String promptName = parsePromptName();
        String description = parsePromptDesc();

        ArrayList<Option> options = new ArrayList<Option>();
        ArrayList<Question> likertQuestions = new ArrayList<Question>();

        while (xpp.getName().equals("op"))
        {
            options.add(parseOptionLikert());
        }

        while (xpp.getEventType() != XmlPullParser.END_TAG)
        {
            String name = xpp.getName();
            switch (name) {
                case "q":
                    likertQuestions.add(new Question(parseTag("q", false), false));
                    break;
                case "q-rev":
                    likertQuestions.add(new Question(parseTag("q-rev", false), true));
                    break;
                case "q-pos":
                    likertQuestions.add(new Question(parseTag("q-pos", false), false, true));
                    break;
                case "q-pos-rev":
                    likertQuestions.add(new Question(parseTag("q-pos-rev", false), true, true));
                    break;
                case "q-neg":
                    likertQuestions.add(new Question(parseTag("q-neg", false), false, false));
                    break;
                case "q-neg-rev":
                    likertQuestions.add(new Question(parseTag("q-neg-rev", false), true, false));
                    break;
                default:
                    throw new Exception("Line: " + (xpp.getLineNumber()+1) + " tag following </op> tag must be a <op> tag or likert questionn tag(eg. <q>, <q-rev>, <q-rev-pos>, etc), right now it's " + xpp.getName());
            }
        }


        return new Prompt("likert", promptName, null, options.toArray(new Option[1]), likertQuestions.toArray(new Question[1]), description);
    }

    private String parseFormName()
            throws Exception
    {

        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Xml file must start with <name> tag");
        if (!xpp.getName().equals("name"))
            throw new Exception("Xml file must start with <name> tag, right now it's " + xpp.getName());
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Xml <name> must have text content");
        String formName = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Xml <name> tag must end with </name> tag");

        xpp.next();
        checkWhitespace();

        return formName;
    }

    private String parsePromptName()
            throws Exception
    {
        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Prompt must start with <name> tag");
        if (!xpp.getName().equals("name"))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Prompt must start with <name> tag, right now it's " + xpp.getName());
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <name> must have text content");
        String formName = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <name> tag must end with </name> tag");

        xpp.next();
        checkWhitespace();

        return formName;
    }

    private String parsePromptDesc()
            throws Exception
    {
        String formName = "";

        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " <name> tag in <likert> must be followed by a <desc> tag");
        if (!xpp.getName().equals("desc"))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " <name> tag in <likert> must be followed by a <desc> tag, right now it's " + xpp.getName());
        if (xpp.next() == XmlPullParser.TEXT)
            formName = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <desc> tag must end with </desc> tag");

        xpp.next();
        checkWhitespace();

        return formName;
    }

    private Question parseQuestion()
            throws Exception
    {
        checkWhitespace();

        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + "  must start with <q> tag");
        if (!xpp.getName().equals("q"))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Prompt must start with <q> tag, right now it's " + xpp.getName());
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <q> must have text content");
        Question question = new Question(xpp.getText());
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <q> tag must end with </q> tag");

        xpp.next();
        checkWhitespace();

        return question;
    }

    private Option parseOption()
            throws Exception
    {
        Option option = new Option();
        checkWhitespace();
        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + "  must start with <op> or <op-text> tag");
        if (!xpp.getName().equals("op") && !xpp.getName().equals("op-text"))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Prompt must start with <op> or <op-text> tag, right now it's " + xpp.getName());
        if (xpp.getName().equals("op-text"))
            option.textBox = true;
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <op> or <op-text> must have text content");
        option.choice = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <op> or <op-text> tag must end with </op> or </op-text> tag");

        xpp.next();
        checkWhitespace();

        return option;
    }

    private Option parseOptionLikert()
            throws Exception
    {
        Option option = new Option();
        xpp.next();
        checkWhitespace();

        String text = parseTag("text", false);
        String score = parseTag("score", false);

        option.choice = text;
        option.score = score;

        xpp.next();
        checkWhitespace();

        return option;
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

}