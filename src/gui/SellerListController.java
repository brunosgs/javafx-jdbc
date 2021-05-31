package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	private SellerService sellerService;

	@FXML
	private TableView<Seller> tbSeller;

	@FXML
	private TableColumn<Seller, Integer> tcId;

	@FXML
	private TableColumn<Seller, String> tcName;

	@FXML
	private TableColumn<Seller, Seller> tcEdit;

	@FXML
	private TableColumn<Seller, Seller> tcRemove;

	@FXML
	private Button btnNew;
	private ObservableList<Seller> obsListSeller;

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller seller = new Seller();

		createDialogForm(seller, "/gui/SellerFormView.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	public void updateTableView() {
		if (sellerService == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Seller> listSeller = sellerService.findAll();
		obsListSeller = FXCollections.observableArrayList(listSeller);

		tbSeller.setItems(obsListSeller);

		initEditButtons();
		initRemoveButtons();
	}

	/**
	 * Método para redimensionar a tabela conforme o tamanho da tela principal.
	 */
	private void resizeTable() {
		Stage stage = (Stage) Main.getMainScene().getWindow();

		tbSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	private void initializeNodes() {
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		resizeTable();
	}

	private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			// O getController() retorna o controlador associado a tela que foi carregada.
//			SellerFormController sellerFormController = loader.getController();
//
//			// Enjeta as dependências
//			sellerFormController.setSellerEntity(seller);
//			sellerFormController.setSellerService(new SellerService());
//			sellerFormController.subscribeDataChangeListener(this);
//			sellerFormController.updateFormData();
//
//			Stage dialogStage = new Stage();
//
//			dialogStage.setTitle("Enter Seller data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	private void initEditButtons() {
		tcEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller seller, boolean empty) {
				super.updateItem(seller, empty);

				if (seller == null) {
					setGraphic(null);

					return;
				}

				setGraphic(button);

				button.setOnAction(
						event -> createDialogForm(seller, "/gui/SellerFormView.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tcRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller seller, boolean empty) {
				super.updateItem(seller, empty);

				if (seller == null) {
					setGraphic(null);

					return;
				}

				setGraphic(button);

				button.setOnAction(event -> removeEntity(seller));
			}
		});
	}

	private void removeEntity(Seller seller) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (sellerService == null) {
				throw new IllegalStateException("Service was null");
			}

			try {
				sellerService.remove(seller);

				updateTableView();
			} catch (DbException e) {
				Alerts.showAlert("Error removing object", "null", e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
