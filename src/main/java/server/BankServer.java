package server;

import io.grpc.bank.currencyRates.Currency;

import java.util.LinkedList;
import java.util.List;

public class BankServer {

    private final String host = "localhost";
    private final int port = 50051;
    private ExchangeClient exchangeClient;

    public BankServer(String[] args){
        startExchangeClient();
        startBankService(args);
    }

    private void startExchangeClient() {
            List<Currency> currencies = new LinkedList<>();
            currencies.add(Currency.EUR);
            currencies.add(Currency.GBP);
            exchangeClient = new ExchangeClient(host, port, currencies);
            exchangeClient.startUpdaters();
            //client.waitForUpdaters();
    }

    private void startBankService(String[] args){
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p 10000");
            com.zeroc.Ice.Object object = new BankServiceI(exchangeClient);
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }




    public static void main(String[] args){

        BankServer bank = new BankServer(args);
    }

}
