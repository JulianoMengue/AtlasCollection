package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class CollectionController extends AppController implements Initializable {

	@FXML
	TableView<String> tableView = new TableView<>();

	TableColumn<String, String> col1 = new TableColumn<>();

	List<String> list = new ArrayList<>();

	String transaction;

	@SuppressWarnings("unchecked")
	public void getCol() {
		MongoCollection<Document> collection = getConnection(collectionName);
		FindIterable<Document> query = collection.find();
		ArrayList<Document> result = new ArrayList<Document>();
		query.into(result);

		for (int i = 0; i < result.size(); i++) {
			col1.setText(collectionName.toUpperCase());
			list.add(result.get(i).toString().replace("{", "").replace("Document", "").replace("}", ""));
			ObservableList<String> details = FXCollections.observableArrayList(list);
			tableView.getColumns().addAll(col1);
			col1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
			tableView.setItems(details);
		}
	}

	public void collectionDelete() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<String, Void> colBtn = new TableColumn<String, Void>();
		Callback<TableColumn<String, Void>, TableCell<String, Void>> cellFactory = new Callback<TableColumn<String, Void>, TableCell<String, Void>>() {
			public TableCell<String, Void> call(final TableColumn<String, Void> param) {
				final TableCell<String, Void> cell = new TableCell<String, Void>() {

					private Button btn = new Button("DELETE");

					{
						btn.setMinWidth(200);
						btn.setOnAction((ActionEvent event) -> {
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Confirmation Dialog");
							alert.setContentText("Are you ok with this?");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK) {
								String id = getTableView().getItems().get(getIndex());
								String idClean = id.substring(4, 28);
								MongoCollection<Document> collection = getConnection(collectionName);
								collection.deleteOne(new Document("_id", new ObjectId(idClean)));
								tableView.getItems().remove(id);
							}

						});

					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {

							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setMinWidth(250);
		colBtn.setCellFactory(cellFactory);
		tableView.getColumns().add(colBtn);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			collectionDelete();
			getCol();
			col1.setStyle("-fx-font: 20 arial;");

		} catch (ClassNotFoundException | IOException | SQLException e) {
			e.printStackTrace();
		}

	}
}
