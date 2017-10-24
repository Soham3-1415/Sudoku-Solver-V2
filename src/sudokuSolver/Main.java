package sudokuSolver;

import javax.swing.GroupLayout.Alignment;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	private NumberField[][] numberFields = new NumberField[9][9];
	private Sudoku sudoku;
	
	private Label titleLbl = new Label("Sudoku Solver V2");
	private GridPane numberFieldGrid = new GridPane();
	private JFXProgressBar  progressBar = new JFXProgressBar();
	private JFXButton solveBtn = new JFXButton("Solve");
	
	@Override
	public void start(Stage primaryStage) {
		sudoku = new Sudoku(numberFields);
		
		for(int r = 0; r < 9; r++)
			for(int c = 0; c < 9; c++)
				numberFields[r][c] = new NumberField();
		
		VBox root = new VBox();
		Scene scene = new Scene(root,900,900);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Sudoku Solver");
		primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("sudokuSolver/Icon.png")));
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(400);
		primaryStage.show();
		numberFieldGrid = createNumberFieldGrid();
		
		root.getChildren().clear();
		root.getChildren().addAll(titleLbl, numberFieldGrid, progressBar, solveBtn);
		solveBtn.setOnAction(new EventHandler<ActionEvent>() {@Override public void handle(ActionEvent e) {solveBtnClick();}});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private GridPane createNumberFieldGrid() {
		GridPane numberFieldGrid = new GridPane();
		for(int r = 0; r < numberFields.length; r++)
			for(int c = 0; c < numberFields[r].length; c++) {
				NumberField numberField = new NumberField();
				
				numberField.textProperty().addListener(new ChangeListener<String>() {
				    @Override
				    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {numberFieldRegulation(observable, oldValue, newValue, numberField);}
				});
				
				numberFieldGrid.add(numberField, c, r);
				numberFields[r][c] = numberField;
			}
		return numberFieldGrid;
	}
	
	private void numberFieldRegulation(ObservableValue<? extends String> observable, String oldValue, String newValue, NumberField numberField) {
		if (newValue.matches("\\d+") && newValue.indexOf("0") == -1)
	    	Platform.runLater(() -> {
	    		numberField.setText(newValue.substring(newValue.length()-1));
	    		numberField.positionCaret(numberField.getText().length());
	    	});
    	else if (newValue.equals(""));
        else
        	Platform.runLater(() -> {
	    		numberField.setText(oldValue);
	    		numberField.positionCaret(numberField.getText().length());
	    	});
	}
	
	private void solveBtnClick() {
		//Might need to implement concurrency
		for(int r = 0; r < numberFields.length; r++)
			for(int c = 0; c < numberFields.length; c++) {
				NumberField field = numberFields[r][c];
				field.getPossibleValues().clear();
				if(!field.getText().equals(""))
					field.setId("original");
				else
					field.setId("");
			}
		
		for(int r = 0; r < numberFields.length;)
			for(int c = 0; c < numberFields[r].length;) {
				NumberField field = numberFields[r][c];
				if(!field.getId().equals("original")) {
					sudoku.setPossibleValues(r, c);
					if(field.getPossibleValues().size() == 0)
						if(c <= 0) {
							r--;
							c = numberFields[r].length-1; 			//replace with in bounds method check
						}
						else
							c--;
					else {
						field.setValue(field.getPossibleValues().get(0));
						if(c >= numberFields[r].length - 1) {			//replace with in bounds method check
							r++;
							c = 0;
						}
						else
							c++;
					}
			}
		}
	}
}
