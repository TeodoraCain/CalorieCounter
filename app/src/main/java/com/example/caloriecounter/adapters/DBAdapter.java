//package com.example.caloriecounter;
//
//import android.content.Context;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class DBAdapter {
//    private static final String databaseName = "foodCalories";
//    private static final int databaseVersion = 1;
//
//    private final Context context;
//    private DatabaseHelper DBHelper;
//    private SQLiteDatabase  db;
//
//    /* class adapter ***************************/
//    public DBAdapter(Context context){
//        this.context = context;
//        DBHelper = new  DatabaseHelper(this.context);
//    }
//
//    /* DBHelper ********************************/
//    private static class DatabaseHelper extends SQLiteOpenHelper {
//        DatabaseHelper(Context context){
//            super(context, databaseName, null,databaseVersion);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db){
//            try{
//               db.execSQL("CREATE TABLE IF NOT EXISTS food " +
//                            "( food_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                            " food_name VARCHAR," +
//                            " food_manufacturer VARCHAR); ");
//            }
//            catch(SQLException e){
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS food" );
//            onCreate(sqLiteDatabase);
//
//            String TAG = "Tag";
//            Log.w(TAG, "Upgrading database from version " +  i + " to "+ i1 + " will destroy all data");
//        }
//    }
//    /* open database ****************************/
//    public DBAdapter open() throws SQLException{
//        db = DBHelper.getWritableDatabase();
//        return this;
//    }
//    /* close database ****************************/
//    public void close() {
//        DBHelper.close();
//    }
//}
