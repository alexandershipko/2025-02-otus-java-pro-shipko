package ru.otus;


public class NumberGenerator {
    private static final int MAX_NUMBER = 10;
    private static final int MIN_NUMBER = 1;

    private int currentNumber = MIN_NUMBER;
    private boolean increasing = true;


    public int getAndUpdate() {
        int result = currentNumber;

        if (increasing) {
            if (currentNumber == MAX_NUMBER) {
                increasing = false;
                currentNumber--;
            } else {
                currentNumber++;
            }
        } else {
            if (currentNumber == MIN_NUMBER) {
                increasing = true;
                currentNumber++;
            } else {
                currentNumber--;
            }
        }

        return result;
    }

}
