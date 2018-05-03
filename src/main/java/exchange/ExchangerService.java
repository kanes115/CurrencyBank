package exchange;

import io.grpc.bank.currencyRates.CurrencyRate;
import io.grpc.bank.currencyRates.CurrencyRequest;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


public class ExchangerService extends io.grpc.bank.currencyRates.ExchangerGrpc.ExchangerImplBase {

    private List<CurrencyRate> rates;
    Random generator = new Random(293);

    public ExchangerService(){
        rates = new CopyOnWriteArrayList<>(RandomVariator.getStartingRates());
        new Thread(new RandomVariator(rates)).start();
    }

    @Override
    public void getRates(CurrencyRequest request, StreamObserver<CurrencyRate> responseObserver){
        while(true){
            try {
                Optional<CurrencyRate> maybeRequestedCurrency = rates.stream().filter(e -> e.getCurrency().equals(request.getCurrency())).findFirst();
                if(!maybeRequestedCurrency.isPresent()) {
                    System.err.println("Unknown currency");
                    responseObserver.onError(new Exception("Uknown currency"));
                    continue;
                }
                responseObserver.onNext(maybeRequestedCurrency.get());
                int time = generator.nextInt(6000);
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
