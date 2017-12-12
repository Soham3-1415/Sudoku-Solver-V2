package sudokuSolver;

import com.jfoenix.controls.JFXTextField;

import java.util.ArrayList;

public class NumberField extends JFXTextField {
	private ArrayList<Integer> possibleValues = new ArrayList<Integer>();

	public NumberField() {
		super();
	}

	public NumberField(String text) {
		super(text);
	}
	
	public void setPossibleValues(ArrayList<Integer> possibleValues) {
		this.possibleValues = possibleValues;
	}
	
	public ArrayList<Integer> getPossibleValues() {
		return possibleValues;
	}
	
	public int getValue() {
		try {
			return Integer.parseInt(getText());
		}
		catch(Exception e) {
			return 0;
		}
	}
	
	public void setValue(int value) {
		if(value >= 1 && value <= 9)
			setText(Integer.toString(value));
		else
			setText("");
	}
}
