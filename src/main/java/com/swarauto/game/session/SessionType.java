package com.swarauto.game.session;

import com.swarauto.game.analyzer.Analyzer;
import com.swarauto.game.analyzer.DungeonAnalyzer;
import com.swarauto.game.analyzer.RiftAnalyzer;
import com.swarauto.game.analyzer.ToaAnalyzer;
import com.swarauto.game.director.Director;
import com.swarauto.game.director.dungeon.CairosDungeon;
import com.swarauto.game.director.dungeon.RiftDungeon;
import com.swarauto.game.director.exp.FaimonHell1Stars;
import com.swarauto.game.director.exp.FaimonHell1StarsEXPBoost;
import com.swarauto.game.director.exp.FaimonHell2Stars;
import com.swarauto.game.director.exp.FaimonHell2StarsEXPBoost;
import com.swarauto.game.director.exp.FaimonHell3Stars;
import com.swarauto.game.director.exp.FaimonHell3StarsEXPBoost;
import com.swarauto.game.director.exp.FaimonHell4Stars;
import com.swarauto.game.director.exp.FaimonHell4StarsEXPBoost;
import com.swarauto.game.director.toa.AutoToa;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionType {
    CAIROS(DungeonAnalyzer.class, CairosDungeon.class),
    RIFT(RiftAnalyzer.class, RiftDungeon.class),
    FAIMON_4S_X2EXP(DungeonAnalyzer.class, FaimonHell4StarsEXPBoost.class),
    FAIMON_4S(DungeonAnalyzer.class, FaimonHell4Stars.class),
    FAIMON_3S_X2EXP(DungeonAnalyzer.class, FaimonHell3StarsEXPBoost.class),
    FAIMON_3S(DungeonAnalyzer.class, FaimonHell3Stars.class),
    FAIMON_2S_X2EXP(DungeonAnalyzer.class, FaimonHell2StarsEXPBoost.class),
    FAIMON_2S(DungeonAnalyzer.class, FaimonHell2Stars.class),
    FAIMON_1S_X2EXP(DungeonAnalyzer.class, FaimonHell1StarsEXPBoost.class),
    FAIMON_1S(DungeonAnalyzer.class, FaimonHell1Stars.class),
    TOA(ToaAnalyzer.class, AutoToa.class);

    private final Class<? extends Analyzer> analyzerClass;
    private final Class<? extends Director> directorClass;
}
