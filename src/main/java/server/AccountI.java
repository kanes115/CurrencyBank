package server;

import Bank.Currency;
import com.zeroc.Ice.Current;

import java.util.HashMap;
import java.util.Map;

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
    public Map<Currency, Float> getCredit(float amount, Current current) {
        Map<Currency, Float> map = new HashMap<>();
        map.put(Currency.EUR, 56f);
        return map;
    }
}
