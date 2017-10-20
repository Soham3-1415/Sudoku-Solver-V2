package sudokuSolver;

import java.util.ArrayList;

import com.jfoenix.controls.JFXTextField;

public class NumberField extends JFXTextField {
	private ArrayList<Integer> possibleValues;

	public NumberField() {
		super();
	}

	public NumberField(String text) {
		super(text);
	}
	
	public NumberField(ArrayList<Integer> possibleValues) {
		setPossibleValues(possibleValues);
		}
	
	public NumberField(String text, ArrayList<Integer> possibleValues) {
		this(text);
		setPossibleValues(possibleValues);
	}
	
	public void setPossibleValues(ArrayList<Integer> possibleValues) {
		this.possibleValues = possibleValues;
	}
	
	public ArrayList<Integer> getPossibleValues() {
		return possibleValues;
	}
}
