package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	private DepartmentService departmentService;

	@FXML
	private TableView<Department> tbDepartment;

	@FXML
	private TableColumn<Department, Integer> tcId;

	@FXML
	private TableColumn<Department, String> tcName;

	@FXML
	private Button btnNew;
	private ObservableList<Department> obsListDepartment;

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

	private void initializeNodes() {
		tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		resizeTable();
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	/**
	 * Método para redimensionar a tabela conforme o tamanho da tela principal.
	 */
	private void resizeTable() {
		Stage stage = (Stage) Main.getMainScene().getWindow();

		tbDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Department> listDepartment = departmentService.findAll();
		obsListDepartment = FXCollections.observableArrayList(listDepartment);

		tbDepartment.setItems(obsListDepartment);
	}

	private void createDialogForm(Department department, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			// O getController() retorna o controlador associado a tela que foi carregada.
			DepartmentFormController departmentFormController = loader.getController();
			
			departmentFormController.setDepartmentEntity(department);
			departmentFormController.setDepartmentService(new DepartmentService());
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

}
