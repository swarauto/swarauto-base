package com.swarauto.game.director.exp;

public class FaimonHell2Stars extends FaimonHell2StarsEXPBoost {
    @Override
    public int getNeededRuns() {
        return super.getNeededRuns() * 2;
    }
}
