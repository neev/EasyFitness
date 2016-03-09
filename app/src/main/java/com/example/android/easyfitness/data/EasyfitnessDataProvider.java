/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.easyfitness.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class EasyfitnessDataProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EasyfitnessDbHelper mOpenHelper;

    static final int USER = 100;
    static final int USER_WITH_WORKOUT = 101;
    static final int USER_WITH_WORKOUT_AND_DATE = 102;
    static final int WORKOUT = 300;
    static final int WORKOUT_WITHID = 301;

    private static final SQLiteQueryBuilder sUserDeatilByWorkoutIdQueryBuilder;
    static{
        sUserDeatilByWorkoutIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sUserDeatilByWorkoutIdQueryBuilder.setTables(
                EasyFitnessContract.UserDetailEntry.TABLE_NAME + " INNER JOIN " +
                        EasyFitnessContract.WorkOutEntry.TABLE_NAME +
                        " ON " + EasyFitnessContract.UserDetailEntry.TABLE_NAME +
                        "." + EasyFitnessContract.UserDetailEntry.COLUMN_USER_WORKOUT_KEY +
                        " = " + EasyFitnessContract.WorkOutEntry.TABLE_NAME +
                        "." + EasyFitnessContract.WorkOutEntry.COLUMN_WORKOUT_ID);
    }
    private static final SQLiteQueryBuilder sWORKOUTOPTIONSByWorkoutIdQueryBuilder;
    static{
        sWORKOUTOPTIONSByWorkoutIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sWORKOUTOPTIONSByWorkoutIdQueryBuilder.setTables(

                EasyFitnessContract.WorkOutEntry.TABLE_NAME );
    }

    //WORKOUT OPTIONS WITH WORKOUT_ID
    private static final String sWorkoutOptionSelection =
            EasyFitnessContract.WorkOutEntry.TABLE_NAME+ "."+
                    EasyFitnessContract.WorkOutEntry.COLUMN_WORKOUT_ID + " = ?";

    //USERDETAILS WITH USER AUTH ID
    private static final String sUserWithUserAuthIdSelection =
            EasyFitnessContract.UserDetailEntry.TABLE_NAME+
                    "." + EasyFitnessContract.UserDetailEntry.COLUMN_USERDEATIL_AUTHENTIFICATION_ID + " = ? ";


    private Cursor getUserNameByUserAuthIdSelection(Uri uri, String[] projection, String
            sortOrder) {
        String _UserAuthId = EasyFitnessContract.UserDetailEntry.getUserAuthIdFromUri(uri);
        //long createdDate = EasyFitnessContract.UserDetailEntry.getUserCreatedDateFromUri(uri);
        String[] selectionArgs;
        String selection;



            selection = sUserWithUserAuthIdSelection;
            selectionArgs = new String[]{_UserAuthId};
         /*else {
            selectionArgs = new String[]{_UserAuthId, Long.toString(createdDate)};
            selection = sUserWithCreatedDateSelection;
        }*/

        return sUserDeatilByWorkoutIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getWorkoutoptionDescByWorkoutIdSelection(Uri uri, String[] projection, String
            sortOrder) {
        int workoutId = EasyFitnessContract.WorkOutEntry.getWorkoutIdFromUri(uri);
        //long createdDate = EasyFitnessContract.UserDetailEntry.getUserCreatedDateFromUri(uri);
        String[] selectionArgs;
        String selection;
        selection = sWorkoutOptionSelection;
        selectionArgs = new String[]{Integer.toString(workoutId)};


        return sWORKOUTOPTIONSByWorkoutIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EasyFitnessContract.CONTENT_AUTHORITY;
        //final String aut = EasyFitnessContract.BASE_CONTENT_URI.toString();
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EasyFitnessContract.PATH_USERDETAIL, USER );
        matcher.addURI(authority, EasyFitnessContract.PATH_USERDETAIL + "/*", USER_WITH_WORKOUT );
        matcher.addURI(authority, EasyFitnessContract.PATH_USERDETAIL + "/*/#", USER_WITH_WORKOUT_AND_DATE );
        matcher.addURI(authority, EasyFitnessContract.PATH_WORKOUT, WORKOUT );
        matcher.addURI(authority, EasyFitnessContract.PATH_WORKOUT + "/*", WORKOUT_WITHID );

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new EasyfitnessDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case USER_WITH_WORKOUT_AND_DATE :
                return EasyFitnessContract.UserDetailEntry.CONTENT_ITEM_TYPE;
            case USER_WITH_WORKOUT :
                return EasyFitnessContract.UserDetailEntry.CONTENT_TYPE;
            case USER :
                return EasyFitnessContract.UserDetailEntry.CONTENT_TYPE;
            case WORKOUT :
                return EasyFitnessContract.WorkOutEntry.CONTENT_TYPE;
            case WORKOUT_WITHID :
                return EasyFitnessContract.WorkOutEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case USER_WITH_WORKOUT_AND_DATE :
            {
                retCursor = getUserNameByUserAuthIdSelection(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case USER_WITH_WORKOUT : {
                retCursor = getUserNameByUserAuthIdSelection(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case USER : {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EasyFitnessContract.UserDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "weather"
            case WORKOUT : {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EasyFitnessContract.WorkOutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case WORKOUT_WITHID : {
                retCursor =  getWorkoutoptionDescByWorkoutIdSelection(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case USER: {
                normalizeDate(values);
                long _id = db.insert(EasyFitnessContract.UserDetailEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyFitnessContract.UserDetailEntry.buildUserDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case WORKOUT: {
                long _id = db.insert(EasyFitnessContract.WorkOutEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyFitnessContract.WorkOutEntry.buildWorkoutUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case USER:
                rowsDeleted = db.delete(
                        EasyFitnessContract.UserDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WORKOUT:
                rowsDeleted = db.delete(
                        EasyFitnessContract.WorkOutEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(EasyFitnessContract.UserDetailEntry.COLUMN_USER_CREATED_DATE)) {
            long dateValue = values.getAsLong(EasyFitnessContract.UserDetailEntry.COLUMN_USER_CREATED_DATE);
            values.put(EasyFitnessContract.UserDetailEntry.COLUMN_USER_CREATED_DATE, EasyFitnessContract.normalizeDate
                    (dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case USER:
                normalizeDate(values);
                rowsUpdated = db.update(EasyFitnessContract.UserDetailEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case WORKOUT:
                rowsUpdated = db.update(EasyFitnessContract.WorkOutEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case WORKOUT:
                db.beginTransaction();
                int returnwCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(EasyFitnessContract.WorkOutEntry.TABLE_NAME, null,
                                value);
                        if (_id != -1) {
                            returnwCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnwCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}