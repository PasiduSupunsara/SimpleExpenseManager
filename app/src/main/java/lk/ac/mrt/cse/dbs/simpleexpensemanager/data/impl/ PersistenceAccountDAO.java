package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistenceMemoryAccountDAO implements AccountDAO {
    DBHelper dbHelper ;

    public PersistenceMemoryAccountDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DBHelper.AccountTable.COL_ACCOUNT_NO
        };

        Cursor cursor = db.query(
                DBHelper.AccountTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List accountList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String account_no = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_NO));
            accountList.add(account_no);
        }
        cursor.close();

        return accountList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        Cursor cursor = db.query(
                DBHelper.AccountTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List accountList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String account_no = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_NO));
            String bank_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BANK));
            String account_holder_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BALANCE));

            Account account = new Account(account_no,bank_name,account_holder_name,balance);
            accountList.add(account);
        }
        cursor.close();

        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DBHelper.AccountTable.COL_ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = db.query(
                DBHelper.AccountTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToFirst()) {
            String account_no = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_NO));
            String bank_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BANK));
            String account_holder_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BALANCE));

            Account account = new Account(account_no,bank_name,account_holder_name,balance);
            return account;
        }
        cursor.close();
        return null;
//        String account_no = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_NO));
//        String bank_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BANK));
//        String account_holder_name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_ACCOUNT_HOLDER));
//        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.AccountTable.COL_BALANCE));
//
//
//        return new Account(account_no, bank_name, account_holder_name, balance);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.AccountTable.COL_ACCOUNT_NO, account.getAccountNo());
        values.put(DBHelper.AccountTable.COL_ACCOUNT_HOLDER, account.getAccountHolderName());
        values.put(DBHelper.AccountTable.COL_BALANCE, account.getBalance());
        values.put(DBHelper.AccountTable.COL_BANK, account.getBankName());

        long newRowId = db.insert(DBHelper.AccountTable.TABLE_NAME, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DBHelper.AccountTable.COL_ACCOUNT_NO + " = ?";
        String[] selecionArgs = { accountNo };
        int deletedRow = db.delete(DBHelper.AccountTable.TABLE_NAME, selection, selecionArgs);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Account account = getAccount(accountNo);
        double balance = account.getBalance();
        double newBalance;

        switch (expenseType){
            case INCOME:
                newBalance = balance + amount;
                break;
            case EXPENSE:
                newBalance = balance - amount;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + expenseType);
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.AccountTable.COL_BALANCE, newBalance);

        String selection = DBHelper.AccountTable.COL_ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        int count = db.update(
                DBHelper.AccountTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}

