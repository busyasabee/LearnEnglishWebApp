package com.dmitr.romashov;

import java.util.Objects;

/**
 * Created by Дмитрий on 11.06.2017.
 */
public class Word implements Comparable{
    // не знаю, можно ли будет получить не public поля из jsp
    private int wordId;
    private String englishName;
    private String russianName;
    private int knowledge;
    private String transcription;
    private String partOfSpeech;

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public Word(String englishName, String russianName, String transcription, String partOfSpeech, int knowledge) {
        this.englishName = englishName;
        this.russianName = russianName;
        this.transcription = transcription;
        this.partOfSpeech = partOfSpeech;
        this.knowledge = knowledge;
    }

    public Word() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(getEnglishName(), word.getEnglishName()) &&
                Objects.equals(getRussianName(), word.getRussianName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnglishName(), getRussianName());
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getRussianName() {
        return russianName;
    }

    public void setRussianName(String russianName) {
        this.russianName = russianName;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    @Override
    public int compareTo(Object o) {
        Word secondWord = (Word) o;
        return - Integer.compare(this.knowledge, secondWord.getKnowledge());
    }
}
