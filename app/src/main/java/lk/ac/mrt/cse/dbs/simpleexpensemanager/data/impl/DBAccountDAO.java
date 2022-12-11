package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DBAccountDAO implements AccountDAO {
    private final SQLiteHelper helper ;
    private SQLiteDatabase database ;

    public DBAccountDAO(Context context) {
        helper = new SQLiteHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        database = helper.getReadableDatabase();

        String[] projection = {
                SQLiteHelper.ACCOUNT_NO
        };
        Cursor cursor = database.query(
                SQLiteHelper.TABLE_ACCOUNT,   // The table to query
                projection,                  // The array of columns to return (pass null to get all)
                null,                        // The columns for the WHERE clause
                null,                        // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                null                         // The sort order
        );
        List<String> accountNumbers = new ArrayList<String>();

        while(cursor.moveToNext()) {
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(SQLiteHelper.ACCOUNT_NO));
            accountNumbers.add(accountNumber);
        }
        cursor.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        database = helper.getReadableDatabase();

        String[] projection = {
                SQLiteHelper.ACCOUNT_NO,
                SQLiteHelper.NAME_OF_BANK,
                SQLiteHelper.NAME_OF_HOLDER,
                SQLiteHelper.BALANCE
        };
        Cursor cursor = database.query(
                SQLiteHelper.TABLE_ACCOUNT,   // The table to query
                projection,                  // The array of columns to return (pass null to get all)
                null,                        // The columns for the WHERE clause
                null,                        // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                null                         // The sort order
        );

        while (cursor.moveToNext()) {
            String accountNo = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.ACCOUNT_NO));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.NAME_OF_BANK));
            String accountHolderName = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.NAME_OF_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.BALANCE));

            Account account = new Account(accountNo, bankName, accountHolderName, balance);
            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        database = helper.getReadableDatabase();
        String[] projection = {
                SQLiteHelper.ACCOUNT_NO,
                SQLiteHelper.NAME_OF_BANK,
                SQLiteHelper.NAME_OF_HOLDER,
                SQLiteHelper.BALANCE
        };
        String selection = SQLiteHelper.ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_ACCOUNT,   // The table to query
                projection,                  // The array of columns to return (pass null to get all)
                selection,                   // The columns for the WHERE clause
                selectionArgs,               // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                null                         // The sort order
        );
        if (cursor.getCount() == 0) {
            throw new InvalidAccountException("No account found for account number: " + accountNo);
        }
        cursor.moveToFirst();

        Account account = new Account(
                cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.ACCOUNT_NO)),
                cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.NAME_OF_BANK)),
                cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.NAME_OF_HOLDER)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.BALANCE)));

        cursor.close();
        return account;


    }

    @Override
    public void addAccount(Account account) {
        database = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.ACCOUNT_NO, account.getAccountNo());
        values.put(SQLiteHelper.NAME_OF_BANK, account.getBankName());
        values.put(SQLiteHelper.NAME_OF_HOLDER, account.getAccountHolderName());
        values.put(SQLiteHelper.BALANCE, account.getBalance());

        database.insert(SQLiteHelper.TABLE_ACCOUNT, null, values);
        database.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        database = helper.getWritableDatabase();
        database.delete(SQLiteHelper.TABLE_ACCOUNT, SQLiteHelper.ACCOUNT_NO + " = ?",
                new String[]{accountNo});
        database.close();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        database = helper.getWritableDatabase();
            Account account = getAccount(accountNo);
            double balance = account.getBalance();
            if (expenseType == ExpenseType.EXPENSE) {
                balance -= amount;
            } else {
                balance += amount;
            }
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.BALANCE, balance);
            database.update(SQLiteHelper.TABLE_ACCOUNT, values, SQLiteHelper.ACCOUNT_NO + " = ?",
                    new String[]{accountNo});
            database.close();

    }
}
