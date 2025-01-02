package edu.uic.cs454.s24.a4;

import edu.uic.cs454.s24.a4.solution.BlockchainSolution;

import java.util.Set;

public abstract class Blockchain<W extends Wallet, C extends Coin> {
    public static Blockchain<?, ?> createBlockchain() {
        return new BlockchainSolution();
    }

    public abstract W createWallet(int capacity);

    public abstract C createCoin(int id);

    public abstract boolean addCoins(W wallet, Set<C> coins);

    public abstract boolean transferCoins(W from, W to, Set<C> coins);

    public abstract boolean payRent(W wallet, Set<C> coins);

    public abstract boolean redeemCoins(W wallet, Set<C> coins);

    public abstract Set<C> getCoins();

    public abstract Set<C> getCoins(W wallet);

    public abstract Result<Boolean> addCoinsAsync(W wallet, Set<C> coins);

    public abstract Result<Boolean> transferCoinsAsync(W from, W to, Set<C> coins);

    public abstract Result<Boolean> payRentAsync(W wallet, Set<C> coins);

    public abstract Result<Boolean> redeemCoinsAsync(W wallet, Set<C> coins);

    public abstract Result<Set<C>> getCoinsAsync();

    public abstract Result<Set<C>> getCoinsAsync(W wallet);
}