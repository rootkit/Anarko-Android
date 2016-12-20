package com.oppous.anarko.holder;


import com.oppous.anarko.models.BaseItem;
import com.oppous.anarko.models.VideoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wayne
 */
public class ItemUtils {

    private static final String VIDEO_URL1 = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4";
    private static final String PIC_URL1 = "http://img10.3lian.com/sc6/show02/67/27/03.jpg";

    public static List<BaseItem> generateMockData() {
        List<BaseItem> list = new ArrayList<>();
        list.add(new VideoItem(VIDEO_URL1, PIC_URL1));

        return list;
    }

}
