package com.eukon05.classroom.Controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.eukon05.classroom.Domains.AppUser;
import com.eukon05.classroom.Services.UserService;
import com.eukon05.classroom.Utils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("api/v1/refresh")
public class RefreshController {

    //Code copied from my previous project, "Leopard"

    private final UserService userService;
    private final Gson gson;


    @Autowired
    public RefreshController(UserService userService, Gson gson){
        this.userService = userService;
        this.gson = gson;
    }

    @PostMapping
    String refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String auth = request.getHeader("Authorization");
        if(auth!=null && auth.startsWith("Bearer ")){

            try {
                DecodedJWT jwt = Utils.verifyAndReturnToken(auth);

                if(!jwt.getClaim("type").asString().equals("refresh"))
                    throw new Exception("Invalid token");

                String username = jwt.getSubject();

                AppUser user = userService.getUserByUsername(username);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", Utils.createAccessToken(user.getUsername(), request.getRequestURL().toString()));
                tokens.put("refresh_token", auth.replace("Bearer ", ""));

                response.setContentType(APPLICATION_JSON_VALUE);
                response.getWriter().write(gson.toJson(tokens));

            }
            catch(Exception ex){
                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return gson.toJson(error);
            }

        }
        else
            throw new Exception("Missing refresh token");

        return null;
    }
}

