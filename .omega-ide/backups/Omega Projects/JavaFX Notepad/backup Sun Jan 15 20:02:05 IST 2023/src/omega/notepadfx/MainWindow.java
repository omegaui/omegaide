package omega.notepadfx;
import javafx.stage.Stage;

import javafx.application.Application;
public class MainWindow extends Application{
	@Override
	public void start(Stage stage) {
		stage.setWidth(500);
		stage.setHeight(500);
		stage.centerOnScreen();
		MainViewController.init(stage);
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}
