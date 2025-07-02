package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ClientsServlet.class);

    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";

    private final DBServiceClient dbServiceClient;
    private final TemplateProcessor templateProcessor;

    public ClientsServlet(DBServiceClient dbServiceClient, TemplateProcessor templateProcessor) {
        this.dbServiceClient = dbServiceClient;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Client> clients = dbServiceClient.findAll();

        List<Map<String, Object>> clientModels = clients.stream()
                .map(client -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", client.getId());
                    map.put("name", client.getName());
                    map.put("street", client.getAddress() != null ? client.getAddress().getStreet() : "");
                    map.put("phoneNumbers", client.getPhones().stream()
                            .map(Phone::getNumber)
                            .toList());
                    return map;
                })
                .toList();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("clients", clientModels);

        resp.setContentType("text/html;charset=utf-8");
        try {
            resp.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error(e.getMessage());
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
