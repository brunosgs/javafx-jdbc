package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private Button btNew;
	private ObservableList<Department> obsListDepartment;

	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
