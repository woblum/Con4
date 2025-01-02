package edu.uic.cs454.s24.a4;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_AddSingleWallet {
    @Test
    public void testMaxCapacity() {
        Blockchain b = Blockchain.createBlockchain();
        int size = 10;
        int test = 20;
        Wallet w = b.createWallet(size);

        w.startThread();

        Set<Coin> expected = new HashSet<>();

        for (int i = 0 ; i < test ; i++) {
            Coin v = b.createCoin(i);

            if (i < size) {
                expected.add(v);
                Assert.assertTrue(b.addCoins(w, Set.of(v)));
            } else {
                Assert.assertFalse(b.addCoins(w, Set.of(v)));
            }
        }

        w.stopThread();

        Assert.assertFalse(w.didThrowException());
        Assert.assertEquals(expected, w.getContents());
    }

    @Test
    public void testMaxCapacityAsync() {
        Blockchain b = Blockchain.createBlockchain();
        int size = 10;
        int test = 20;
        Wallet w = b.createWallet(size);

        w.startThread();

        Result<Boolean>[] rs = new Result[test];
        Set<Coin> expected = new HashSet<>();

        for (int i = 0 ; i < test ; i++) {
            Coin v = b.createCoin(i);
            rs[i] = b.addCoinsAsync(w, Set.of(v));
            if (i < size)
                expected.add(v);

        }

        for (int i = 0 ; i < test ; i++) {
            if (i < size)
                Assert.assertTrue(rs[i].getResult());
            else
                Assert.assertFalse(rs[i].getResult());
        }

        w.stopThread();

        Assert.assertFalse(w.didThrowException());
        Assert.assertEquals(expected, w.getContents());
    }

    @Test
    public void testCoinAlreadyInWallet() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCoinAlreadyInWallet(sequentialIndexes);
        testCoinAlreadyInWallet(shuffledIndexesList);
    }

    private void testCoinAlreadyInWallet(List<Integer> indexes) {
        int size = indexes.size();
        Blockchain b = Blockchain.createBlockchain();
        Wallet w = b.createWallet(size);

        w.startThread();

        Coin[] tests = new Coin[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = b.createCoin(i);
        }

        for (int i : indexes) {
            {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                Assert.assertTrue(b.addCoins(w, toAdd));
                Assert.assertFalse(b.addCoins(w, toAdd));
                Assert.assertFalse(b.addCoins(w, Set.of(tests[i])));
            }

            if (i > 0) {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                toAdd.add(tests[i-1]);
                Assert.assertFalse(b.addCoins(w, toAdd));
                Assert.assertFalse(b.addCoins(w, Set.of(tests[i], tests[i-1])));
            }

            {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                Coin anotherCoin = b.createCoin(size+i);
                toAdd.add(anotherCoin);
                Assert.assertFalse(b.addCoins(w, toAdd));
                Assert.assertFalse(b.addCoins(w, Set.of(tests[i], anotherCoin)));
            }
        }

        w.stopThread();

        Assert.assertFalse(w.didThrowException());
        Assert.assertEquals(Set.of(tests), w.getContents());
    }

    @Test
    public void testCoinAlreadyInWalletAsync() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        Collections.shuffle(shuffledIndexesList);

        testCoinAlreadyInWalletAsync(sequentialIndexes);
        testCoinAlreadyInWalletAsync(shuffledIndexesList);
    }

    private void testCoinAlreadyInWalletAsync(List<Integer> indexes) {
        int size = indexes.size();
        Blockchain b = Blockchain.createBlockchain();
        Wallet w = b.createWallet(size);

        w.startThread();

        Coin[] tests = new Coin[size];
        for (int i = 0 ; i < size ; i++) {
            tests[i] = b.createCoin(i);
        }

        List<Result<Boolean>> trueResults  = new LinkedList<>();
        List<Result<Boolean>> falseResults = new LinkedList<>();

        for (int i : indexes) {
            {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                trueResults.add(b.addCoinsAsync(w, toAdd));
                falseResults.add(b.addCoinsAsync(w, toAdd));
                falseResults.add(b.addCoinsAsync(w, Set.of(tests[i])));
            }

            if (i > 0) {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                toAdd.add(tests[i-1]);
                falseResults.add(b.addCoinsAsync(w, toAdd));
                falseResults.add(b.addCoinsAsync(w, Set.of(tests[i], tests[i-1])));
            }

            {
                Set<Coin> toAdd = new HashSet<>();
                toAdd.add(tests[i]);
                Coin anotherCoin = b.createCoin(size+i);
                toAdd.add(anotherCoin);
                falseResults.add(b.addCoinsAsync(w, toAdd));
                falseResults.add(b.addCoinsAsync(w, Set.of(tests[i], anotherCoin)));
            }

        }

        for (Result<Boolean> r : trueResults)
            Assert.assertTrue(r.getResult());

        for (Result<Boolean> r : falseResults)
            Assert.assertFalse(r.getResult());

        w.stopThread();

        Assert.assertFalse(w.didThrowException());
        Assert.assertEquals(Set.of(tests), w.getContents());
    }


}
