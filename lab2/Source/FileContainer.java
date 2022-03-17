import javax.swing.*;
import java.util.ArrayList;

public class FileContainer {

    private ImageIcon icon;
    private ArrayList<String> readList;

    public ImageIcon getIcon() {
        return icon;
    }

    public ArrayList<String> getReadList() {
        return readList;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public void setReadList(ArrayList<String> readList) {
        this.readList = readList;
    }

}
