package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200401J.sqlite";
    private static final int VERSION = 1 ;

    //Table Names
    public static final String TABLE_ACCOUNT = "account" ;
    public static final String TABLE_TRANSACTION = "transacion" ;

    //common column names
    public static final String ACCOUNT_NO = "accountNumber";

    //column names of account table
    public static final String NAME_OF_BANK = "bankName";
    public static final String NAME_OF_HOLDER = "accHolderName";
    public static final String BALANCE = "balance";

    //column names of transaction table
    public static final String ID = "id" ;
    public static final String DATE = "date" ;
    public static final String EXPENSE_TYPE = "expenseType" ;
    public static final String AMOUNT = "amount" ;

    public SQLiteHelper( Context context) {
        super(context, DATABASE_NAME, null, VERSION );
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ACCOUNT + "(" +
                ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                NAME_OF_BANK + " TEXT NOT NULL, " +
                NAME_OF_HOLDER + " TEXT NOT NULL, " +
                BALANCE + " REAL NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TRANSACTION + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT NOT NULL, " +
                EXPENSE_TYPE + " TEXT NOT NULL, " +
                AMOUNT + " REAL NOT NULL, " +
                ACCOUNT_NO + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + TABLE_ACCOUNT + "(" + ACCOUNT_NO + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVewrsion) {
        //on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);

        //create new tables
        onCreate(sqLiteDatabase);
    }

    }

