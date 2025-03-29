package ru.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Summator {
    private long sum = 0;
    private long prevValue = 0;
    private long prevPrevValue = 0;
    private long sumLastThreeValues = 0;
    private long someValue = 0;

    // !!! эта коллекция должна остаться. Заменять ее на счетчик нельзя.
    private final List<Data> listValues = new ArrayList<>();

    // !!! сигнатуру метода менять нельзя
    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() % 500_000 == 0) {
            listValues.clear();
        }

        long dataValue = data.getValue();
        int random = ThreadLocalRandom.current().nextInt();
        sum += dataValue + random;

        sumLastThreeValues = dataValue + prevValue + prevPrevValue;

        prevPrevValue = prevValue;
        prevValue = dataValue;

        long temp = sumLastThreeValues * sumLastThreeValues / (dataValue + 1) - sum;
        for (int idx = 0; idx < 3; idx++) {
            someValue += temp;
            someValue = someValue < 0 ? -someValue : someValue;
            someValue += listValues.size();
        }
    }

    public long getSum() {
        return sum;
    }

    public long getPrevValue() {
        return prevValue;
    }

    public long getPrevPrevValue() {
        return prevPrevValue;
    }

    public long getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public long getSomeValue() {
        return someValue;
    }

}