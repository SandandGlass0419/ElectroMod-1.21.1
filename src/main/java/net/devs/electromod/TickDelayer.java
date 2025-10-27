package net.devs.electromod;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TickDelayer
{
    private static final Map<Integer, AtomicInteger> waitedMap = new HashMap<>();
    private static final Map<Integer, Runnable> taskMap = new HashMap<>();

    @Deprecated
    public static void counter(MinecraftServer server)
    {
        for (int id : waitedMap.keySet())
        {
            if (waitedMap.get(id).decrementAndGet() <= 0)
            {
                taskMap.get(id).run();

                waitedMap.remove(id);
                taskMap.remove(id);
            }
        }
    }

    @Deprecated
    public static void createDelayer(int ticks, Runnable task)
    {
        int id = waitedMap.size();

        waitedMap.put(id, new AtomicInteger(ticks));
        taskMap.put(id, task);
    }
}
