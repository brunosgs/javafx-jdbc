package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {
	@FXML
	private TableView<Department> tbDepartment;

	@FXML
	private TableColumn<Department, Integer> tcId;

	@FXML
	private TableColumn<Department, String> tcName;

	@FXML
	private Button btNew;

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

	/**
	 * Método para redimensionar a tabela conforme o tamanho da tela principal.
	 */
	private void resizeTable() {
		Stage stage = (Stage) Main.getMainScene().getWindow();

		tbDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
}
