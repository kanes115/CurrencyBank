module Bank
{

    exception userAlreadyExists {}
    exception invalidUid {}
    exception accountIsNotPremium {}

    enum Currency{ EUR, USD, GBP, PLN };

    dictionary<Currency, float> credits;

    interface Account
    {
        float getBalance();
        credits getCredit(float amount)
                throws accountIsNotPremium;
    }

    interface BankService
    {
        long createAccount(string firstname, string lastname, long pesel, float income)
                throws userAlreadyExists;

        Account* getAccount(long pesel)
                throws invalidUid;
    }

}