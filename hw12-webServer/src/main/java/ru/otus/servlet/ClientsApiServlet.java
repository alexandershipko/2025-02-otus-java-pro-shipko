package ru.otus.servlet;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ClientsApiServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ClientsApiServlet.class);

    private final DBServiceClient dbServiceClient;
    private final Gson gson;

    public ClientsApiServlet(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            ServletOutputStream out = response.getOutputStream();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Client> clients = dbServiceClient.findAll();
                out.print(gson.toJson(clients));
            } else {
                long id = extractIdFromPath(pathInfo);
                if (id <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid client ID\"}");
                    return;
                }

                Optional<Client> clientOptional = dbServiceClient.getClient(id);
                if (clientOptional.isPresent()) {
                    out.print(gson.toJson(clientOptional.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Client not found\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (ServletOutputStream out = response.getOutputStream()) {
                out.print("{\"error\":\"Internal server error\"}");
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            }
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        try (ServletOutputStream out = response.getOutputStream()) {
            String json = request.getReader().lines().collect(Collectors.joining());
            Client client = gson.fromJson(json, Client.class);
            Client savedClient = dbServiceClient.saveClient(client);
            out.print(gson.toJson(savedClient));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (ServletOutputStream out = response.getOutputStream()) {
                out.print("{\"error\":\"Invalid client data\"}");
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            }
            logger.error(e.getMessage());
        }
    }

    private long extractIdFromPath(String pathInfo) {
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            return -1;
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static class ClientCycleExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getDeclaringClass() == ru.otus.crm.model.Address.class && f.getName().equals("client"))
                    || (f.getDeclaringClass() == ru.otus.crm.model.Phone.class && f.getName().equals("client"));
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

}
