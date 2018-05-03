package server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.bank.currencyRates.Currency;
import io.grpc.bank.currencyRates.CurrencyRate;
import io.grpc.bank.currencyRates.CurrencyRequest;
import io.grpc.bank.currencyRates.ExchangerGrpc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExchangeClient {
    private static final Logger logger = Logger.getLogger(ExchangeClient.class.getName());

    private final ManagedChannel channel;
    private final ExchangerGrpc.ExchangerBlockingStub calcBlockingStub;
    private final Map<Currency, Float> rates = new ConcurrentHashMap<>();
    private final List<Currency> currenciesToSubscribe;
    private List<Thread> threads;

    public Map<Currency, Float> getRates(){
        return rates;
    }

    public ExchangeClient(String host, int port, List<Currency> currenciesToSubscribe) {
        this.currenciesToSubscribe = currenciesToSubscribe;
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();

        calcBlockingStub = ExchangerGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void startUpdaters(){
        threads = this.currenciesToSubscribe.stream().map(currency -> {
            System.out.println("Starting updater for currency: " + currency);
            CurrencyRequest request = CurrencyRequest.newBuilder().setCurrency(currency).build();
            Iterator<CurrencyRate> result = calcBlockingStub.getRates(request);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (result.hasNext()) {
                            CurrencyRate newRate = result.next();
                            updateCurrency(currency, newRate.getValue());
                            System.out.println(currency + " " + newRate.getValue());
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            t.start();
            return t;
        }).collect(Collectors.toList());
    }

    public void waitForUpdaters(){
        try {
            threads.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }finally {
            try {
                shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void updateCurrency(Currency currency, float newValue){
        rates.put(currency, newValue);
    }

}

