package com.qq456cvb.videoview.CustomWidgets;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class ImageGridWithText extends ImageGrid {
    private String text;
    public ImageGridWithText(String imageUrl, String text) {
        super(imageUrl);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

}
