module Bank
{

    interface Account
    {
        float getBalance();
        float getCredit();
    }

    interface BankService
    {
        long createAccount(string firstname, string lastname, long pesel, float income);
        Account* getAccount(long pesel);
    }

}