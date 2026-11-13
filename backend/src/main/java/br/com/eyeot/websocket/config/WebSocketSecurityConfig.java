package br.com.eyeot.websocket.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity 
public class WebSocketSecurityConfig {

     @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
            .nullDestMatcher().authenticated()

            // Notificações individuais para usuários autenticados.
            .simpSubscribeDestMatchers("/user/queue/telemetry/changed")
                .hasAnyRole("USER", "ADMIN")

            // Notificações gerais exclusivas dos administradores.
            .simpSubscribeDestMatchers("/topic/admin/**")
                .hasRole("ADMIN")

            // Clientes não podem publicar mensagens nesses destinos.
            .simpDestMatchers("/topic/**", "/queue/**")
                .denyAll()

            // Bloqueia outras inscrições e mensagens não autorizadas.
            .simpTypeMatchers(
                SimpMessageType.MESSAGE,
                SimpMessageType.SUBSCRIBE
            ).denyAll()

            .anyMessage().denyAll();

        return messages.build();
    }
}