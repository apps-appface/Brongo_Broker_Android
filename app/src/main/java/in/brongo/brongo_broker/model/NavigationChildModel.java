package in.brongo.brongo_broker.model;

/**
 * Created by mohit on 04-03-2017.
 */

public class NavigationChildModel {
    int image;
    String text;

    public NavigationChildModel(int image, String text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
