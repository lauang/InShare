package no.bufferoverflow.inshare;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationLogger {
    /*Logger for Authentication */
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationLogger.class);

    @EventListener
    public void loginEvent(AuthenticationSuccessEvent event) {
        User user = ((User) event.getAuthentication().getPrincipal());
        LocalDateTime timestamp = LocalDateTime.now();

        // Log the successful login event
        logger.info("user: {}, timestamp: {}, message: logged in successfully", user.id, timestamp);
    }

    @EventListener
    public void logoutEvent(LogoutSuccessEvent event) {
        User user = ((User) event.getAuthentication().getPrincipal());
        LocalDateTime timestamp = LocalDateTime.now();

        // Log the logout event
        logger.info("user: {}, timestamp: {}, message: logged out", user.id, timestamp);
    }

    @EventListener
    public void failedLoginEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        LocalDateTime timestamp = LocalDateTime.now();

        // Log the failed login event
        logger.warn("attempted username: {}, timeStamp: {}, message: Failed login attempt", username, timestamp);
    }
}
