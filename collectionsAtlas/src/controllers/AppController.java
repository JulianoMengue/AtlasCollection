package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import classes.AtlasCollectionClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AppController implements Initializable {

	@FXML
	TableView<AtlasCollectionClass> tableView = new TableView<AtlasCollectionClass>();

	public List<MongoCollection<Document>> collectionList;
	public static String collectionName;
	private ArrayList<Document> result = new ArrayList<Document>();
	private List<String> listName = new ArrayList<>();

	@SuppressWarnings({ "resource" })
	public void getCollection() {
		MongoClientURI uri = new MongoClientURI(
				"mongodb+srv://bananastaut:""@bananacluster.gzaux.mongodb.net/<>?retryWrites=true&w=majority");
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase dataBase = mongoClient.getDatabase("BananaDatabase");
		AtlasCollectionClass acc = null;
		MongoIterable<Document> colls = dataBase.listCollections();

		colls.into(result);
		List<AtlasCollectionClass> list = new ArrayList<>();

		for (int i = 0; i < result.size(); i++) {
			String collectionName = result.get(i).getString("name");
			listName.add(collectionName);
			acc = new AtlasCollectionClass(collectionName);
			list.add(acc);
			ObservableList<AtlasCollectionClass> observableListAtlas = FXCollections.observableArrayList(list);
			tableView.setItems(observableListAtlas);
		}

	}

	public boolean isCollEmpty(String collectionName) {
		boolean empty = false;
		MongoCollection<Document> collection = getConnection(collectionName);
		if (collection.count() == 0) {
			empty = true;
		} else
			empty = false;
		return empty;
	}

	@SuppressWarnings("resource")
	public static MongoCollection<Document> getConnection(String collectionName) {
		MongoClientURI uri = new MongoClientURI(
				"mongodb+srv://bananastaut:""@bananacluster.gzaux.mongodb.net/<>?retryWrites=true&w=majority");
		MongoClient mongoClient = new MongoClient(uri);
		MongoCollection<Document> collection = mongoClient.getDatabase("BananaDatabase").getCollection(collectionName);
		return collection;
	}

	public void collectionButton() throws IOException, ClassNotFoundException, SQLException {
		TableColumn<AtlasCollectionClass, Void> colBtn = new TableColumn<AtlasCollectionClass, Void>("Collections");
		Callback<TableColumn<AtlasCollectionClass, Void>, TableCell<AtlasCollectionClass, Void>> cellFactory = new Callback<TableColumn<AtlasCollectionClass, Void>, TableCell<AtlasCollectionClass, Void>>() {
			public TableCell<AtlasCollectionClass, Void> call(final TableColumn<AtlasCollectionClass, Void> param) {
				final TableCell<AtlasCollectionClass, Void> cell = new TableCell<AtlasCollectionClass, Void>() {

					private Button btn = new Button();
					{
						btn.setMinWidth(500);
						btn.setOnAction((ActionEvent event) -> {
							collectionName = getTableView().getItems().get(getIndex()).getName();
							
							try {
								collectionView();
							} catch (ClassNotFoundException | IOException | SQLException e) {
								e.printStackTrace();
							}
						});

					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							btn.setText(getTableView().getItems().get(getIndex()).getName().toUpperCase());
							if (isCollEmpty(getTableView().getItems().get(getIndex()).getName())) {
								btn.setStyle("-fx-background-color: #E06B5B ; ");
							} else {
								btn.setStyle("-fx-background-color: #1BD320 ; ");
							}
							setGraphic(btn);
							super.updateItem(item, empty);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setMinWidth(500);
		colBtn.setCellFactory(cellFactory);
		tableView.getColumns().add(colBtn);
	}

	public void collectionView() throws IOException, ClassNotFoundException, SQLException {
		Parent parent = FXMLLoader.load(getClass().getResource("/controllers/CollectionView.fxml"));
		Scene scene = new Scene(parent);
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(collectionName.toUpperCase());
		window.setScene(scene);
		window.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			collectionButton();
			getCollection();
		} catch (ClassNotFoundException | IOException | SQLException e) {
			e.printStackTrace();
		}

	}
}
