package com.example.pmed.formparser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by calebbasse on 3/22/16.
 */

public class Prompt
{
    public String promptType;
    public String name;
    public Question question;
    public ArrayList<Question> likertQuestions; //optional
    public ArrayList<Option> options; //optional
    public String likertDescription;
    public int id;

    public String answerText = "";

    public Prompt(String type, String name, Question question, Option[] options)
    {
        likertQuestions = new ArrayList<Question>();
        this.options = new ArrayList<Option>();

        promptType = type;
        this.name = name;
        this.question = question;
        if(options != null && options.length > 0)
        {
            this.options = new ArrayList<Option>(Arrays.asList(options));
        }
        this.likertQuestions = null;
        this.likertDescription = null;
    }

    public Prompt(String type, String name, Question question, Option[] options, Question[] likertQuestions, String likertDescription)
    {
        this.likertQuestions = new ArrayList<Question>();
        this.options = new ArrayList<Option>();

        promptType = type;
        this.name = name;
        this.question = question;
        if(options != null && options.length > 0)
        {
            this.options = new ArrayList<Option>(Arrays.asList(options));
        }

        if(likertQuestions != null && likertQuestions.length > 0)
        {
            this.likertQuestions = new ArrayList<Question>(Arrays.asList(likertQuestions));
        }
        this.likertDescription = likertDescription;
    }

    public Prompt()
    {
        this.likertQuestions = new ArrayList<Question>();
        this.options = new ArrayList<Option>();
    }
}

