package hu.bme.instagram.controllers;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import hu.bme.instagram.dal.PhotoRepository;
import hu.bme.instagram.entity.Like;
import hu.bme.instagram.entity.Photo;
import hu.bme.instagram.entity.User;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static hu.bme.instagram.Utilities.Constants.cloudinary;

@Controller
@Scope("session")
public class UploadController {

    private User user;
    private String uploadedPhotoName;

    @Autowired
    private PhotoRepository photoRepository;

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
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {

        user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:signin";
        }

        model.addAttribute("userName", user.getName());
        model.addAttribute("userImage", user.getGooglePictureUrl());

        if (uploadedPhoto.getSize() > 5000000) {
            System.out.println("A fájl mérete nagyobb a megengedett 5MB-nál.");
            return "upload";
        }
        uploadedPhotoName = title;
        Map options = getOptions(title);

        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(uploadedPhoto.getBytes(), options);
        } catch (IOException e) {
            System.out.println("Error at upload:\n" + e.getMessage());
            return "upload";
        }

        Photo photo = getPhotoInstance(uploadResult);
        System.out.println("Saving image to db. Image ID is: " + photo.getPublic_id());
        photoRepository.save(photo);

        redirectAttributes.addFlashAttribute("photo", cloudinary.url()
                .imageTag(photo.getPublic_id()));
        addAttributesToModel(model, photo);

        return "redirect:main";
    }

    private Map getOptions(@RequestParam(value = "title", required = true) String title) {
        Transformation transformation =
                new Transformation().width(1000).height(1000).crop("limit").fetchFormat("png");

        String[] allowedImageFormats = new String[]{"jpg", "png", "bmp"};

        return ObjectUtils.asMap(
                "public_id", "temp/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "-" + title,
                "transformation", transformation,
                "allowed_formats", allowedImageFormats
        );
    }

    private void addAttributesToModel(Model model, Photo photo) {
        model.addAttribute("details", photo.getPublic_id());

        model.addAttribute("photo", cloudinary.url()
                .transformation(new Transformation().width(100).height(150).crop("fill"))
                .imageTag(photo.getPublic_id())
        );
    }

    public Photo getPhotoInstance(Map uploadResult) {
        Photo photo = new Photo();
        photo.setPublic_id((String) uploadResult.get("public_id"));
        photo.setUser(user);
        photo.setCreated_at(new Date());
        photo.setTitle(uploadedPhotoName);
        photo.setLike(new Like());
        return photo;
    }
}
