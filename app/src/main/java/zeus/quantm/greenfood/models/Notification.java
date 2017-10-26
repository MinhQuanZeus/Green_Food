package zeus.quantm.greenfood.models;

/**
 * Created by QuanT on 6/25/2017.
 */

public class Notification {
    private String title;
    private String image;
    private String description;

    public Notification(String title, String image, String description) {
        this.title = title;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
