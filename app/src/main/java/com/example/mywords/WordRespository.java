package com.example.mywords;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

class WordRespository {
    private LiveData<List<Word>> allWordsLife;
    private WordDao wordDao;
    WordRespository(Context context) {
        WordDataBase wordDataBase=WordDataBase.getDatabase(context.getApplicationContext());
        wordDao = wordDataBase.getWordDao();
        allWordsLife= wordDao.getAllWordslife();
    }

    LiveData<List<Word>> getAllWordsLife() {
        return allWordsLife;
    }
    LiveData<List<Word>> findWordsWithPattern(String pattern) {
        return wordDao.findWordsWithPattern("%" + pattern + "%");
    }
    void insetWords(Word... words){
        new InsetAsynctTask(wordDao).execute(words);
    }

    void deleteWords(Word... words){
        new DeleteAsynctTask(wordDao).execute(words);
    }

    void deleteAllWords(){
        new DeleteAllAsynctTask(wordDao).execute();
    }

    void updataWords(Word... words){
        new UpdataAsynctTask(wordDao).execute(words);
    }

    private static class InsetAsynctTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        InsetAsynctTask(WordDao wordDao){
            this.wordDao =wordDao;
        }
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insetWords(words);
            return null;
        }
    }
    private static class UpdataAsynctTask extends AsyncTask<Word,Void,Void> {
        private WordDao wordDao;

        UpdataAsynctTask(WordDao wordDao){
            this.wordDao =wordDao;
        }
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updataWords(words);
            return null;
        }
    }

    private static class DeleteAsynctTask extends AsyncTask<Word,Void,Void>{
        private WordDao wordDao;

        DeleteAsynctTask(WordDao wordDao){
            this.wordDao =wordDao;
        }
        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    private static class DeleteAllAsynctTask extends AsyncTask<Void,Void,Void>{
        private WordDao wordDao;

        DeleteAllAsynctTask(WordDao wordDao){
            this.wordDao =wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
