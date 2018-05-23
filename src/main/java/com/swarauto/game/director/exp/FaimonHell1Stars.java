package com.swarauto.game.director.exp;

public class FaimonHell1Stars extends FaimonHell1StarsEXPBoost {
    @Override
    public int getNeededRuns() {
        return super.getNeededRuns() * 2;
    }
}
