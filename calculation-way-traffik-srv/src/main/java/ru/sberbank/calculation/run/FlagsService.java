package ru.sberbank.calculation.run;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class FlagsService {
    private final AtomicBoolean endCalcFlag;
    private final AtomicBoolean readyForRun;

    public FlagsService() {
        endCalcFlag = new AtomicBoolean(false);
        readyForRun = new AtomicBoolean(true);
    }

    public AtomicBoolean getEndCalcFlag() {
        return endCalcFlag;
    }

    public AtomicBoolean getReadyForRun() {
        return readyForRun;
    }
}
