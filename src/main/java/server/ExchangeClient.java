package server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.bank.currencyRates.Currency;
import io.grpc.bank.currencyRates.CurrencyRate;
import io.grpc.bank.currencyRates.CurrencyRequest;
import io.grpc.bank.currencyRates.ExchangerGrpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExchangeClient {
    private static final Logger logger = Logger.getLogger(ExchangeClient.class.getName());

    private final ManagedChannel channel;
    private final ExchangerGrpc.ExchangerStub stub;
    private final Map<Currency, Float> rates = new ConcurrentHashMap<>();
    private final List<Currency> currenciesToSubscribe;

    public Map<Bank.Currency, Float> getRates(){
        return convertToBankCurrency(rates);
    }

    private Map<Bank.Currency, Float> convertToBankCurrency(Map<Currency, Float> in){
        Map<Bank.Currency, Float> out = new HashMap<>();
        for(Map.Entry<Currency, Float> rate: in.entrySet()){
            if(rate.getKey().equals(Currency.EUR))
                out.put(Bank.Currency.EUR, rate.getValue());
            if(rate.getKey().equals(Currency.GBP))
                out.put(Bank.Currency.GBP, rate.getValue());
            if(rate.getKey().equals(Currency.USD))
                out.put(Bank.Currency.USD, rate.getValue());
            if(rate.getKey().equals(Currency.PLN))
                out.put(Bank.Currency.PLN, rate.getValue());
        }
        return out;
    }

    public ExchangeClient(String host, int port, List<Currency> currenciesToSubscribe) {
        this.currenciesToSubscribe = currenciesToSubscribe;
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();

        stub = ExchangerGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void startUpdaters(){
        this.currenciesToSubscribe.forEach(currency -> {
            System.out.println("Starting updater for currency: " + currency);
            CurrencyRequest request = CurrencyRequest.newBuilder().setCurrency(currency).build();
            stub.getRates(request, new RatesObserver());
        });
    }

    private class RatesObserver implements StreamObserver<CurrencyRate> {

        @Override
        public void onNext(CurrencyRate value) {
            System.out.println("" + value.getCurrency() + value.getValue());
            rates.put(value.getCurrency(), value.getValue());
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Problem with currency service");
        }

        @Override
        public void onCompleted() {
            // never happens actually
        }
    }

}

