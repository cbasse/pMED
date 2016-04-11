package com.example.pmed.formparser;

/**
 * Created by calebbasse on 3/22/16.
 */
public class Prompt
{
    public final String promptType;
    public final String name;
    public final String question;
    public final Option[] options;
    public String answer = "";
    public String answerText = "NA"; //optional for checkboxes with text responses

    public Prompt(String type, String name, String question, Option[] options)
    {
        promptType = type;
        this.name = name;
        this.question = question;
        this.options = options;
    }

}
