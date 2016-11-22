package hu.bme.instagram.Utilities;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public abstract class Constants {
    public static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "egyesrepo-cloudinary",
            "api_key", "525216556637445",
            "api_secret", "PtK7Je6XE8rEVCqRdWcDk_KzTFs"));
}
