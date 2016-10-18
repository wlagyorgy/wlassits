package hu.bme.instagram.egyes;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class ImageController {

    private Photo photo;

    private Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "egyesrepo-cloudinary",
            "api_key", "525216556637445",
            "api_secret", "PtK7Je6XE8rEVCqRdWcDk_KzTFs"));


    @GetMapping("/upload")
    public String uploadingForm(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadingSubmit(@RequestParam(value = "image", required = true) MultipartFile uploadedPhoto,
                                 @RequestParam(value = "title", required = true) String title,
                                 Model model) {

        if (uploadedPhoto != null && !uploadedPhoto.isEmpty()) {

            try {
                Map uploadResult;

                Transformation transformation =
                        new Transformation().width(1000).height(1000).crop("limit").fetchFormat("png");

                Map options = ObjectUtils.asMap(
                        "public_id", "temp/" + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + "-" + title,
                        "transformation",transformation
                );


                uploadResult = cloudinary.uploader().upload(uploadedPhoto.getBytes(), options);

                photo = new Photo();
                photo.setPublic_id((String) uploadResult.get("public_id"));
                photo.setUrl((String) uploadResult.get("url"));


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        model.addAttribute("details", photo.getPublic_id());

        model.addAttribute("photo", cloudinary.url()
                .transformation(new Transformation().width(100).height(150).crop("fill"))
                .imageTag(photo.getPublic_id())
        );

        return "result";
    }


}
