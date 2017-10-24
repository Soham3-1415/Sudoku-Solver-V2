package sudokuSolver;

import java.util.ArrayList;

import com.jfoenix.controls.JFXTextField;

public class NumberField extends JFXTextField {
	private ArrayList<Integer> possibleValues = new ArrayList<Integer>();

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
	
	public int getValue() {
		if(getText().equals(""))
			return 0;
		return Integer.parseInt(getText());
	}
	
	public void setValue(int value) {
		setText(Integer.toString(value));
	}
}
