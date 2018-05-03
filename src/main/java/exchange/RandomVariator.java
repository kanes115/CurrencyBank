package exchange;

import io.grpc.bank.currencyRates.Currency;
import io.grpc.bank.currencyRates.CurrencyRate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class RandomVariator implements Runnable {

    private final List<CurrencyRate> rates;
    CurrencyRate.Builder builder = CurrencyRate.newBuilder();
    Random generator = new Random(394);

    public RandomVariator(List<CurrencyRate> rates){
        this.rates = rates;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < rates.size(); i++){
                if(rates.get(i).getCurrency().equals(Currency.PLN))
                    continue;
                if(generator.nextDouble() < 0.3) {
                    rates.set(i, builder
                            .setCurrency(rates.get(i).getCurrency())
                            .setValue(variate(rates.get(i).getValue()))
                            .build());
                }
            }
        }
    }


    private float variate(float base){
        float ratio;
        if(generator.nextBoolean())
            ratio = 1.0f + generator.nextFloat() / 10;
        else
            ratio = 1.0f - generator.nextFloat() / 10;
        return base * ratio;
    }

    public static List<CurrencyRate> getStartingRates() {
        List<CurrencyRate> res = new CopyOnWriteArrayList<>();
        res.add(createRate(Currency.EUR, 4.1f));
        res.add(createRate(Currency.GBP, 5.2f));
        res.add(createRate(Currency.USD, 3.5f));
        res.add(createRate(Currency.PLN, 1.0f));
        return res;
    }

    private static CurrencyRate createRate(Currency currency, float value){
        return CurrencyRate.newBuilder()
                .setCurrency(currency)
                .setValue(value)
                .build();
    }
}
