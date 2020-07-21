package com.example.leafpiction;

import java.util.ArrayList;

public class TrialData {

    private static String[] img_name = {
            "Image_10012020",
            "Image_12032020",
            "Image_27062020",
    };

    private static String[] img_date = {
            "10 Jan 2020 14:05",
            "12 Mar 2020 16:49",
            "27 Jun 2020 09:29"
    };

    private static int[] img_photo = {
            R.drawable.antoimg,
            R.drawable.karotenoidimg,
            R.drawable.klorofilimg,
    };

    static ArrayList<Photo> getListData() {
        ArrayList<Photo> list = new ArrayList<>();
        for (int position = 0; position < img_name.length; position++) {
            Photo photo = new Photo();
            photo.setName(img_name[position]);
            photo.setDate(img_date[position]);
            photo.setPhoto(img_photo[position]);
            list.add(photo);
        }
        return list;
    }

}
