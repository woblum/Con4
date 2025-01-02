package edu.uic.cs454.s24.a4.solution;

import edu.uic.cs454.s24.a4.Action;
import edu.uic.cs454.s24.a4.Coin;
import edu.uic.cs454.s24.a4.Result;
import edu.uic.cs454.s24.a4.Wallet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WalletSolution extends Wallet<CoinSolution> {
    private final int capacity;
    private final Queue<Action> actions = new LinkedList<>();

    public WalletSolution(int capacity){
        this.capacity = capacity;
    }


    @Override
    public synchronized void submitAction(Action a) {
        this.actions.offer(a);
        this.notifyAll();
    }

    @Override
    protected synchronized Action getAction() {

        while(actions.isEmpty()) {
            try {
                this.wait(50);
            } catch (InterruptedException e) {
                continue;
            }
        }
        Action nextAction = this.actions.poll();
        return nextAction;

        }




    @Override
    protected void addCoins(Set<CoinSolution> coins, Result<Boolean> result) {
        if (this.getContents().size() + coins.size() > this.capacity) {
            result.setResult(false);
            return;
        }
        for (CoinSolution c : coins) {
            if (c.status != Coin.Status.MINED) {
                result.setResult(false);
                return;
            }
        }
        for (CoinSolution c : coins) {
            c.status = Coin.Status.IN_CIRCULATION;
        }

        this.addCoins(coins);
        result.setResult(true);
        return;
    }

    @Override
    protected void redeemCoins(Set<CoinSolution> coins, Result<Boolean> result) {
        if (!this.getContents().containsAll(coins)) {
            result.setResult(false);
            return;
        }
        for (CoinSolution coin : coins) {
            if (coin.getStatus() != Coin.Status.IN_CIRCULATION) {
                result.setResult(false);
                return;
            }
        }
        for (CoinSolution coin : coins) {
            coin.status = Coin.Status.REDEEMED;
        }
        this.removeCoins(coins);

        result.setResult(true);
    }


    @Override
    protected void payRent(Set<CoinSolution> coins, Result<Boolean> result) {
        if (!this.getContents().containsAll(coins)) {
            result.setResult(false);
            return;
        }
        for (CoinSolution coin : coins) {
            if (coin.getStatus() != Coin.Status.IN_CIRCULATION) {
                result.setResult(false);
                return;
            }
        }
        for (CoinSolution coin : coins) {
            coin.status = Coin.Status.RENT;
        }
        this.removeCoins(coins);

        result.setResult(true);
    }

    @Override
    protected void contents(Result result) {
        result.setResult(this.getContents());
    }

    @Override
    protected void moveIn(Set<CoinSolution> coins, Result<Boolean> result) {
        if (this.getContents().size() + coins.size() > this.capacity) {
            result.setResult(false);
            return;
        }

        this.addCoins(coins);
        result.setResult(true);
        return;
    }

    @Override
    protected void moveOut(Set<CoinSolution> coins, Result<Boolean> result) {
        if (!this.getContents().containsAll(coins)) {
            result.setResult(false);
            return;
        }

        this.removeCoins(coins);

        result.setResult(true);
    }
}
