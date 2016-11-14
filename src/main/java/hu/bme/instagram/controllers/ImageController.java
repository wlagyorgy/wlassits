package hu.bme.instagram.controllers;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@Scope("session")
public class ImageController {

    private User user;
    private String uploadedPhotoName;

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
        return "redirect:";
    }

    @PostMapping("/upload")
    public String uploadingSubmit(@RequestParam(value = "image", required = true) MultipartFile uploadedPhoto,
                                  @RequestParam(value = "title", required = true) String title,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

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
                .transformation(new Transformation().width(100).height(150).crop("fill"))
                .imageTag(photo.getPublic_id()));
        addAttributesToModel(model, photo);

        return "redirect:main";

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

        String[] allowedImageFormats = new String[]{"jpg", "png", "bmp"};

        return ObjectUtils.asMap(
                "public_id", "temp/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "-" + title,
                "transformation", transformation,
                "allowed_formats", allowedImageFormats
        );
    }

    private Photo getPhotoInstance(Map uploadResult) {
        Photo photo = new Photo();
        photo.setPublic_id((String) uploadResult.get("public_id"));
        photo.setUser(user);
        photo.setCreated_at(new Date());
        photo.setTitle(uploadedPhotoName);
        return photo;
    }

    @GetMapping("/result")
    public String getResultPage(@ModelAttribute Photo photo) {
        return "result";
    }

    @GetMapping("/main")
    public String loadAllPictures(Model model) {
        List<Photo> photos = photoRepository.findAll();
        Collections.sort(photos,new PhotoComparator());
        model.addAttribute("images", getSearchedUserImagesWithUrls(photos,
                                            new Transformation().width(300).height(300).crop("fill")));
        return "main";
    }


    public List<PhotoWithUrl> getSearchedUserImagesWithUrls(Iterable<Photo> photos,Transformation transformation)
    {
        List<PhotoWithUrl> urls = new ArrayList<>();
        for (Photo p : photos) {
            urls.add(new PhotoWithUrl(p, transformation));
        }
        return urls;
    }

    @GetMapping("/myimages")
    public String currentUserImages(Model model, HttpServletRequest request)
    {
        String userName = ((User)request.getSession().getAttribute("user")).getName();
        List<Photo> photos = photoRepository.findByUserName(userName);
        Collections.sort(photos,new PhotoComparator());
        model.addAttribute("images", getSearchedUserImagesWithUrls(photos,
                                                        new Transformation().width(200).height(300).crop("fill")));
        return "myimages";
    }

    @GetMapping("/userimages")
    public String searchedUserImagesPost(Model model, HttpServletRequest request)
    {
        String searchedUser = request.getParameter("username");
        List<Photo> photos = photoRepository.findByUserNameContains(searchedUser);
        Collections.sort(photos,new PhotoComparator());
        model.addAttribute("images", getSearchedUserImagesWithUrls(photos,
                new Transformation().width(100).height(150).crop("fill")));
        return "userimages";
    }

    @GetMapping("/tagresultimages")
    public String searchedByTag(Model model, HttpServletRequest request)
    {
        String searchedTag = request.getParameter("imagetag");
        List<Photo> photos = photoRepository.findByTitleContains(searchedTag);
        Collections.sort(photos, new PhotoComparator());
        model.addAttribute("images", getSearchedUserImagesWithUrls(photos,
                new Transformation().width(150).height(200).crop("fill")));
        return "tagresultimages";
    }

    public class PhotoWithUrl {
        private Photo photo;
        private String url;

        public PhotoWithUrl(Photo photo, Transformation transformation) {
            this.photo = photo;
            this.url = cloudinary.url()
                    .transformation(transformation).imageTag(photo.getPublic_id());
        }

        public Photo getPhoto() {
            return photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
