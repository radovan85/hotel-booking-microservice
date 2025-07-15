package com.radovan.spring.brokers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.spring.converter.DeserializeConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.services.UserService;
import com.radovan.spring.utils.JwtUtil;
import com.radovan.spring.utils.NatsUtils;

import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.impl.Headers;

@Component
public class UserNatsListener {

    private DeserializeConverter deserializeConverter;
    private UserService userService;
    private ObjectMapper objectMapper;
    private NatsUtils natsUtils;
    private JwtUtil jwtUtil;

    @Autowired
    private void initialize(DeserializeConverter deserializeConverter, UserService userService,
                            ObjectMapper objectMapper, NatsUtils natsUtils, JwtUtil jwtUtil) {
        this.deserializeConverter = deserializeConverter;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.natsUtils = natsUtils;
        this.jwtUtil = jwtUtil;
        initListeners(); // automatski startuje listener
    }

    private void initListeners() {
        Dispatcher dispatcher = natsUtils.getConnection().createDispatcher(this::handleMessage);
        dispatcher.subscribe("user.create");
        dispatcher.subscribe("user.get");
        dispatcher.subscribe("user.delete.*");
        dispatcher.subscribe("user.get.*");

    }

    private void handleMessage(Message msg) {
        try {
            String subject = msg.getSubject();
            switch (subject) {
                case "user.get" -> handleUserGet(msg);         // ✔️ Current authenticated user (via token)
                case "user.create" -> handleUserCreate(msg);   // ✔️ User creation

                default -> {
                    if (subject.startsWith("user.delete.")) {
                        handleUserDelete(msg);                 // ✔️ Deletion via ID
                    } else if (subject.startsWith("user.get.")) {
                        handleUserGetById(msg);                // ✔️ Targeted user fetch (admin style)
                    }
                }
            }
        } catch (Exception e) {
            sendErrorResponse(getReplyTo(msg), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void handleUserGet(Message msg) throws Exception {
        JsonNode request = objectMapper.readTree(msg.getData());
        String token = request.get("token").asText();
        authenticateUser(token);

        UserDto currentUser = userService.getCurrentUser();
        if (currentUser.getEnabled() == 0) {
            sendErrorResponse(getReplyTo(msg), "Account suspended", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
            return;
        }

        natsUtils.getConnection().publish(getReplyTo(msg), objectMapper.writeValueAsBytes(currentUser));
    }

    private void handleUserDelete(Message msg) {
		processUserOperation(msg, userId -> userService.deleteUser(userId), "User ID %d successfully deleted");
	}


    private void handleUserCreate(Message msg) {
        String replyTo = getReplyTo(msg);
        try {
            UserDto userDto = deserializeConverter.payloadToUserDto(new String(msg.getData(), StandardCharsets.UTF_8));
            UserDto createdUser = userService.addUser(userDto);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("id", createdUser.getId());
            response.put("status", HttpStatus.OK.value());

            natsUtils.getConnection().publish(replyTo, objectMapper.writeValueAsBytes(response));
        } catch (ExistingInstanceException e) {
            sendErrorResponse(replyTo, "Email already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            sendErrorResponse(replyTo, "Error creating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private void handleUserGetById(Message msg) {
        try {
            Integer userId = Integer.parseInt(msg.getSubject().replace("user.get.", ""));
            UserDto user = userService.getUserById(userId);
            natsUtils.getConnection().publish(getReplyTo(msg), objectMapper.writeValueAsBytes(user));
        } catch (Exception e) {
            sendErrorResponse(getReplyTo(msg), "Error fetching user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void processUserOperation(Message msg, UserOperation operation, String successMessage) {
        try {
            Integer userId = extractUserId(msg);
            String replyTo = getReplyTo(msg);

            operation.execute(userId);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("status", HttpStatus.OK.value());
            response.put("message", String.format(successMessage, userId));

            natsUtils.getConnection().publish(replyTo, objectMapper.writeValueAsBytes(response));
        } catch (Exception e) {
            sendErrorResponse(getReplyTo(msg), "Error processing operation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Integer extractUserId(Message msg) {
        return Integer.parseInt(msg.getSubject().replaceAll("user.(delete|suspend|reactivate)\\.", ""));
    }

    private String getReplyTo(Message msg) {
        Headers headers = msg.getHeaders();
        String replyTo = msg.getReplyTo();
        return (replyTo == null || replyTo.isEmpty())
                ? (headers != null ? headers.getFirst("Nats-Reply-To") : "user.response")
                : replyTo;
    }

    private void authenticateUser(String token) {
        String userId = jwtUtil.extractUsername(token);
        List<GrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userId, token, authorities));
    }

    private void sendErrorResponse(String replyTo, String message, HttpStatus status) {
        if (replyTo == null || replyTo.isEmpty()) return;

        try {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("status", status.value());
            errorNode.put("message", message);
            natsUtils.getConnection().publish(replyTo, objectMapper.writeValueAsBytes(errorNode));
        } catch (Exception ignored) {
        }
    }

    @FunctionalInterface
    private interface UserOperation {
        void execute(Integer userId) throws Exception;
    }
}
