package com.swarauto.game.director.exp;

public class FaimonHell3Stars extends FaimonHell3StarsEXPBoost {
    @Override
    public int getNeededRuns() {
        return super.getNeededRuns() * 2;
    }
}
