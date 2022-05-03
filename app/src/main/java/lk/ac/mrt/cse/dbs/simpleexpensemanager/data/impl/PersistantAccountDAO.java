
package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PerAccountDAO extends SQLiteOpenHelper implements AccountDAO{



    public PerAccountDAO(@Nullable Context context) {
        super(context , "190612L.db", null, 1 );
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
    public List<String> getAccountNumbersList() {
        List<String> accNums = new ArrayList<>();

        String sqlQuery = "SELECT accountNo from ACCOUNTS";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()){
            do{
                String accountNum = cursor.getString(0);
                accNums.add(accountNum);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  accNums;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> acc = new ArrayList<>();

        String sqlQuery = "SELECT * from ACCOUNTS";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()){
            do{
                String accountNum = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);

                Account newAccount = new Account(accountNum,bankName, accountHolderName, balance);

                acc.add(newAccount);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  acc;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        String sqlQuery = "SELECT * from ACCOUNTS WHERE accountNo='"+ accountNo + "' ;";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if(cursor.moveToFirst()){

            String accountNum = cursor.getString(0);
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);

            Account acc = new Account(accountNum,bankName, accountHolderName, balance);

            cursor.close();
            db.close();
            return acc;
        }
        else {
            String msg = "Account " + accountNo + " is invalid.";

            cursor.close();
            db.close();
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public boolean addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("accountNo", account.getAccountNo());
        cv.put("bankName", account.getBankName());
        cv.put("accountHolderName", account.getAccountHolderName());
        cv.put("balance", account.getBalance());

        db.insert("ACCOUNTS", null,cv);
        return false;
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sqlQuery= "DELETE balance from ACCOUNTS where accountNo= '"+ accountNo+"' ;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuerySelect = "select balance from ACCOUNTS where accountNo = '"+ accountNo +"' ;";
        Cursor cursor = db.rawQuery(sqlQuerySelect,null);

        cursor.moveToFirst();
        double bal = cursor.getDouble(0);
        switch(expenseType){
            case EXPENSE:
                bal  -= amount;
                break;
            case INCOME:
                bal  += amount;
                break;
        }

        String sqlQueryUpdate = "UPDATE ACCOUNTS SET balance = "+ bal +" WHERE accountNo = '"+accountNo+"' ;";
        db.execSQL(sqlQueryUpdate);
        cursor.close();
        db.close();

    }
}
