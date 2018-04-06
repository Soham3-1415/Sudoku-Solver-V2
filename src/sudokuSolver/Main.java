package sudokuSolver;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	private NumberField[][] numberFields = new NumberField[9][9];
	private Sudoku sudoku;
	private final String originalString = "original";
	private boolean solved = false;
	private boolean entryLocked = false;

	private Label titleLbl = new Label("Sudoku Solver V2");
	private GridPane numberFieldGrid = new GridPane();
	private HBox buttonsBox = new HBox(10);
	private JFXButton solveBtn = new JFXButton("Solve");
	private JFXButton clearBtn = new JFXButton("Clear");
	private Label infoLbl = new Label("");

	@Override
	public void start(Stage primaryStage) {
		sudoku = new Sudoku(numberFields, originalString);

		for (int r = 0; r < 9; r++)
			for (int c = 0; c < 9; c++)
				numberFields[r][c] = new NumberField();

		VBox root = new VBox(25);
		Scene scene = new Scene(root, 900, 900);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Sudoku Solver");
		primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("sudokuSolver/Icon.png")));
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(400);
		primaryStage.show();
		numberFieldGrid = createNumberFieldGrid();

		buttonsBox.getStyleClass().setAll("h-box", "box");
		buttonsBox.getChildren().clear();
		buttonsBox.getChildren().addAll(solveBtn, clearBtn);

		titleLbl.setId("title");
		infoLbl.setId("info");
		
		root.getChildren().clear();
		root.getChildren().addAll(titleLbl, numberFieldGrid, buttonsBox, infoLbl);
		
		solveBtn.setId("solve");
		solveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				solveBtnClick();
			}
		});
		clearBtn.setId("clear");
		clearBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clearBtnClick();
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void setEntryLocked(boolean entryLocked) {
		this.entryLocked = entryLocked;
		for(NumberField[] fieldRow:numberFields)
			for(NumberField field:fieldRow)
				field.setEditable(!entryLocked);
	}
	
	private GridPane createNumberFieldGrid() {
		GridPane numberFieldGrid = new GridPane();
		for (int r = 0; r < numberFields.length; r++)
			for (int c = 0; c < numberFields[r].length; c++) {
				NumberField numberField = new NumberField();

				numberField.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						if(!entryLocked)
							numberFieldRegulation(observable, oldValue, newValue, numberField);
					}
				});

				numberFieldGrid.add(numberField, c, r);
				numberFields[r][c] = numberField;
			}
		return numberFieldGrid;
	}

	private void numberFieldRegulation(ObservableValue<? extends String> observable, String oldValue, String newValue, NumberField numberField) {
		if (newValue.matches("\\d{1}") && newValue.indexOf("0") == -1) {
			if (oldValue.matches("\\d{1}") && oldValue.indexOf("0") == -1)
				return;
			Platform.runLater(() -> {
				numberField.setText(newValue);
				numberField.positionCaret(numberField.getText().length());
			});
		} else if (newValue.matches("\\d{2}") && newValue.indexOf("0") == -1)
			Platform.runLater(() -> {
				String digit = "";
				if (newValue.indexOf(oldValue) == 0)
					digit = "" + newValue.charAt(1);
				else
					digit = "" + newValue.charAt(0);
				numberField.setText(digit);
				numberField.positionCaret(numberField.getText().length());
			});
		else if (newValue.equals("")) ;
		else
			Platform.runLater(() -> {
				numberField.setText(oldValue);
				numberField.positionCaret(numberField.getText().length());
			});
	}

	private void solveBtnClick() {
		solved = false;
		for (int r = 0; r < numberFields.length; r++)
			for (int c = 0; c < numberFields[r].length; c++) {
				NumberField field = numberFields[r][c];
				field.getPossibleValues().clear();
				if (!field.getText().equals("")) {
					field.setId(originalString);
					int temp = field.getValue();
					field.setText("");
					if (!sudoku.isUsable(temp, r, c)) {
						field.setText("" + temp);
						sudoku.clearSudokuIds();
						System.out.println("The sudoku is unsolvable.");
						infoLbl.setText("Error: invalid sudoku");
						return;
					}
					field.setText("" + temp);
				} else
					field.setId("");
			}

		int r = 0;
		int c = 0;
		if (numberFields[0][0].getId().equals(originalString)) {
			r = sudoku.getNextRValue(0, 0);
			c = sudoku.getNextCValue(0, 0);
		}
		double start = System.currentTimeMillis() / 1000.0;
		setEntryLocked(true);
		evaluateCell(r, c);
		setEntryLocked(false);
		double end = System.currentTimeMillis() / 1000.0;
		double time = end - start;
		infoLbl.setText("Solving duration: " + String.format("%.5f", time) + " seconds");
		solved = false;
	}

	private void evaluateCell(int r, int c) {
		sudoku.setPossibleValues(r, c);
		if (numberFields[r][c].getPossibleValues().size() == 0)
			return;
		for (int value : numberFields[r][c].getPossibleValues()) {
			numberFields[r][c].setValue(value);
			if (sudoku.hasNextCell(r, c)) {
				evaluateCell(sudoku.getNextRValue(r, c), sudoku.getNextCValue(r, c));
				if (solved)
					return;
				numberFields[sudoku.getNextRValue(r, c)][sudoku.getNextCValue(r, c)].getPossibleValues().clear();
				numberFields[sudoku.getNextRValue(r, c)][sudoku.getNextCValue(r, c)].setValue(0);
			} else {
				solved = true;
				return;
			}
		}
	}

	private void clearBtnClick() {
		sudoku.clearSudoku();
		infoLbl.setText("");
	}
}