package edu.uic.cs454.s24.a4.solution;

import edu.uic.cs454.s24.a4.Result;

public class ResultSolution<T> extends Result<T> {
    @Override
    public synchronized void setResult(T result) {
        this.set(result);
        this.notifyAll();
    }

    @Override
    public synchronized T getResult() {
        while (!this.isReady()) {
            try {
                this.wait(50);
            } catch (InterruptedException e) {
                continue;
            }
        }
        return this.get();
    }
}
