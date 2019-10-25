package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the image view. This view shows the image and the checkbox allowing the user to
 * select the image to be included in their creation.
 * 
 * @author Courtney Hunter and Yujia Wu
 */
public class ImageController extends Controller {

	@FXML ImageView view;
	@FXML CheckBox box;

	private int imageNum;
	private boolean selected;

	@FXML
	public void handleSelect() {
		// Change the selected status when checkbox is clicked
		selected = !selected;
		box.setSelected(selected);

		// Opacity low if is not selected, and full when is selected
		if (selected) {
			view.setOpacity(1.0);
		} else {
			view.setOpacity(0.2);
		}
	}

	/**
	 * Sets the image passed in to the pane and controller
	 * @param img The image to be added to this view
	 * @param i The image number ID
	 */
	public void setImage(Image img, int i) {
		imageNum = i;
		view.setImage(img);
		view.setOpacity(0.2);
	}

	/**
	 * Sets the image to status selected
	 */
	public void selectedImage() {
		box.setSelected(true);
		selected = true;
		view.setOpacity(1.0);
	}

	/**
	 * @return whether the image is selected
	 */
	public boolean checkSelectedImage() {
		return selected;
	}

	/**
	 * @return the number ID associated with the current image
	 */
	public int imgNum() {
		return imageNum;
	}
}