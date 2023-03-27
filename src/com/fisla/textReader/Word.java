// Christian Fisla

package com.fisla.textReader;

// Word Class
public class Word implements Comparable<Word> {

    // Set fields
    private String word;
    private int frequency = 1;

    // Constructor
    public Word(String word) {
        this.word = word;
    }

    // GETTERS AND SETTERS
    public String getWord() {
        return word;
    }
    public void increaseFrequency() {
        this.frequency++;
        System.out.println(this.frequency);
    }
    public int getFrequency() {
        return frequency;
    }

    // Define default sorting criteria
    @Override
    public int compareTo(Word o) {
        return this.word.compareTo(o.getWord());
    }
}
