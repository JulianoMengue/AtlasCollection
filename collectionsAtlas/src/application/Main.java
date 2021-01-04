package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException, ClassNotFoundException {
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/App.fxml"));
		Scene scene = new Scene(parent);
		Stage window = new Stage();
		window.setScene(scene);
		window.show();
	}

}
