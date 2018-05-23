package com.swarauto.game.director.exp;

public class FaimonHell4Stars extends FaimonHell4StarsEXPBoost {
    @Override
    public int getNeededRuns() {
        return super.getNeededRuns() * 2;
    }
}
