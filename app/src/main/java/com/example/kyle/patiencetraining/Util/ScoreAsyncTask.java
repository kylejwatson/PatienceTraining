package com.example.kyle.patiencetraining.Util;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class ScoreAsyncTask extends AsyncTask<Score, Void, List<Score>> {

    public final static int TASK_GET_ALL_SCORES = 0;
    public final static int TASK_INSERT_SCORE = 3;
    public final static int TASK_UPDATE_SCORE = 2;
    private int task;
    private static AppDatabase sDatabase;
    private OnPostExecuteListener listener;
    public ScoreAsyncTask(Context context, int task, OnPostExecuteListener listener){
        this.task = task;
        this.listener = listener;
        sDatabase = AppDatabase.getInstance(context);
    }

    public ScoreAsyncTask(Context context, int task){
        this.task = task;
        this.listener = new OnPostExecuteListener() {
            @Override
            public void onPostExecute(List<Score> list) {
                //
            }
        };
        sDatabase = AppDatabase.getInstance(context);
    }
    @Override
    protected List<Score> doInBackground(Score... scores) {
        switch (task){
            case TASK_INSERT_SCORE:
                sDatabase.scoreDao().insertScore(scores);
                break;
            case TASK_UPDATE_SCORE:
                sDatabase.scoreDao().updateScores(scores);
                break;
            case TASK_GET_ALL_SCORES:
                return sDatabase.scoreDao().getLocalScores();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Score> list) {
        super.onPostExecute(list);
        if(list != null)
            listener.onPostExecute(list);
    }

    public interface OnPostExecuteListener{
        void onPostExecute(List<Score> list);
    }
}