package dev.dpvb.survive.util.computation;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BooleanSupplier;

public class AsyncWait extends BukkitRunnable {
    final BooleanSupplier condition;
    final Runnable callback;

    public AsyncWait(BooleanSupplier condition, Runnable callback) {
        this.condition = condition;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (!isCancelled() && condition.getAsBoolean()) {
            callback.run();
            cancel();
        }
    }
}
