package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private Seller sellerEntity;
	private SellerService sellerService;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField tfId;

	@FXML
	private TextField tfName;

	@FXML
	private TextField tfEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField tfBaseSalary;

	@FXML
	private ComboBox<Department> cbDepartment;
	private ObservableList<Department> obsListDepartment;

	@FXML
	private Label lbErrorName;

	@FXML
	private Label lbErrorEmail;

	@FXML
	private Label lbErrorBirthDate;

	@FXML
	private Label lbErrorBaseSalary;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	public void setSellerEntity(Seller sellerEntity) {
		this.sellerEntity = sellerEntity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if (sellerEntity == null) {
			throw new IllegalStateException("Entity was null");
		}

		if (sellerService == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			sellerEntity = getFormData();

			sellerService.saveOrUpdate(sellerEntity);

			notifyDataChangeListeners();

			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void updateFormData() {
		if (sellerEntity == null) {
			throw new IllegalStateException("Entity was null");
		}

		tfId.setText(String.valueOf(sellerEntity.getId()));
		tfName.setText(sellerEntity.getName());
		tfEmail.setText(sellerEntity.getEmail());

		if (sellerEntity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDateTime
					.ofInstant(sellerEntity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}

		Locale.setDefault(Locale.US);
		tfBaseSalary.setText(String.format("%.2f", sellerEntity.getBaseSalary()));

		if (sellerEntity.getDepartment() == null) {
			cbDepartment.getSelectionModel().selectFirst();
		} else {
			cbDepartment.setValue(sellerEntity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}

		List<Department> listDepartments = departmentService.findAll();
		obsListDepartment = FXCollections.observableArrayList(listDepartments);

		cbDepartment.setItems(obsListDepartment);
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(tfId);
		Constraints.setTextFieldMaxLength(tfName, 70);
		Constraints.setTextFieldMaxLength(tfEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(tfBaseSalary);

		initializeComboBoxDepartment();
	}

	private Seller getFormData() {
		Seller seller = new Seller();
		ValidationException exception = new ValidationException("Validation error");

		seller.setId(Utils.tryParseToInt(tfId.getText()));

		if (tfName.getText() == null || tfName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}

		seller.setName(tfName.getText());

		if (tfEmail.getText() == null || tfEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}

		seller.setEmail(tfEmail.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		} else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));

			seller.setBirthDate(Date.from(instant));
		}

		if (tfBaseSalary.getText() == null || tfBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}

		seller.setBaseSalary(Utils.tryParseToDouble(tfBaseSalary.getText()));
		seller.setDepartment(cbDepartment.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return seller;
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		lbErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		lbErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		lbErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
		lbErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {

			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};

		cbDepartment.setCellFactory(factory);
		cbDepartment.setButtonCell(factory.call(null));
	}
}
