package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PerAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PerTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PerExpenseManager extends ExpenseManager{

    private final Context context;
    public PerExpenseManager(Context context) throws ExpenseManagerException {
        //setup db
        this.context = context;

        //call setup
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setup() throws ExpenseManagerException {

        TransactionDAO perTransactionDAO = new PerTransactionDAO(this.context);
        setTransactionsDAO(perTransactionDAO);

        AccountDAO perAccountDAO = new PerAccountDAO(this.context);
        setAccountsDAO(perAccountDAO);


    }
}
