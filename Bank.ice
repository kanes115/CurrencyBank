module Bank
{

    enum Currency{ EUR, USD, GBP, PLN };

    dictionary<Currency, float> credits;

    interface Account
    {
        float getBalance();
        credits getCredit(float amount);
    }

    interface BankService
    {
        long createAccount(string firstname, string lastname, long pesel, float income);
        Account* getAccount(long pesel);
    }

}