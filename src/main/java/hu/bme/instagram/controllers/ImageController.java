package hu.bme.instagram.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import hu.bme.instagram.dal.PhotoRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import hu.bme.instagram.entity.Photo;
import hu.bme.instagram.entity.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


@Controller
@Scope("session")
public class ImageController {

    private User user;

    @Autowired
    private PhotoRepository photoRepository;

    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "egyesrepo-cloudinary",
            "api_key", "525216556637445",
            "api_secret", "PtK7Je6XE8rEVCqRdWcDk_KzTFs"));


    @GetMapping("/upload")
    public String uploadingForm(Model model,
                                HttpServletRequest request) {
        user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImage", user.getGooglePictureUrl());
            return "upload";
        }
        return "redirect:signin";
    }

    @PostMapping("/upload")
    public String uploadingSubmit(@RequestParam(value = "image", required = true) MultipartFile uploadedPhoto,
                                  @RequestParam(value = "title", required = true) String title,
                                  Model model) {

        if (uploadedPhoto.getSize() > 5000000) {
            System.out.println("A fájl mérete nagyobb a megengedett 5MB-nál.");
            return "upload";
        }

        Map options = getOptions(title);

        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(uploadedPhoto.getBytes(), options);
        } catch (IOException e) {
            System.out.println("Error at upload:\n" + e.getMessage());
            return "upload";
        }

        Photo photo = getPhotoInstance(uploadResult);
        photoRepository.save(photo);

        addAttributesToModel(model, photo);

        return "result";

    }

    private void addAttributesToModel(Model model, Photo photo) {
        model.addAttribute("details", photo.getPublic_id());

        model.addAttribute("photo", cloudinary.url()
                .transformation(new Transformation().width(100).height(150).crop("fill"))
                .imageTag(photo.getPublic_id())
        );
    }

    private Map getOptions(@RequestParam(value = "title", required = true) String title) {
        Transformation transformation =
                new Transformation().width(1000).height(1000).crop("limit").fetchFormat("png");

        String[] allowedImageFormats = new String[]{"jpg", "png","bmp"};

        return ObjectUtils.asMap(
                    "public_id", "temp/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "-" + title,
                    "transformation", transformation,
                    "allowed_formats", allowedImageFormats
            );
    }

    private Photo getPhotoInstance(Map uploadResult) {
        Photo photo = new Photo();
        photo.setPublic_id((String) uploadResult.get("public_id"));
        photo.setUrl((String) uploadResult.get("url"));
        return photo;
    }

}
