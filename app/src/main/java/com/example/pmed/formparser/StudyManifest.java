package com.example.pmed.formparser;

/**
 * Created by calebbasse on 4/11/16.
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

public class StudyManifest {
    public String studyName;
    public Form baseline;
    public int days;
    public int readingA;
    public Form formA;
    public String soundclip;
    public int readingB;
    public Form formB;
    public Form formFinal = null;

    private XmlPullParser xpp;

    public StudyManifest(String manifestFolder) {
        try {
            //String fileContent = readXMLFile(formFile);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();

            File folder = new File(manifestFolder);
            if (!folder.isDirectory()) throw new Exception("Items must be a folder containing Study documents");
            File[] files = folder.listFiles();


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

            soundclip = parseTag("soundclip", false);

            filename = parseTag("questionnaire-b", false);
            formB = new Form(findFile(filename, files));

            readingB = Integer.parseInt(parseTag("reading-b", false));

            if (xpp.getEventType() != XmlPullParser.END_DOCUMENT && xpp.getName().equals("questionnaire-final")) {
                filename = parseTag("questionnaire-final", true);
                if (!filename.equals("")) {
                    formB = new Form(findFile(filename,files));
                }
            }



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
            System.exit(0);
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


}