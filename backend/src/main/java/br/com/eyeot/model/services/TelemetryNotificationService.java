package br.com.eyeot.model.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.websocket.TelemetryChangedEventDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelemetryNotificationService {

    private final DeviceRepository deviceRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    @Transactional(readOnly = true)
    public void notifyTelemetryChanged(TelemetryChangedEventDTO event) {

        // Avisa os administradores conectados.
        messagingTemplate.convertAndSend(
            "/topic/admin/telemetry/changed",
            event
        );

        // Busca o dispositivo junto com seu proprietário.
        deviceRepository.findByDeviceIdWithOwner(event.deviceId())
            .ifPresent(device -> {
                var owner = device.getOwner();

                if (owner == null) {
                    return;
                }

                messagingTemplate.convertAndSendToUser(
                    owner.getEmail1(),
                    "/queue/telemetry/changed",
                    event
                );

                this.diagnosticarUsuarios();
            });
    }

    public void diagnosticarUsuarios() {
        simpUserRegistry.getUsers().forEach(user -> {
            System.out.println(
                "Usuário STOMP registrado: " + user.getName()
            );
        });
    }
}
