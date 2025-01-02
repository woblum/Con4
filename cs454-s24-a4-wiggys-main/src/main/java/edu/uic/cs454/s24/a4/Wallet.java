package edu.uic.cs454.s24.a4;

import java.util.HashSet;
import java.util.Set;

public abstract class Wallet<C extends Coin> implements Runnable {

    private final HashSet<C> contents = new HashSet<>();
    protected final Thread allowedThread;
    private boolean exception = false;

    private final Action STOP = new Action(Action.Direction.CONTENTS, null, null);

    public Wallet() {
        this.allowedThread = new Thread(this);
        this.allowedThread.setDaemon(true);
        this.allowedThread.setUncaughtExceptionHandler( (Thread thread, Throwable throwable) -> {
            System.err.println(throwable.getMessage());
            throwable.printStackTrace();
            exception = true;
        });
    }

    public final void startThread() {
        this.allowedThread.start();
    }

    public final void run() {
        while (true) {
            Action a = getAction();

            if (a == STOP)
                return;

            switch (a.getDirection()) {
                case ADD:
                    addCoins((Set<C>) a.getTarget(), a.getResult());
                    break;
                case REDEEMED:
                    redeemCoins((Set<C>) a.getTarget(), a.getResult());
                    break;
                case RENT:
                    payRent((Set<C>) a.getTarget(), a.getResult());
                    break;
                case CONTENTS:
                    contents(a.getResult());
                    break;
                case MOVE_IN:
                    moveIn((Set<C>) a.getTarget(), a.getResult());
                    break;
                case MOVE_OUT:
                    moveOut((Set<C>) a.getTarget(), a.getResult());
                    break;
                default:
                    throw new Error("Unknown operation");
            }
        }
    }

    public final void addCoins(Set<C> coins) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.contents.addAll(coins);
    }

    public final void removeCoins(Set<C> coins) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.contents.removeAll(coins);
    }

    public final Set<C> getContents() {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        return new HashSet<>(this.contents);
    }

    public final void stopThread() {
        if (!this.allowedThread.isAlive())
            throw new Error("Thread already stopped, maybe due to an exception?");

        this.submitAction(STOP);

        while (this.allowedThread.isAlive()) {
            try {
                this.allowedThread.join();
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    public final boolean didThrowException() {
        return this.exception;
    }

    public abstract void submitAction(Action a);

    protected abstract Action getAction();

    protected abstract void addCoins(Set<C> coins, Result<Boolean> result);

    protected abstract void redeemCoins(Set<C> coins, Result<Boolean> result);

    protected abstract void payRent(Set<C> coins, Result<Boolean> result);

    protected abstract void contents(Result<Set<C>> result);

    protected abstract void moveIn(Set<C> coins, Result<Boolean> result);

    protected abstract void moveOut(Set<C> coins, Result<Boolean> result);
}
