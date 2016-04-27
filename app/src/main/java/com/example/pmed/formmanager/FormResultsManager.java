package com.example.pmed.formmanager;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.pmed.formparser.Form;
import com.example.pmed.formparser.Option;
import com.example.pmed.formparser.Prompt;
import com.example.pmed.formparser.Question;

import java.util.HashMap;

/**
 * Created by calebbasse on 4/14/16.
 */
public class FormResultsManager implements Parcelable {
    public HashMap<String, String> results;


    public FormResultsManager(Form form) {
        results = new HashMap<>();

        results.put("form_name", form.formName);

        for (Prompt p : form.prompts) {
            if(p.promptType.equals("likert"))
            {
                for(Question q : p.likertQuestions)
                {
                    results.put(q.id, "");
                }
            }
            else
            {
                results.put(p.question.id, "");
            }
            /*
            if (p.promptType.equals("mult")) {
                results.put(p.question.id, "");
                for (Option op : p.options) {
                    if (op.textBox) {
                        //results.put(p.name + "_text", "");
                        break;
                    }
                }
            } else if (p.promptType.equals("check")) {
                for (Option op : p.options) {
                    results.put(p.name + "_" + op.choice, "");
                    if (op.textBox) {
                        results.put(p.name + "_" + op.choice + "_text", "");
                    }
                }
            } else if (p.promptType.equals("short")) {
                results.put(p.name, "");
            } else if (p.promptType.equals("likert")) {
                for (Question q : p.likertQuestions) {
                    results.put(p.name + "_" + q.getText(), "");
                }
            }
            */
        }

        results.put("likert_pos_affects", "0");
        results.put("likert_neg_affects", "0");

    }



    public FormResultsManager(Parcel p) {
        results = p.readHashMap(ClassLoader.getSystemClassLoader());
    }

    public void setValue(String key, String value) {
        if (results.containsKey(key))
            results.put(key,value);
        else
            throw new NullPointerException("key: " + key + "does not exist.");
    }

    public String getValue(String key) {
        if (results.containsKey(key))
            return results.get(key);
        else
            throw new NullPointerException("key: " + key + "does not exist.");

    }


    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeMap(results);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FormResultsManager> CREATOR = new Parcelable.Creator<FormResultsManager>() {
        public FormResultsManager createFromParcel(Parcel in) {
            return new FormResultsManager(in);
        }

        public FormResultsManager[] newArray(int size) {
            return new FormResultsManager[size];
        }
    };

    public String toString() {
        return results.toString();
    }
}
