package gui;

import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	private DepartmentService departmentService;

	@FXML
	private TableView<Department> tbDepartment;

	@FXML
	private TableColumn<Department, Integer> tcId;

	@FXML
	private TableColumn<Department, String> tcName;

	@FXML
	private TableColumn<Department, Department> tcEdit;

	@FXML
	private TableColumn<Department, Department> tcRemove;

	@FXML
	private Button btnNew;
	private ObservableList<Department> obsListDepartment;

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department department = new Department();

		createDialogForm(department, "/gui/DepartmentFormView.fxml", parentStage);
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
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Department> listDepartment = departmentService.findAll();
		obsListDepartment = FXCollections.observableArrayList(listDepartment);

		tbDepartment.setItems(obsListDepartment);

		initEditButtons();
		initRemoveButtons();
	}

	/**
	 * M�todo para redimensionar a tabela conforme o tamanho da tela principal.
	 */
	private void resizeTable() {
		Stage stage = (Stage) Main.getMainScene().getWindow();

		tbDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	private void initializeNodes() {
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		resizeTable();
	}

	private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			// O getController() retorna o controlador associado a tela que foi carregada.
			DepartmentFormController departmentFormController = loader.getController();

			// Enjeta as depend�ncias
			departmentFormController.setDepartmentEntity(department);
			departmentFormController.setDepartmentService(new DepartmentService());
			departmentFormController.subscribeDataChangeListener(this);
			departmentFormController.updateFormData();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initEditButtons() {
		tcEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department department, boolean empty) {
				super.updateItem(department, empty);

				if (department == null) {
					setGraphic(null);

					return;
				}

				setGraphic(button);

				button.setOnAction(event -> createDialogForm(department, "/gui/DepartmentFormView.fxml",
						Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tcRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tcRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Department department, boolean empty) {
				super.updateItem(department, empty);

				if (department == null) {
					setGraphic(null);

					return;
				}

				setGraphic(button);

				button.setOnAction(event -> removeEntity(department));
			}
		});
	}

	private void removeEntity(Department department) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (departmentService == null) {
				throw new IllegalStateException("Service was null");
			}

			try {
				departmentService.remove(department);
				
				updateTableView();
			} catch (DbException e) {
				Alerts.showAlert("Error removing object", "null", e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
