package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	private Department departmentEntity;

	@FXML
	private TextField tfId;

	@FXML
	private TextField tfName;

	@FXML
	private Label lbErrorName;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	public void setDepartmentEntity(Department departmentEntity) {
		this.departmentEntity = departmentEntity;
	}

	@FXML
	public void onBtnSaveAction() {
		System.out.println("onBtnSaveAction");
	}

	@FXML
	public void onBtnCancelAction() {
		System.out.println("onBtnCancelAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(tfId);
		Constraints.setTextFieldMaxLength(tfName, 30);
	}

	public void updateFormData() {
		if (departmentEntity == null) {
			throw new IllegalStateException("Entity was null");
		}

		tfId.setText(String.valueOf(departmentEntity.getId()));
		tfName.setText(departmentEntity.getName());
	}

}
