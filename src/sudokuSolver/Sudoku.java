package sudokuSolver;

import java.util.ArrayList;

public class Sudoku {

	private NumberField[][] numberFields = new NumberField[9][9];
	private final String originalString;
	
	public Sudoku (NumberField[][] numberFields, String originalString) {
		this.numberFields = numberFields;
		this.originalString = originalString;
	}
	
	public ArrayList<Integer> setPossibleValues(int r, int c) {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		for(int i = 1; i <= 9; i++)
			if(isUsable(i, r, c))
				possibleValues.add(i);
		numberFields[r][c].setPossibleValues(possibleValues);
		return possibleValues ;
	}
	
	public boolean isInBounds(int r, int c) {
		if(!(r >= 0 && r < numberFields.length && c >= 0 && c < numberFields[r].length))
			return false;
		return true;
	}

	private int[] traverseForward(int r, int c) {
		int [] originalPosition = {r,c};
		if(!isInBounds(r,c))
			return originalPosition;
		do {
			c++;
			if(c >= numberFields[r].length) {
				r++;
				c = 0;
			}
			if(!isInBounds(r, c)) {
				return originalPosition;}
		} while(numberFields[r][c].getId() == originalString);
		return new int[]{r, c};
	}

	private int[] traverseBackward(int r, int c) {
		int [] originalPosition = {r,c};
		if(!isInBounds(r,c))
			return originalPosition;
		do {
			c--;
			if(c <= 0) {
				r--;
				c = numberFields[r].length-1;
			}
			if(!isInBounds(r, c))
				return originalPosition;
		} while(numberFields[r][c].getId() == originalString);
		return new int[]{r, c};
	}

	public boolean hasNextCell(int r, int c) {
		if(getNextRValue(r, c) == r && getNextCValue(r, c) == c){return false;}
		return true;
	}
	
	public int getNextRValue(int r, int c) {
		return traverseForward(r,c)[0];
	}
	
	public int getNextCValue(int r, int c) {
		return traverseForward(r,c)[1];
	}
	
	public boolean hasPreviousCell(int r, int c) {
		if(getPreviousRValue(r, c) == r && getPreviousCValue(r, c) == c)
			return false;
		return true;
	}
	
	public int getPreviousRValue(int r, int c) {
		return traverseBackward(r,c)[0];
	}
	
	public int getPreviousCValue(int r, int c) {
		return traverseBackward(r,c)[1];
	}
	
	public boolean isUsable (int value, int r, int c) {
		if(isInRow(value, r))
			return false;
		if(isInColumn(value, c))
			return false;
		if(isInSection(value, r, c))
			return false;
		return true;
	}
	
	public boolean isInRow(int value, int r) {
		for(NumberField field : numberFields[r])
			if(field.getValue() == value)
				return true;
		return false;
	}
	
	public boolean isInColumn(int value, int c) {
		for(NumberField field[] : numberFields)
			if(field[c].getValue() == value)
				return true;
		return false;
	}
	
	public boolean isInSection(int value, int r, int c) {
		return isInSection(value, getSectionNumber(r, c));
	}
	
	public boolean isInSection(int value, int section) {
		NumberField[][] sectorFields = getSection(section);
		for(NumberField[] row : sectorFields)
			for(NumberField field : row)
				if(field.getValue() == value)
					return true;
		return false;
	}
	
	public NumberField[][] getSection(int r, int c) {
		return getSection(getSectionNumber(r, c));
	}
	
	public NumberField[][] getSection(int section) {
		NumberField[][] numberFieldsSection = new NumberField[3][3];
		int startRowPos = ((section-1)/3)*3;
		int startColPos = ((section-1)%3)*3;
		for(int r = startRowPos; r < startRowPos+3; r++)
			for(int c = startColPos; c < startColPos+3; c++)
				numberFieldsSection[r%3][c%3] = numberFields[r][c];
		return numberFieldsSection;
	}
	
	public int getSectionNumber(int r, int c) {
		return (((r/3)*3)+1) + c/3;
	}

	public void clearSudoku() {
		for(NumberField[] fieldArray: numberFields)
			for(NumberField field: fieldArray) {
				field.setValue(0);
				clearSudokuIds();
			}
	}

	public void clearSudokuIds() {
		for(NumberField[] fieldArray: numberFields)
			for(NumberField field: fieldArray) {
				field.setId("");
			}
	}

	public String toString() {
		String output = "";
		for(NumberField[] row : numberFields) {
			for(NumberField field : row)
				output += field.getText() + " ";
			output += "\n";
		}
		output += "\b";
		return output;
	}
}
