package server;

import com.zeroc.Ice.Current;

public class AccountI implements Bank.Account {

    private final String firstname;
    private final String lastname;
    private final long pesel;
    private final float income;

    public AccountI(String firstname, String lastname, long pesel, float income) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.pesel = pesel;
        this.income = income;
    }

    @Override
    public float getBalance(Current current) {
        return income;
    }

    @Override
    public float getCredit(Current current) {
        return 3932;
    }
}
