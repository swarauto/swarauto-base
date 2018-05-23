package com.swarauto.game.director.event;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public final class DirectorEvent {
    public static final int LOGGING = 1;
    public static final int EXCEPTION_OCCURRED = 2;
    public static final int ISSUING_DIRECTION = 5;
    public static final int DIRECTION_ISSUED = 6;
    public static final int REFILLING_ENERGY = 7;
    public static final int NO_MORE_RUN = 8;

    private final int id;
    @Singular("data")
    private final List<Object> data;

    public Object getDataAt(int index) {
        if (data.size() == 0 || index > data.size() - 1) return null;

        return data.get(index);
    }
}
