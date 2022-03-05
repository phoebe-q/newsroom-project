package models;

public class Image {

    String fullcaption; // caption information
    String imageURL; // url of image
    String mime; // mime (Multipurpose Internet Mail Extensions) type of the item (usually text/html)
    String imageHeight; // image height
    String imageWidth; // image width
    String type; // the type of the content (usually sanitized_html)
    String blurb; // image associated text

    public Image() {

    }

    public Image(String fullcaption, String imageURL, String imageHeight, String imageWidth, String blurb, String type, String mime){
        super();
        this.fullcaption = fullcaption;
        this.imageURL = imageURL;
        this.mime = mime;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.type = type;
        this.blurb = blurb;
    }
    public String getFullcaption() {
        return fullcaption;
    }
    public void setFullcaption(String fullcaption) {
        this.fullcaption = fullcaption;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageWidth() {
        return imageWidth;
    }
    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getBlurb() {
        return blurb;
    }
    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        this.mime = mime;
    }
}
