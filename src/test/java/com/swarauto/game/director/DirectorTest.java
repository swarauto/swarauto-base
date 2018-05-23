package com.swarauto.game.director;

import com.swarauto.game.GameState;
import com.swarauto.game.GameStatus;
import com.swarauto.game.director.event.DirectorEvent;
import org.greenrobot.eventbus.Subscribe;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DirectorTest {
    @Test
    public void testNewInstanceFailed() {
        try {
            Director instance = Director.newInstance(null);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Director instance = Director.newInstance(Director.class);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testDummyDirector() {
        DummySubscriber subscriber = mock(DummySubscriber.class);

        Director director = new Director() {
            @Override
            protected boolean direct(GameStatus gameStatus) {
                return false;
            }
        };
        director.registerEventListener(subscriber);

        GameStatus gameStatus = GameStatus.create(GameState.RUNE_REWARD, "src/test/resources/sampleRuneReward.png");
        director.act(gameStatus);

        ArgumentCaptor<DirectorEvent> argument = ArgumentCaptor.forClass(DirectorEvent.class);
        verify(subscriber, atLeastOnce()).onDirectorEvent(argument.capture());

        List<DirectorEvent> events = argument.getAllValues();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(DirectorEvent.ISSUING_DIRECTION, events.get(0).getId());
        Assert.assertEquals(DirectorEvent.DIRECTION_ISSUED, events.get(1).getId());

        director.unregisterEventListener(subscriber);
    }

    @Test
    public void testDummyException() {
        DummySubscriber subscriber = mock(DummySubscriber.class);

        Director director = new Director() {
            @Override
            protected boolean direct(GameStatus gameStatus) {
                throw new IllegalStateException("Sample exception when directing");
            }
        };
        director.registerEventListener(subscriber);

        GameStatus gameStatus = GameStatus.create(GameState.RUNE_REWARD, "src/test/resources/sampleRuneReward.png");
        director.act(gameStatus);

        ArgumentCaptor<DirectorEvent> argument = ArgumentCaptor.forClass(DirectorEvent.class);
        verify(subscriber, atLeastOnce()).onDirectorEvent(argument.capture());

        List<DirectorEvent> events = argument.getAllValues();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(DirectorEvent.ISSUING_DIRECTION, events.get(0).getId());
        Assert.assertEquals(DirectorEvent.EXCEPTION_OCCURRED, events.get(1).getId());

        director.unregisterEventListener(subscriber);
    }

    public class DummySubscriber {
        @Subscribe
        public void onDirectorEvent(DirectorEvent event) {

        }
    }
}
