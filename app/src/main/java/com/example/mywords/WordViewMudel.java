package com.example.mywords;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class WordViewMudel extends AndroidViewModel {

    private WordRespository respository;

    public WordViewMudel(@NonNull Application application) {
        super(application);
        respository = new WordRespository(application);

    }

    LiveData<List<Word>> getAllWordsLive() {
        return respository.getAllWordsLife();
    }
    LiveData<List<Word>> findWordsWithPattern(String pattern) {
        return respository.findWordsWithPattern(pattern);
    }

    void insetWords(Word... words){
        respository.insetWords(words);
    }

    void deleteWords(Word... words){
        respository.deleteWords(words);
    }

    void deleteAllWords(){
        respository.deleteAllWords();
    }

    void updataWords(Word... words){
        respository.updataWords(words);
    }
}
