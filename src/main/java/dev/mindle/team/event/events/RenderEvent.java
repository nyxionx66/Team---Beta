package dev.mindle.team.event.events;

import dev.mindle.team.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends Event {
    private final MatrixStack matrices;
    private final float tickDelta;

    public RenderEvent(MatrixStack matrices, float tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() { return matrices; }
    public float getTickDelta() { return tickDelta; }

    public static class World extends RenderEvent {
        public World(MatrixStack matrices, float tickDelta) {
            super(matrices, tickDelta);
        }
    }

    public static class Hud extends RenderEvent {
        public Hud(MatrixStack matrices, float tickDelta) {
            super(matrices, tickDelta);
        }
    }
}