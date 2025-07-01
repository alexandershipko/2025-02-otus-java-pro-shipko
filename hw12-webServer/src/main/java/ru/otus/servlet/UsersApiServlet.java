package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.SystemUser;
import ru.otus.crm.service.DBServiceSystemUser;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings({"java:S1989"})
public class UsersApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DBServiceSystemUser dbServiceSystemUser;
    private final transient Gson gson;

    public UsersApiServlet(DBServiceSystemUser dbServiceSystemUser, Gson gson) {
        this.dbServiceSystemUser = dbServiceSystemUser;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Optional<SystemUser> optionalSystemUser = dbServiceSystemUser.getSystemUser(extractIdFromRequest(request));

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        if (optionalSystemUser.isPresent()) {
            out.print(gson.toJson(optionalSystemUser.get()));
        }
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}
