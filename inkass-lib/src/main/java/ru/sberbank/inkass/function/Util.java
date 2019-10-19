package ru.sberbank.inkass.function;

import ru.sberbank.inkass.dto.PointDto;

public class Util {

    //    Util
    public static double calcMoneyOnThisTrip(PointDto rightPoint, double currentMoneyOnThisTrip, PointDto bankPoint) {
        double moneyOnThisTrip = 0;
        if (!rightPoint.equals(bankPoint))
            moneyOnThisTrip = currentMoneyOnThisTrip + rightPoint.getSum();
        return moneyOnThisTrip;
    }

}
