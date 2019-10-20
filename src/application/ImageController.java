package application;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageController{

	@FXML ImageView view;
	@FXML CheckBox box;

	private int imageNum;
	private boolean isSelected;

	@FXML
	public void handleSelect() {
		isSelected = !isSelected;
		box.setSelected(isSelected);		
		if(isSelected) {
			view.setOpacity(1.0);
		}else {
			view.setOpacity(0.2);
		}

	}


	public void setImage(Image img,int i) {
		imageNum=i;
		view.setImage(img);
		view.setOpacity(0.2);
	}


	
	public void selectedImage() {
		box.setSelected(true);
		isSelected=true;
		view.setOpacity(1.0);
	}

    public boolean checkSelectedImage() {
    	return isSelected;
    }
    
    public int imgNum() {
    	return  imageNum;
    }
    
    






}


