package edu.uic.cs454.s24.a4.solution;

import edu.uic.cs454.s24.a4.Action;
import edu.uic.cs454.s24.a4.Blockchain;
import edu.uic.cs454.s24.a4.Result;
import edu.uic.cs454.s24.a4.Wallet;

import java.util.HashSet;
import java.util.Set;

public class BlockchainSolution extends Blockchain<WalletSolution,CoinSolution> {

    private final HashSet<WalletSolution> wallets = new HashSet<>();

    @Override
    public WalletSolution createWallet(int capacity) {
        WalletSolution w  = new WalletSolution(capacity);
        wallets.add(w);
        return w;
    }

    @Override
    public CoinSolution createCoin(int id) {
        return new CoinSolution(id);
    }

    @Override
    public boolean addCoins(WalletSolution wallet, Set<CoinSolution> coins) {
        return addCoinsAsync(wallet,coins).getResult();
    }

    @Override
    public boolean transferCoins(WalletSolution from, WalletSolution to, Set<CoinSolution> coins) {
        ResultSolution<Boolean> removedFrom =  new ResultSolution<>();
        from.submitAction(new Action(Action.Direction.MOVE_OUT, coins, removedFrom));
        if(!removedFrom.getResult()){
            return false;
        }

        ResultSolution<Boolean> addTo =  new ResultSolution<>();
        to.submitAction(new Action(Action.Direction.MOVE_IN, coins, addTo));
        if(addTo.getResult()){
            return true;
        }
        while(true){
            ResultSolution<Boolean> addBack =  new ResultSolution<>();
            from.submitAction(new Action(Action.Direction.MOVE_IN, coins, addBack));

            if(addBack.getResult()){
                return false;
            }
        }
    }

    @Override
    public boolean payRent(WalletSolution wallet, Set<CoinSolution> coins) {
        return payRentAsync(wallet,coins).getResult();
    }

    @Override
    public boolean redeemCoins(WalletSolution wallet, Set<CoinSolution> coins) {
        return redeemCoinsAsync(wallet,coins).getResult();
    }

    @Override
    public Set<CoinSolution> getCoins() {
        HashSet<CoinSolution> ret = new HashSet<>();

        HashSet<ResultSolution<Set<CoinSolution>>> results = new HashSet<>();
        for (WalletSolution w :wallets){
            ResultSolution<Set<CoinSolution>> result = new ResultSolution<>();
            w.submitAction(new Action(Action.Direction.CONTENTS,null,result));
            results.add(result);
        }
        for (ResultSolution<Set<CoinSolution>> result : results){
            ret.addAll(result.getResult());
        }
        return ret;
    }

    @Override
    public Set<CoinSolution> getCoins(WalletSolution wallet) {
        return getCoinsAsync(wallet).getResult();
    }

    @Override
    public Result<Boolean> addCoinsAsync(WalletSolution wallet, Set<CoinSolution> coins) {
        ResultSolution<Boolean> result = new ResultSolution<>();
        wallet.submitAction(new Action(Action.Direction.ADD,coins,result));
        return result;
    }

    @Override
    public Result<Boolean> transferCoinsAsync(WalletSolution from, WalletSolution to, Set<CoinSolution> coins) {
        ResultSolution<Boolean> removedFrom =  new ResultSolution<>();
        from.submitAction(new Action(Action.Direction.MOVE_OUT, coins, removedFrom));

        ResultSolution<Boolean> addTo =  new ResultSolution<>();
        to.submitAction(new Action(Action.Direction.MOVE_IN, coins, addTo));

        Result<Boolean> combined = new Result<>(){
            @Override
            public void setResult(Boolean result){
                throw new Error("Not supported");
            }

            @Override
            public Boolean getResult(){
                if(removedFrom.getResult() && addTo.getResult()){
                    return true;
                } else if(!removedFrom.getResult() && !addTo.getResult()) {
                    return false;
                } else if (removedFrom.getResult() && !addTo.getResult()) {
                    while(true){
                        ResultSolution<Boolean> addBack =  new ResultSolution<>();
                        from.submitAction(new Action(Action.Direction.MOVE_IN, coins, addBack));

                        if(addBack.getResult()){
                            return false;
                        }
                    }
                } else if (!removedFrom.getResult() && addTo.getResult()) {
                    while(true){
                        ResultSolution<Boolean> removeTo =  new ResultSolution<>();
                        to.submitAction(new Action(Action.Direction.MOVE_OUT, coins, removeTo));

                        if(removeTo.getResult()){
                            return false;
                        }
                    }
                }
                throw new Error("Dead code");
            }
        };

        return combined;

    }

    @Override
    public Result<Boolean> payRentAsync(WalletSolution wallet, Set<CoinSolution> coins) {
        ResultSolution<Boolean> result = new ResultSolution<>();
        wallet.submitAction(new Action<>(Action.Direction.RENT, coins, result));
        return result;
    }

    @Override
    public Result<Boolean> redeemCoinsAsync(WalletSolution wallet, Set<CoinSolution> coins) {
        ResultSolution<Boolean> result = new ResultSolution<>();
        wallet.submitAction(new Action<>(Action.Direction.REDEEMED, coins, result));
        return result;
    }

    @Override
    public Result<Set<CoinSolution>> getCoinsAsync() {


        HashSet<ResultSolution<Set<CoinSolution>>> results = new HashSet<>();
        for (WalletSolution w :wallets){
            ResultSolution<Set<CoinSolution>> result = new ResultSolution<>();
            w.submitAction(new Action(Action.Direction.CONTENTS,null,result));
            results.add(result);
        }

       Result<Set<CoinSolution>> combined = new Result<>(){
            @Override
           public void setResult(Set<CoinSolution> result){
                throw new Error("Not supported");
            }

           @Override
           public Set<CoinSolution> getResult(){
                Set<CoinSolution> ret = new HashSet<>();
               for (ResultSolution<Set<CoinSolution>> result : results){
                   ret.addAll(result.getResult());
               }
                return ret;
           }
       };

        return combined;
    }

    @Override
    public Result<Set<CoinSolution>> getCoinsAsync(WalletSolution wallet) {
        ResultSolution<Set<CoinSolution>> result = new ResultSolution<>();
        wallet.submitAction(new Action(Action.Direction.CONTENTS,null,result));
        return result;
    }
}
