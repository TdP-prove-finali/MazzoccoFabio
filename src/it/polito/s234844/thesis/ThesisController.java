package it.polito.s234844.thesis;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import it.polito.s234844.thesis.model.Order;
import it.polito.s234844.thesis.model.Part;
import it.polito.s234844.thesis.model.ThesisModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class ThesisController {
	
	final String error = "-fx-border-color: red; -fx-border-width: 2px;";
	final String noError ="-fx-border-color: none;";
	
	ThesisModel model = null;
	HashMap<String, Integer> orderMap;
	
	public ThesisController() {
		this.model = null;
		this.orderMap = new HashMap<String, Integer>();
	}
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Part> cbParts;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Button btnAddPart;

    @FXML
    private ListView<Order> partsList;

    @FXML
    private GridPane grid;
    
    @FXML
    private Pane topBar;
    
    @FXML
    private Button btnReset;
    
    @FXML
    private DatePicker datePicker;

    
    
    
    
    /* ======================================================================================================================================== */
	/* ======================================================================================================================================== */
	/* ================================================================= MAIN ================================================================= */
	/* ======================================================================================================================================== */
	/* ======================================================================================================================================== */
    
    @FXML
    void handleAddPart(ActionEvent event) {
    	//Set the comboBox and the textField back to normal (no errors detected)
    	this.cbParts.setStyle(this.noError);
    	this.txtQuantity.setStyle(this.noError);
    	
    	//Get the part from the input and checks it (if it has been chosen and if it is already present)
    	Part part = this.cbParts.getValue();
    	if(part == null) {
    		this.cbParts.setStyle(this.error);
    		return;
    	}else if(this.orderMap.containsKey(part.getPart_number())) {
    		JOptionPane.showMessageDialog(null, "The chosen part-number is already in the list", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	//Gets the quantity and checks if it is a valid value
    	Integer quantity = 0;
    	try {
    		quantity = Integer.parseInt(this.txtQuantity.getText());
    		if(quantity <= 0)
    			throw new Exception();
    	}catch(Exception e) {
    		this.txtQuantity.setStyle(this.error);
    		return;
    	}
    	
    	//Create the order and shows it in the listView (if the grid was disabled, sets it enabled)
    	Order newOrder = new Order("", part.getPart_number(), quantity, part.getDescription(), LocalDate.MIN, LocalDate.MIN);
    	this.orderMap.put(part.getPart_number(), quantity);
    	this.partsList.getItems().add(newOrder);
    	this.manageGrid();
    	
    	//Reset of the input fields
    	this.txtQuantity.clear();
    	this.cbParts.setValue(null);
    }
    

    @FXML
    void handleBestRate(ActionEvent event) {

    }

    @FXML
    void handleDueDateProbability(ActionEvent event) {

    }

    @FXML
    void handleDueDateQuoting(ActionEvent event) {

    }

    @FXML
    void handleSimulation(ActionEvent event) {

    }
    
    @FXML
    void deleteLine(KeyEvent event) {
    	if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
	    	List<Order> toBeRemoved = new ArrayList<Order>(this.partsList.getSelectionModel().getSelectedItems());
	    	this.partsList.getItems().removeAll(toBeRemoved);
	    	for(Order o : toBeRemoved)
	    		this.orderMap.keySet().remove(o.getPart_number());
	    	this.manageGrid();
    	}
    }
    

    
    
    
    
    /* ======================================================================================================================================== */
	/* ======================================================================================================================================== */
	/* ================================================================ OTHERS ================================================================ */
	/* ======================================================================================================================================== */
	/* ======================================================================================================================================== */
    
    @FXML
    void initialize() {
        assert cbParts != null : "fx:id=\"cbParts\" was not injected: check your FXML file 'Thesis.fxml'.";
        assert txtQuantity != null : "fx:id=\"txtQuantity\" was not injected: check your FXML file 'Thesis.fxml'.";
        assert btnAddPart != null : "fx:id=\"btnAddPart\" was not injected: check your FXML file 'Thesis.fxml'.";
        assert partsList != null : "fx:id=\"partsList\" was not injected: check your FXML file 'Thesis.fxml'.";
        assert grid != null : "fx:id=\"grid\" was not injected: check your FXML file 'Thesis.fxml'.";
        this.btnAddPart.setDefaultButton(true);
        this.btnReset.setAlignment(Pos.CENTER_RIGHT);
        this.datePicker.setShowWeekNumbers(true);
        this.datePicker.setDayCellFactory(getDayCellFactory());
    }

    public void setModel(ThesisModel model) {
    	this.grid.setDisable(true);
    	this.model = model;
    	this.model.loadData();
    	this.cbParts.getItems().addAll(this.model.getPartsList());
    }
    
    @FXML
    void resetList(ActionEvent event) {
    	this.orderMap.clear();
    	this.partsList.getItems().clear();
    	this.grid.setDisable(true);
    	this.datePicker.setValue(null);
    }
    
    private void manageGrid() {
    	if(this.orderMap.size()==0 || this.datePicker.getValue() == null || this.datePicker.getValue().isBefore(LocalDate.now()))
    		this.grid.setDisable(true);
    	else
    		this.grid.setDisable(false);
    }
    
    @FXML
    void checkDateValidity(ActionEvent event) {
    	if(this.datePicker.getValue()!=null && this.datePicker.getValue().isBefore(LocalDate.now()))
    		this.datePicker.setValue(LocalDate.now());
    	this.manageGrid();
    }
    
    // Factory to create Cell of DatePicker
    public Callback<DatePicker, DateCell> getDayCellFactory() {
 
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
 
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
 
                        // Disable Monday, Tueday, Wednesday.
                        if (item.isBefore(LocalDate.now())){
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        return dayCellFactory;
    }
}