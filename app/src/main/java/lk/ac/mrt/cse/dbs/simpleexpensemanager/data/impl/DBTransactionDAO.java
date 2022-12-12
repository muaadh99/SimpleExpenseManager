package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.AMOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.DATE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteHelper.EXPENSE_TYPE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBTransactionDAO implements TransactionDAO {
    private final SQLiteHelper helper;
    private SQLiteDatabase database;

    public DBTransactionDAO(Context context) {
        helper = new SQLiteHelper(context);
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        database = helper.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues values = new ContentValues();
        values.put(DATE, dateFormat.format(date));
        values.put(ACCOUNT_NO, accountNo);
        values.put(EXPENSE_TYPE, String.valueOf(expenseType));
        values.put(AMOUNT, amount);

        database.insert(SQLiteHelper.TABLE_TRANSACTION, null, values);
        database.close();

    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transaction = new ArrayList<>();

        database = helper.getReadableDatabase();

        String[] projection = {
                DATE,
                ACCOUNT_NO,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_TRANSACTION,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
            Date newDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(AMOUNT));

            transaction.add(new Transaction(newDate, accountNo, ExpenseType.valueOf(expenseType), amount));
            return transaction;
        }
        cursor.close();
        return transaction;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transaction = new ArrayList<>();

        database = helper.getReadableDatabase();

        String[] projection = {
                DATE,
                ACCOUNT_NO,
                EXPENSE_TYPE,
                AMOUNT
        };

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_TRANSACTION,
                projection,
                null,
                null,
                null,
                null,
                null,
                String.valueOf(limit)
        );

        int size = cursor.getCount();

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
            Date newDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndexOrThrow(EXPENSE_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(AMOUNT));

            transaction.add(new Transaction(newDate, accountNo, ExpenseType.valueOf(expenseType), amount));

        }
        if (size > limit) {
            if (size - limit > 0) {
                transaction.subList(0, size - limit).clear();
            }
        }
        cursor.close();
        return transaction;
    }
}
