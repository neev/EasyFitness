package com.neeraja.android.easyfit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by neeraja on 3/13/2016.
 */
public class WorkoutHistoryListAdapter extends CursorAdapter {

    public static final int COL_DESC = 2;
    public static final int COL_DURATION = 1;
    public static final int COL_YEAR = 3;
    public static final int COL_MONTH = 4;
    public static final int COL_DATE = 5;
    public static final int COL_DAY = 6;

    private String EASYFIT_HASHTAG = "#EasyFit";
    public WorkoutHistoryListAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.list_item_workout_history, parent, false);
        ViewHolderWorkoutHistory mHolder = new ViewHolderWorkoutHistory(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolderWorkoutHistory mHolder = (ViewHolderWorkoutHistory) view.getTag();


        mHolder.recorded_workout_desc.setText(cursor.getString(COL_DESC));
        mHolder.recorded_workout_duration.setText(cursor.getString(COL_DURATION)+" min");
        String _year = cursor.getString(COL_YEAR);
        String _month = cursor.getString(COL_MONTH);
        String _date = cursor.getString(COL_DATE);
        String _day = cursor.getString(COL_DAY);
        StringBuilder _displayDate = (new StringBuilder()

                .append(Utilities.getMonthName(Integer.parseInt(_month)))
                .append(" ")
                .append(_date).append(", ")
                .append(_year).append(" "));

        mHolder.recorded_date.setText(_displayDate);
        System.out.println("HISTORY VALUES: " + cursor.getString(COL_DESC) + cursor.getString
                (COL_DURATION));
        mHolder.share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //add Share Action
                context.startActivity(createShareForecastIntent(mHolder.recorded_date.getText()+" "
                        +mHolder.recorded_workout_desc.getText()+" "+mHolder.recorded_workout_duration.getText() + " "));
            }
        });

       /* mHolder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Share Action
                context.startActivity(createShareForecastIntent(mHolder.recorded_date.getText()+ " "
                        + mHolder.recorded_workout_desc.getText() + " " +mHolder.recorded_workout_duration.getText() + " "));
            }
        });*/

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + EASYFIT_HASHTAG);
        return shareIntent;
    }


}
