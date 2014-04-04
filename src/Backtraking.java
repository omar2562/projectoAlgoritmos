import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @author Andrey Omar Mozo Uscamayta
 * 
 */
public class Backtraking {

	private static long assignationCounter;
	private static long selectionCounter;
	private static long MAX_DEEP_TREE = 1*100*100*100;
	private static List<List<Integer>> rowValueList;
	private static List<List<Integer>> columnValueList;
	private static List<List<Integer>> blockValueList;
	private static boolean FORDWARD_CHECKING = false;
	private static boolean MIN_REMAINING_VALUES = false;
	private static Random r = new Random();
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int[][] board = null;
		int numTests = sc.nextInt();
		int boardSize = -1;
		int blankSpaces = 0;
		FORDWARD_CHECKING = true;
		MIN_REMAINING_VALUES = true;
		for (int testCounter = 0; testCounter < numTests; testCounter++) {
			while (boardSize < 1 || boardSize > 5) {
				boardSize = sc.nextInt();
			}
			rowValueList = new ArrayList<List<Integer>>(boardSize * boardSize);
			columnValueList = new ArrayList<List<Integer>>(boardSize
					* boardSize);
			blockValueList = new ArrayList<List<Integer>>(boardSize * boardSize);
			List<Integer> defaulValues = new ArrayList<>();
			for (int i = 0; i < boardSize * boardSize; i++) {
				defaulValues.add(i + 1);
			}
			for (int i = 0; i < boardSize * boardSize; i++) {

				rowValueList.add(new ArrayList<>(defaulValues));
				columnValueList.add(new ArrayList<>(defaulValues));
				blockValueList.add(new ArrayList<>(defaulValues));
			}
			board = new int[boardSize * boardSize][boardSize * boardSize];
			for (int rowCounter = 0; rowCounter < board.length; rowCounter++) {
				for (int columnCounter = 0; columnCounter < board.length; columnCounter++) {
					board[rowCounter][columnCounter] = sc.nextInt();
					if (board[rowCounter][columnCounter] == 0) {
						blankSpaces++;
					} else {
						rowValueList.get(rowCounter).remove(
								new Integer(board[rowCounter][columnCounter]));
						columnValueList.get(columnCounter).remove(
								new Integer(board[rowCounter][columnCounter]));
						int var = rowCounter * board.length + columnCounter;
						int blockNumber = (int) (boardSize * Math.floor(Math
								.floor(var / board.length) / boardSize));
						blockNumber += ((int) Math.floor(var % board.length) / boardSize);
						blockValueList.get(blockNumber).remove(
								new Integer(board[rowCounter][columnCounter]));
					}
				}
			}
			assignationCounter = 0;
			selectionCounter = 0;
			int[][] b = backtrakingSearch(board, blankSpaces);
			System.out.println(Arrays.deepToString(board).replaceAll("],",
					"],\r\n"));
			System.out.println("effort asig: " + assignationCounter + " ,sel: "
					+ selectionCounter);
			validateSudoku(board);
			boardSize = -1;
			blankSpaces = 0;
		}

		sc.close();
		System.exit(0);
	}

	private static int[][] backtrakingSearch(int[][] csp, int blankSpaces) {
		int[][] assigment = new int[blankSpaces][2];
		for (int i = 0; i < assigment.length; i++) {
			Arrays.fill(assigment[i], -1);
		}
		recursingBacktraking(0, assigment, csp);
		return csp;
	}

	private static boolean recursingBacktraking(int assigmentPosition,
			int[][] assigment, int[][] csp) {
		if(selectionCounter > MAX_DEEP_TREE) {
			System.err.println("Too many attempts, no solution");
			return false;
		}
		if (isCompleteAssigment(assigmentPosition, assigment))
			return true;
		int var = selectUnassignedVariable(assigmentPosition, assigment, csp);
		int row, column;
		List<Integer> psbVal = new ArrayList<Integer>(Arrays.asList(orderDomainValues(var, assigment, csp)));
		int value;		
		while (!psbVal.isEmpty()) {
			value = psbVal.get(r.nextInt(psbVal.size()));
			psbVal.remove(new Integer(value));
			selectionCounter++;
			if (isPosibleValue(value, var, csp)) {
				assigment[assigmentPosition][0] = var;
				assigment[assigmentPosition][1] = value;
				row = (int) Math.floor(var / csp.length);
				column = (int) Math.floor(var % csp.length);
				csp[row][column] = value;
				assignationCounter++;
				//System.out.println(assignationCounter);
				rowValueList.get(row).remove(
						new Integer(csp[row][column]));
				columnValueList.get(column).remove(
						new Integer(csp[row][column]));
				int boardSize = (int) Math.sqrt(csp.length);
				int blockNumber = (int) (boardSize * Math.floor(Math.floor(var
						/ csp.length)
						/ boardSize));
				blockNumber += ((int) Math.floor(var % csp.length) / boardSize);
				blockValueList.get(blockNumber).remove(
						new Integer(csp[row][column]));

				if (recursingBacktraking(++assigmentPosition, assigment, csp)) {
					return true;
				} else {
					rowValueList.get(row).add(new Integer(csp[row][column]));
					columnValueList.get(column).add(
							new Integer(csp[row][column]));
					blockValueList.get(blockNumber).add(
							new Integer(csp[row][column]));
					assigmentPosition--;
					csp[row][column] = 0;
					assigment[assigmentPosition][0] = -1;
					assigment[assigmentPosition][1] = -1;
				}
			}
		}
		return false;
	}

	private static boolean isPosibleValue(int value, int var, int[][] csp) {
		int row, column, subBlockSize, rowSubBlock, columnSubBlock;
		subBlockSize = (int) Math.sqrt(csp.length);
		row = (int) Math.floor(var / csp.length);
		column = (int) Math.floor(var % csp.length);
		ArrayList<Integer> rowValueList = new ArrayList<Integer>();
		rowValueList.add(value);
		ArrayList<Integer> columnValueList = new ArrayList<Integer>();
		columnValueList.add(value);
		ArrayList<Integer> blockValueList = new ArrayList<Integer>();
		blockValueList.add(value);
		for (int i = 0; i < csp.length; i++) {
			if (csp[i][column] != 0) {
				if (!columnValueList.contains(csp[i][column])) {
					columnValueList.add(csp[i][column]);
				} else {
					// System.err.println("ColumnCheck value:" + value);
					// System.err.println("row:" + i + " ,col:" + column);
					// System.err.println("ColumnCheck: KO");
					return false;
				}
			}
			if (csp[row][i] != 0) {
				if (!rowValueList.contains(csp[row][i])) {
					rowValueList.add(csp[row][i]);
				} else {
					// System.err.println("RowCheck value:" + value);
					// System.err.println("row:" + row + " ,col:" + i);
					// System.err.println("RowCheck: KO");
					return false;
				}
			}
		}
		int blockNumber = (int) (subBlockSize * Math.floor(Math.floor(var
				/ csp.length)
				/ subBlockSize));
		blockNumber += ((int) Math.floor(var % csp.length) / subBlockSize);
		rowSubBlock = (int) (subBlockSize * (Math.floor(blockNumber
				/ subBlockSize)));
		columnSubBlock = (int) (subBlockSize * Math.floor(blockNumber
				% subBlockSize));
		for (int rowBlock = rowSubBlock; rowBlock < rowSubBlock + subBlockSize; rowBlock++) {
			for (int colBlock = columnSubBlock; colBlock < columnSubBlock
					+ subBlockSize; colBlock++) {
				if (csp[rowBlock][colBlock] != 0) {
					if (!blockValueList.contains(csp[rowBlock][colBlock])) {
						blockValueList.add(csp[rowBlock][colBlock]);
					} else {
						// System.err.println("BlockCheck value:" + value);
						// System.err.println("block:" + blockNumber + " ,row:"
						// + rowBlock + " ,col:" + colBlock);
						// System.err.println("BlockCheck: KO");
						return false;
					}
				}
			}
		}
		return true;
	}

	private static Integer[] orderDomainValues(int var, int[][] assigment,
			int[][] csp) {
		Integer[] domainValues = null;
		if (!FORDWARD_CHECKING) {
			domainValues = new Integer[csp.length];
			for (int i = 0; i < domainValues.length; i++) {
				domainValues[i] = i + 1;
			}
		} else {
			Set<Integer> joinList = getRemainingValues(var, csp);
			domainValues = new Integer[joinList.size()];
			Iterator<Integer> it = joinList.iterator();
			domainValues = joinList.toArray(new Integer[0]);
		}
		return domainValues;
	}

	private static Set<Integer> getRemainingValues(int var, int[][] csp) {
		int subBlockSize = (int) Math.sqrt(csp.length);
		int row = (int) Math.floor(var / csp.length);
		int column = (int) Math.floor(var % csp.length);
		int blockNumber = (int) (subBlockSize * Math.floor(Math.floor(var
				/ csp.length)
				/ subBlockSize));
		blockNumber += ((int) Math.floor(var % csp.length) / subBlockSize);
		Set<Integer> joinList = new HashSet<Integer>(rowValueList.get(row));
		joinList.retainAll(columnValueList.get(column));
		joinList.retainAll(blockValueList.get(blockNumber));
		return joinList;
	}

	private static int selectUnassignedVariable(int assigmentPosition,
			int[][] assigment, int[][] csp) {
		int pos = -1;		
		int row, column;// , subBlockSize = (int) Math.sqrt(csp.length);
		if (MIN_REMAINING_VALUES) {
			int minRemainingValue = Integer.MAX_VALUE;
			int posMinRemainingValue = 0;
			for (pos = 0; pos < csp.length * csp.length; pos++) {
				row = (int) Math.floor(pos / csp.length);
				column = (int) Math.floor(pos % csp.length);
				if(csp[row][column] == 0){
					int tempMinVal = getRemainingValues(pos, csp).size();
					if(tempMinVal < minRemainingValue){
						minRemainingValue = tempMinVal;
						posMinRemainingValue = pos;
					}
				}
			}
			pos = posMinRemainingValue;
		} else {
			if (assigmentPosition - 1 >= 0)
				pos = assigment[assigmentPosition - 1][0];
			do {
				pos++;
				row = (int) Math.floor(pos / csp.length);
				column = (int) Math.floor(pos % csp.length);
			} while (csp[row][column] != 0 && pos < csp.length * csp.length);
		}
		return pos;
	}

	private static boolean isCompleteAssigment(int assigmentPosition,
			int[][] assigment) {
		// for (int i = 0; i < assigment.length; i++) {
		// for (int j = 0; j < assigment.length; j++) {
		// if (assigment[i][j] == -1)
		// return false;
		// }
		// }
		// return true;
		if (assigmentPosition >= assigment.length)
			return true;
		return false;
	}

	private static void validateSudoku(int[][] board) {
		ArrayList<Integer> valueList = new ArrayList<Integer>();
		for (int i = 0; i < board.length; i++) {
			for (int col = 0; col < board.length; col++) {
				if (!valueList.contains(board[i][col])) {
					valueList.add(board[i][col]);
				} else {
					System.out.println("RowCheck");
					System.out.println("row:" + i + " ,col:" + col);
					System.out.println("Sudoku: KO");
					return;
				}
			}
			valueList.clear();
			for (int row = 0; row < board.length; row++) {
				if (!valueList.contains(board[row][i])) {
					valueList.add(board[row][i]);
				} else {
					System.out.println("ColumnCheck");
					System.out.println("row:" + row + " ,col:" + i);
					System.out.println("Sudoku: KO");
					return;
				}
			}
			/*
			 * valueList.clear();
			 * 
			 * for (int subBoard = 0*i; subBoard < subBoardSize; subBoard++) {
			 * int vl = subBoard%subBoardSize; if
			 * (!valueList.contains(board[subBoard][i])) {
			 * valueList.add(board[subBoard][i]); } else {
			 * System.out.println("ColumnCheck"); System.out.println("row:" +
			 * subBoard + " ,col:" + i); System.out.println("Sudoku: KO");
			 * return; } }
			 */
			valueList.clear();
		}
		System.out.println("Sudoku: OK");
	}

	/*
	 * public static boolean solve(int[][] s) {
	 * 
	 * for (int i = 0; i < s.length; i++) { for (int j = 0; j < s.length; j++) {
	 * if (s[i][j] != 0) { continue; } for (int num = 1; num <= s.length; num++)
	 * { if (isValid(num, i, j, s)) { s[i][j] = num; if (solve(s)) { return
	 * true; } else { s[i][j] = 0; } } } return false; } } return true; }
	 */

}
