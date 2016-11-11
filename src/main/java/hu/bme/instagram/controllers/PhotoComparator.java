package hu.bme.instagram.controllers;


import hu.bme.instagram.entity.Photo;

import java.util.Comparator;

public class PhotoComparator implements Comparator<Photo> {

    @Override
    public int compare(Photo p1, Photo p2) {
        if(p1.getCreated_at().after(p2.getCreated_at()))
            return -1;
        else if(p1.getCreated_at().before(p2.getCreated_at()))
            return 1;
        else
            return 0;
    }

}
