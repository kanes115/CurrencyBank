package server;

import Bank.AccountPrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectNotExistException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BankServiceI implements Bank.BankService {

    private ExchangeClient exchangeClient;
    private List<Long> users = new CopyOnWriteArrayList<>();
    public static long premiumMinIncome = 20000;

    public BankServiceI(ExchangeClient exchangeClient){
        this.exchangeClient = exchangeClient;
    }

    @Override
    public long createAccount(String firstName, String lastName, long pesel, float income, Current current) throws Bank.userAlreadyExists {
        if(users.contains(pesel))
            throw new Bank.userAlreadyExists();
        String accountType;
        if(income > premiumMinIncome)
            accountType = "premium";
        else
            accountType = "standard";
        Identity identity = new Identity(String.valueOf(pesel), accountType);
        AccountPrx.uncheckedCast(current.adapter.add(new AccountI(firstName, lastName, pesel, income, exchangeClient), identity));
        users.add(pesel);
        return createUid(pesel, accountType);
    }

    @Override
    public AccountPrx getAccount(long uid, Current current) throws Bank.invalidUid {
        String accountType = getAccountType(uid);
        long pesel = getPesel(uid);
        Identity identity = new Identity(String.valueOf(pesel), accountType);
        try {
            return AccountPrx.uncheckedCast(current.adapter.createProxy(identity)); // here exception
        }catch(ObjectNotExistException e){
            throw new Bank.invalidUid();
        }
    }

    private String getAccountType(long uid){
        if(uid % 2 == 0)
            return "standard";
        return "premium";
    }

    private long getPesel(long uid){
        return uid / 10;
    }

    private long createUid(long pesel, String accountType){
        long type;
        if(accountType.equals("premium"))
            type = 1;
        else
            type = 0;
        return pesel * 10 + type;
    }

}
