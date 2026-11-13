package br.com.eyeot.websocket;


import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import br.com.eyeot.model.services.TelemetryNotificationService;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class TelemetryNotificationListener implements SmartLifecycle {

    private static final Logger log =
            LoggerFactory.getLogger(TelemetryNotificationListener.class);

    private static final String CHANNEL = "nova_telemetria";

    private final DataSource dataSource;
    private final TelemetryNotificationService notificationService;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    private volatile boolean running;
    private ExecutorService executor;

    public TelemetryNotificationListener(
            DataSource dataSource,
            TelemetryNotificationService notificationService,
            tools.jackson.databind.ObjectMapper objectMapper) {

        this.dataSource = dataSource;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::listenLoop);
    }

    private void listenLoop() {
        while (running) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {

                PGConnection pgConnection =
                        connection.unwrap(PGConnection.class);

                statement.execute("LISTEN " + CHANNEL);

                log.info("Escutando canal PostgreSQL: {}", CHANNEL);

                while (running && !connection.isClosed()) {
                    PGNotification[] notifications =
                            pgConnection.getNotifications(1000);

                    if (notifications == null) {
                        continue;
                    }

                    for (PGNotification notification : notifications) {
                        if (!running) {
                            break;
                        }

                        TelemetryChangedEventDTO event;

                        try {
                            event = objectMapper.readValue(
                                    notification.getParameter(),
                                    TelemetryChangedEventDTO.class
                            );
                        } catch (Exception e) {
                            log.error(
                                    "Erro ao converter notificação de telemetria",
                                    e
                            );
                            continue;
                        }


                        // Notificação WebSocket.
                        try {
                            notificationService.notifyTelemetryChanged(event);
                        } catch (Exception e) {
                            log.error(
                                    "Erro ao enviar notificação de telemetria",
                                    e
                            );
                        }
                    }
                }

            } catch (Exception e) {
                if (running) {
                    log.error(
                            "Erro na escuta de notificações do PostgreSQL",
                            e
                    );

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        running = false;

        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }
}
