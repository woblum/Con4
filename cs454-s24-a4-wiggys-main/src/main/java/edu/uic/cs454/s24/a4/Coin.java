package edu.uic.cs454.s24.a4;

public interface Coin {
    enum Status { MINED, IN_CIRCULATION , RENT , REDEEMED }

    Status getStatus();
}
