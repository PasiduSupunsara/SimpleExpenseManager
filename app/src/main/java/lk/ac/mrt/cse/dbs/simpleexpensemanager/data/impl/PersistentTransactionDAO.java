package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO{



    public PersistentTransactionDAO(@Nullable Context context) {
        super(context , "190697A.db", null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountTable="CREATE TABLE 'ACCOUNTS' ('accountNo' TEXT PRIMARY KEY, 'bankName' TEXT NOT NULL, 'accountHolderName' TEXT NOT NULL, 'balance' REAL NOT NULL )";
        String createTransactionTable= "CREATE TABLE 'TRANSACTIONS' ('tansaction_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'date' TEXT NOT NULL, 'accountNo' TEXT NOT NULL, 'expenseType' TEXT NOT NULL, 'amount' REAL NOT NULL )";


        sqLiteDatabase.execSQL(createAccountTable);
        sqLiteDatabase.execSQL(createTransactionTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String strDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
        cv.put("date", strDate);
        cv.put("accountNo", accountNo);
        String strExpType = expenseType.equals(ExpenseType.EXPENSE)? "EXPENSE" : "INCOME";
        cv.put("expenseType", strExpType);
        cv.put("amount", amount);

        db.insert("TRANSACTIONS", null,cv);

        System.out.println(new Transaction(date,accountNo,expenseType,amount));

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();

        String queryString = "SELECT * from TRANSACTIONS";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                try {
                    Date date =new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(1));

                    String accountNo = cursor.getString(2);
                    String expType = cursor.getString(3);
                    ExpenseType expenseType = null;
                    if(expType.equals("EXPENSE")){
                        expenseType = ExpenseType.EXPENSE;
                    }
                    else if(expType.equals("INCOME")){
                        expenseType = ExpenseType.INCOME;
                    }
                    double amount = cursor.getDouble(4);

                    Transaction newTransaction = new Transaction(date, accountNo, expenseType, amount);

                    transactions.add(newTransaction);

                    System.out.println(newTransaction);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();

        String queryString = "SELECT date,accountNo,expenseType,amount from TRANSACTIONS";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            int i =1;
            do{
                try {
                    Date date =new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(0));

                    String accountNo = cursor.getString(1);
                    String expType = cursor.getString(2);
                    ExpenseType expenseType = null;
                    if(expType.equals("EXPENSE")){
                        expenseType = ExpenseType.EXPENSE;
                    }
                    else if(expType.equals("INCOME")){
                        expenseType = ExpenseType.INCOME;
                    }
                    double amount = cursor.getDouble(3);

                    Transaction newTransaction = new Transaction(date, accountNo, expenseType, amount);

                    transactions.add(newTransaction);

                    System.out.println(newTransaction);


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                i++;
            }while (cursor.moveToNext() && i < limit);
        }
        cursor.close();
        db.close();
        return  transactions;
    }
}