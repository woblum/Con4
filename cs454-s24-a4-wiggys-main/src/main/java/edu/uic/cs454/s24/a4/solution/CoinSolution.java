package edu.uic.cs454.s24.a4.solution;

import edu.uic.cs454.s24.a4.Coin;

public class CoinSolution implements Coin {

    private final int id;
    /* default */ volatile Status status = Status.MINED;
    public CoinSolution(int id){
        this.id = id;
    }

    @Override
    public Status getStatus() {
        return status;
    }


}
