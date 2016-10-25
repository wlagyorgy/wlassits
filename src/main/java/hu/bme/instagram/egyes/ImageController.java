package hu.bme.instagram.egyes;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import entity.Photo;
import entity.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


@Controller
public class ImageController {

    private Photo photo;
    private User user;
    //    Google API clien id for google sign-in auth2
    private final static String CLIENT_ID = "941751993774-vcefv09ou5poadotds1e0clvsma43qjd.apps.googleusercontent.com";

    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "egyesrepo-cloudinary",
            "api_key", "525216556637445",
            "api_secret", "PtK7Je6XE8rEVCqRdWcDk_KzTFs"));

    @RequestMapping("/")
    public String index() {
        if (user == null) {
            return "redirect:signin";
        } else {
            return "index";
        }
    }

    @GetMapping("/signin")
    public String signInGet() {
        return "signin";
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam(value = "idtoken", required = true) String idTokenString) {
        System.out.println("Signin POST received");
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
                Payload payload = idToken.getPayload();

                // User infók eltárolása
                user = new User();
                user.setToken(payload.getSubject());
                user.setName((String) payload.get("name"));
                user.setGooglePictureUrl((String) payload.get("picture"));


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


    @GetMapping("/upload")
    public String uploadingForm(Model model) {

        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImage", user.getGooglePictureUrl());
        } else {
            return "redirect:signin";
        }
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadingSubmit(@RequestParam(value = "image", required = true) MultipartFile uploadedPhoto,
                                  @RequestParam(value = "title", required = true) String title,
                                  Model model) {

        if (uploadedPhoto.getSize() <= 5000000) {

            try {
                Map uploadResult;

                Transformation transformation =
                        new Transformation().width(1000).height(1000).crop("limit").fetchFormat("png");

                String[] allowedImageFormats = new String[]{"jpg", "png","bmp"};

                Map options = ObjectUtils.asMap(
                        "public_id", "temp/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "-" + title,
                        "transformation", transformation,
                        "allowed_formats", allowedImageFormats
                );


                uploadResult = cloudinary.uploader().upload(uploadedPhoto.getBytes(), options);

                photo = new Photo();
                photo.setPublic_id((String) uploadResult.get("public_id"));
                photo.setUrl((String) uploadResult.get("url"));

                //ha sikeres a képfeltöltés, akkor tovább küldjök a result page-re
                model.addAttribute("details", photo.getPublic_id());

                model.addAttribute("photo", cloudinary.url()
                        .transformation(new Transformation().width(100).height(150).crop("fill"))
                        .imageTag(photo.getPublic_id())
                );

                return "result";


            } catch (IOException e) {
                System.out.println("Hiba a feltöltésnél:" + e.getMessage());
                return "upload";
            }

        } else {
            System.out.println("A fájl mérete nagyobb a megengedett 5MB-nál.");
            return "upload";
        }

    }

}
