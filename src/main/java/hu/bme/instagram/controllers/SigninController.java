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
        try {
            JsonFactory jsonFactory = new JacksonFactory();
            NetHttpTransport transport = new NetHttpTransport();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Arrays.asList(CLIENT_ID))
                    // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
                    // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
                    // "accounts.google.com". If you need to verify tokens from multiple sources, build
                    // a GoogleIdTokenVerifier for each issuer and try them both.
                    .setIssuer("accounts.google.com")
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // User infók eltárolása
                User user = new User();
                user.setToken(payload.getSubject());
                user.setName((String) payload.get("name"));
                user.setGooglePictureUrl((String) payload.get("picture"));

                request.getSession().setAttribute("user", user);
            } else {
                System.out.println("Invalid ID token.");
                return "signin";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "signin";
        }

        return "upload";
    }
}
