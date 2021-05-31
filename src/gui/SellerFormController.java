package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private Seller sellerEntity;
	private SellerService sellerService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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

	public void setSellerEntity(Seller sellerEntity) {
		this.sellerEntity = sellerEntity;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
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
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(tfId);
		Constraints.setTextFieldMaxLength(tfName, 30);
	}

	private Seller getFormData() {
		Seller deparment = new Seller();
		ValidationException exception = new ValidationException("Validation error");

		deparment.setId(Utils.tryParseToInt(tfId.getText()));

		if (tfName.getText() == null || tfName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}

		deparment.setName(tfName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return deparment;
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			lbErrorName.setText(errors.get("name"));
		}
	}
}
