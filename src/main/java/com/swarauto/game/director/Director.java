package com.swarauto.game.director;

import com.swarauto.game.GameStatus;
import com.swarauto.game.director.event.DirectorEvent;
import com.swarauto.game.profile.CommonConfig;
import com.swarauto.game.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public abstract class Director {
    protected static final Logger LOG = LoggerFactory.getLogger(Director.class);

    @Setter
    @Getter
    protected Profile profile;

    @Setter
    @Getter
    protected CommonConfig commonConfig;

    protected final EventBus eventBus;

    public Director() {
        this.eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
    }

    public static Director newInstance(Class<? extends Director> clazz) {
        Director instance = null;
        try {
            Constructor<? extends Director> constructor = clazz.getConstructor();
            instance = constructor.newInstance();
        } catch (Exception e) {
            if (clazz == null) {
                throw new IllegalArgumentException("Null Director class");
            } else {
                throw new IllegalArgumentException("Can't construct Director class: " + clazz.getSimpleName()
                        + " Exception: " + e.getMessage());
            }
        }
        return instance;
    }

    public void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public void act(GameStatus gameStatus) {
        try {
            eventBus.post(DirectorEvent.builder().id(DirectorEvent.ISSUING_DIRECTION).build());
            long startTime = System.currentTimeMillis();
            direct(gameStatus);
            LOG.info("direct: {} (cost {}ms)", gameStatus.getGameState().name(), System.currentTimeMillis() - startTime);
            eventBus.post(DirectorEvent.builder()
                    .id(DirectorEvent.DIRECTION_ISSUED)
                    .data(gameStatus)
                    .build());
        } catch (Exception e) {
            eventBus.post(DirectorEvent.builder()
                    .id(DirectorEvent.EXCEPTION_OCCURRED)
                    .data(e)
                    .build());
        }
    }

    /**
     * @param gameStatus {@link GameStatus}
     * @return if this state is handled
     */
    protected abstract boolean direct(GameStatus gameStatus);
}
