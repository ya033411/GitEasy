package app;





import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GitEasyApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		// create FXML loader
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/GitEasy_welcome.fxml"));
		
		// load fmxl, root layout manager in fxml file is GridPane
		AnchorPane root = (AnchorPane)loader.load();

		// set scene to root
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

}
