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
    private ExchangeClient exchangeClient;

    public AccountI(String firstname, String lastname, long pesel, float income, ExchangeClient exchangeClient) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.pesel = pesel;
        this.income = income;
        this.exchangeClient = exchangeClient;
    }

    @Override
    public float getBalance(Current current) {
        return income;
    }

    @Override
    public Map<Currency, Float> getCredit(float amount, Current current) throws Bank.accountIsNotPremium {
        if(income < BankServiceI.premiumMinIncome)
            throw new Bank.accountIsNotPremium();
        Map<Currency, Float> res = new HashMap<>();
        float baseCost = 0.3f * amount * (1.0f - 10000 / income); // koszt to 30% kwoty pozyczki pomniejszony o procent jaki stanowi income osoby w stosunku do 10000
        for(Map.Entry<Currency, Float> rate: exchangeClient.getRates().entrySet())
            res.put(rate.getKey(), rate.getValue() * baseCost);
        return res;
    }
}
