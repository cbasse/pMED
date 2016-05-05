package com.example.pmed.formparser;


import java.util.ArrayList;

/**
 * Created by calebbasse on 4/11/16.
 */
public class Question
{
    private String text;
    private Boolean reverse = false;
    public Boolean positive = null;
    public String id;

    public Question() { }

    public Question(String text)
    {
        this.text = text;
    }

    public Question(String text, Boolean reverse)
    {
        this.text = text;
        this.reverse = reverse;
    }

    public Question(String text, Boolean reverse, Boolean positive)
    {
        this.text = text;
        this.reverse = reverse;
        this.positive = positive;
    }

    public String getText() {
        return text;
    }

    public Boolean isNeutral() {
        if (positive == null)
            return true;
        else
            return false;
    }

    public Boolean isPositive() {
        if (positive != null && positive == true)
            return true;
        else
            return false;
    }

    public Boolean isReverse() {
        return reverse;
    }
}