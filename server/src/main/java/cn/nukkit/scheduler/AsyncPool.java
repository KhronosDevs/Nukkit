package cn.nukkit.scheduler;

import cn.nukkit.Server;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncPool {

    @Getter
    private final Server server;
    private final ExecutorService executor;

    public AsyncPool(Server server) {
        this.server = server;
        this.executor = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name("Nukkit-VirtualTask-", 0)
                        .factory()
        );
    }

    public void execute(Runnable task) {
        executor.submit(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                server.getLogger().critical("Exception in asynchronous task", t);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
