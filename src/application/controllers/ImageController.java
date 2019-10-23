package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageController extends Controller {

	@FXML ImageView view;
	@FXML CheckBox box;

	private int imageNum;
	private boolean selected;

	@FXML
	public void handleSelect() {
		selected = !selected;
		box.setSelected(selected);

		if (selected) {
			view.setOpacity(1.0);
		} else {
			view.setOpacity(0.2);
		}
	}

	public void setImage(Image img,int i) {
		imageNum = i;
		view.setImage(img);
		view.setOpacity(0.2);
	}

	public void selectedImage() {
		box.setSelected(true);
		selected = true;
		view.setOpacity(1.0);
	}

	public boolean checkSelectedImage() {
		return selected;
	}

	public int imgNum() {
		return imageNum;
	}
}