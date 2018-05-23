package com.swarauto.game.director.event;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public final class AnalyzerEvent {
    public static final int LOGGING = 1;
    public static final int EXCEPTION_OCCURRED = 2;
    public static final int START_DETECTING_GAME_STATE = 3;
    public static final int GAME_STATE_DETECTED = 4;

    private final int id;
    @Singular("data")
    private final List<Object> data;

    public Object getDataAt(int index) {
        if (data.size() == 0 || index > data.size() - 1) return null;

        return data.get(index);
    }
}
