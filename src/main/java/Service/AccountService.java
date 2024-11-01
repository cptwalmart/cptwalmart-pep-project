package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;
    
    public AccountService(){
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

    public Account getAccount(Account account){
        return accountDAO.getAccount(account);
    }

    public Account addAccount(Account account){
        return accountDAO.createAccount(account);
    }

    public boolean usernameExists(String username){
        return accountDAO.checkAccount(username);
    }
}
