package com.example.mywords;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    public Word(String word, String meaning,Integer chineseInbisible) {
        this.word = word;
        this.meaning = meaning;
        this.chineseInbisible = chineseInbisible;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "english_word")
    private String word;
    @ColumnInfo(name = "chinese_meaning")
    private String meaning;
    @ColumnInfo(name = "chinese_inbisible")
    private Integer chineseInbisible;

    Integer getChineseInbisible() {
        return chineseInbisible;
    }

    void setChineseInbisible(Integer chineseInbisible) {
        this.chineseInbisible = chineseInbisible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
