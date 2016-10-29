package hu.bme.instagram.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import hu.bme.instagram.entity.User;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@Scope("session")
public class SigninController {
    //    Google API clien id for google sign-in auth2
    private final static String CLIENT_ID = "941751993774-vcefv09ou5poadotds1e0clvsma43qjd.apps.googleusercontent.com";

    @GetMapping("/signin")
    public String signInGet() {
        return "signin";
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam(value = "idtoken", required = true) String idTokenString,
                         HttpServletRequest request) {
        System.out.println("Signin POST received");

        GoogleIdTokenVerifier verifier = getGoogleIdTokenVerifier();

        GoogleIdToken idToken = null;
        try {
            idToken = getGoogleIdToken(verifier, idTokenString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "signin";
        }

        User user = getUserWithUpdatedInfos(idToken);
        request.getSession().setAttribute("user", user);

        return "upload";
    }

    private User getUserWithUpdatedInfos(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();
        // User infók eltárolása
        User user = new User();
        user.setToken(payload.getSubject());
        user.setName((String) payload.get("name"));
        user.setGooglePictureUrl((String) payload.get("picture"));
        return user;
    }

    private GoogleIdTokenVerifier getGoogleIdTokenVerifier() {
        JsonFactory jsonFactory = new JacksonFactory();
        NetHttpTransport transport = new NetHttpTransport();

        return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(CLIENT_ID))
                .setIssuer("accounts.google.com")
                .build();
    }

    private GoogleIdToken getGoogleIdToken(GoogleIdTokenVerifier verifier, String idTokenString) throws Exception {
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            System.out.println("Exception while verifying id token");
            throw(e);
        }

        if (idToken == null) {
            throw(new Exception("Invalid id token(=null)"));
        }

        return idToken;
    }
}
