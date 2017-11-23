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
	private final String originalString = "original";
	
	private Label titleLbl = new Label("Sudoku Solver V2");
	private GridPane numberFieldGrid = new GridPane();
	private JFXProgressBar  progressBar = new JFXProgressBar();
	private JFXButton solveBtn = new JFXButton("Solve");
	
	@Override
	public void start(Stage primaryStage) {
		sudoku = new Sudoku(numberFields, originalString);
		
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
		//System.out.println(newValue +"\n"+ oldValue+"\n");
		if (newValue.matches("\\d{1}") && newValue.indexOf("0") == -1) {
			if(oldValue.matches("\\d{1}") && oldValue.indexOf("0") == -1)
				return;
	    	Platform.runLater(() -> {
	    		numberField.setText(newValue);
	    		numberField.positionCaret(numberField.getText().length());
	    	});
		}
		else if (newValue.matches("\\d{2}") && newValue.indexOf("0") == -1)
			Platform.runLater(() -> {
				String digit = "";
				if(newValue.indexOf(oldValue) == 0)
					digit = "" + newValue.charAt(1);
				else
					digit = "" + newValue.charAt(0);
				numberField.setText(digit);
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
			for(int c = 0; c < numberFields[r].length; c++) {
				NumberField field = numberFields[r][c];
				field.getPossibleValues().clear();
				if(!field.getText().equals("")) {
					field.setId(originalString);
					int temp = field.getValue();
					field.setText("");
					if(!sudoku.isUsable(temp, r, c)) {
						field.setText("" + temp);
						System.out.println("The sudoku is unsolvable.");
						return;
					}
					field.setText("" + temp);
				}
				else
					field.setId("");
			}
		
		for(int r = sudoku.getNextRValue(0, 0); r < numberFields.length;)
			for(int c = sudoku.getNextCValue(0, 0); c < numberFields[r].length;) {
				NumberField field = numberFields[r][c];
				if(!field.getId().equals(originalString)) { //Checks if cell is an inputed value
					if(!field.isLocked()) { //Only sets the possible values if the field is not locked
						sudoku.setPossibleValues(r, c);
						field.setLocked(true);
					}
					if(field.getPossibleValues().size() == 0) { //Checks if there are no more possible values
						if(sudoku.hasPreviousCell(r, c)) { //Checks if there is a previous cell
							field.setText("");
							field.setLocked(false);
							r = sudoku.getPreviousRValue(r, c);
							c = sudoku.getPreviousCValue(r, c);
							numberFields[r][c].getPossibleValues().remove(0); //Removes the invalid possible value
						}
						else { //If there is no previous cell
							System.out.println("Failed to find results.");
							return;
						}
					}
					else { //If there is 1 or more possible values
						field.setValue(field.getPossibleValues().get(0)); //Set the first value
						if(sudoku.hasNextCell(r, c)) {
							r = sudoku.getNextRValue(r, c);
							c = sudoku.getNextCValue(r, c);
						}
					}
			}
		}
	}
}
