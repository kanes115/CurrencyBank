package server;

import io.grpc.bank.currencyRates.Currency;

import java.util.LinkedList;
import java.util.List;

public class BankServer {

    private final String host = "localhost";
    private final int exchangerPort = 50051;
    private ExchangeClient exchangeClient;
    private final int port;

    public BankServer(int port){
        this.port = port;
        startExchangeClient();
        startBankService();
    }

    private void startExchangeClient() {
            List<Currency> currencies = new LinkedList<>();
            currencies.add(Currency.EUR);
            currencies.add(Currency.GBP);
            currencies.add(Currency.PLN);
            exchangeClient = new ExchangeClient(host, exchangerPort, currencies);
            exchangeClient.startUpdaters();
            //client.waitForUpdaters();
    }

    private void startBankService(){
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize()) {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p " + String.valueOf(port));
            com.zeroc.Ice.Object object = new BankServiceI(exchangeClient);
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("SimplePrinter"));
            adapter.activate();
            communicator.waitForShutdown();
        }
    }




    public static void main(String[] args){

        BankServer bank = new BankServer(10000);
    }

}
