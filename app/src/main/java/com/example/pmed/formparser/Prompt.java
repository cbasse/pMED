package com.example.pmed.formparser;

/**
 * Created by calebbasse on 3/22/16.
 */

public class Prompt
{
    public final String promptType;
    public final String name;
    public final Question question;
    public final Question[] likertQuestions; //optional
    public final Option[] options; //optional
    public final String likertDescription;

    public String answerText = "";

    public Prompt(String type, String name, Question question, Option[] options)
    {
        promptType = type;
        this.name = name;
        this.question = question;
        this.options = options;
        this.likertQuestions = null;
        this.likertDescription = null;
    }

    public Prompt(String type, String name, Question question, Option[] options, Question[] likertQuestions, String likertDescription)
    {
        promptType = type;
        this.name = name;
        this.question = question;
        this.options = options;
        this.likertQuestions = likertQuestions;
        this.likertDescription = likertDescription;
    }

}

