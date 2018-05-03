package server;

import Bank.AccountPrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;

public class BankServiceI implements Bank.BankService {

    private Identity identity2;

    @Override
    public long createAccount(String firstName, String lastName, long pesel, float income, Current current) {
        String accountType;
        if(income > 20000)
            accountType = "premium";
        else
            accountType = "standard";
        Identity identity = new Identity(String.valueOf(pesel), accountType);
        identity2 = identity;
        AccountPrx.uncheckedCast(current.adapter.add(new AccountI(firstName, lastName, pesel, income), identity));
        return createUid(pesel, accountType);
    }

    @Override
    public AccountPrx getAccount(long uid, Current current) {
        String accountType = getAccountType(uid);
        long pesel = getPesel(uid);
        Identity identity = new Identity(String.valueOf(pesel), accountType);
        return AccountPrx.uncheckedCast(current.adapter.createProxy(identity));
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
