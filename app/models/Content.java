package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true) // this tells Jackson to ignore any properties found in the json that does not have an associated class variable
public class Content implements Serializable {
    private static final long serialVersionUID = -664641145229312192L;

    String content; // the content e.g. a line of text from the article
    String subtype; // subtype for the content
    String type; // type of the content
    String mime; // mime (Multipurpose Internet Mail Extensions) type

    // if image
    String fullcaption; // caption
    String imageURL; // url of image
    String imageHeight; // image height
    String imageWidth; // image width
    String blurb; // text related to the image

    // if creditation
    String role;
    String name;
    String bio;

    // if kicker (category)
    String kicker;

    public Content() {

    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getBio() {
        return bio;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getKicker() {
        return kicker;
    }


    public void setKicker(String kicker) {
        this.kicker = kicker;
    }


}
