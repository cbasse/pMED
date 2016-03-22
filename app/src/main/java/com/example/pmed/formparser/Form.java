package com.example.pmed.formparser;

/**
 * Created by calebbasse on 3/22/16.
 */
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

public class Form
{
    public Prompt[] prompts;
    public String formName;
    XmlPullParser xpp;

    public Form(File formFile)
    {
        try {
            //String fileContent = readXMLFile(formFile);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput( new FileReader (formFile) );
            xpp.next();
            //System.out.println(XmlPullParser.START_TAG + " " + XmlPullParser.END_TAG + " " + XmlPullParser.END_DOCUMENT + " " + XmlPullParser.START_DOCUMENT + XmlPullParser.TEXT);

            formName = parseFormName();

            ArrayList<Prompt> temp_prompts = new ArrayList<Prompt>();

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                temp_prompts.add(parsePrompt());
            }
            prompts = temp_prompts.toArray(new Prompt[1]);

            System.out.println("\n\nquestionnaire name: " + formName + "\n");
            for (int i = 0; i < prompts.length; i++)
            {
                System.out.println("question type: " + prompts[i].promptType);
                System.out.println("name: " + prompts[i].name);
                System.out.println("question: " + prompts[i].question);
                if (prompts[i].promptType != "short")
                {
                    for (int j = 0; j < prompts[i].options.length; j++)
                    {
                        System.out.println("option: " + prompts[i].options[j].choice);
                        if (prompts[i].options[j].textBox) System.out.println("\t[textbox]");
                    }
                }
                System.out.println();
            }


        } catch (Exception e) {
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("Problem on line: " + xpp.getLineNumber());
            e.printStackTrace();
            System.out.println(formName);
            System.exit(0);
        }


    }

    private Prompt parsePrompt()
            throws Exception
    {
        String promptType = xpp.getName();
        Prompt prompt;

        if (promptType.equals("likert"))
            prompt = parsePromptStandard("likert");
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
        String question = parseQuestion();
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
            return new Prompt(promptType, promptName, "", null);
        }
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

    private String parseQuestion()
            throws Exception
    {
        checkWhitespace();

        if (xpp.getEventType() != XmlPullParser.START_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + "  must start with <q> tag");
        if (!xpp.getName().equals("q"))
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Prompt must start with <q> tag, right now it's " + xpp.getName());
        if (xpp.next() != XmlPullParser.TEXT)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <q> must have text content");
        String question = xpp.getText();
        if (xpp.next() != XmlPullParser.END_TAG)
            throw new Exception("Line: " + (xpp.getLineNumber()+1) + " Xml <q> tag must end with </name> tag");

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

    private void checkWhitespace()
            throws XmlPullParserException, IOException
    {
        if (xpp.getEventType() == XmlPullParser.TEXT && xpp.isWhitespace())
            xpp.next();
    }

}
