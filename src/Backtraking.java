import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 
 * @author Andrey Omar Mozo Uscamayta
 * 
 */
public class Backtraking {
	
	private static long assignationCounter;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int[][] board = null;
		int numTests = sc.nextInt();
		int boardSize = -1;
		int blankSpaces = 0;		
		for (int testCounter = 0; testCounter < numTests; testCounter++) {
			while (boardSize < 1 || boardSize > 5) {
				boardSize = sc.nextInt();
			}
			board = new int[boardSize * boardSize][boardSize * boardSize];
			for (int rowCounter = 0; rowCounter < board.length; rowCounter++) {
				for (int columnCounter = 0; columnCounter < board.length; columnCounter++) {
					board[rowCounter][columnCounter] = sc.nextInt();
					if (board[rowCounter][columnCounter] == 0)
						blankSpaces++;
				}
			}
			assignationCounter = 0;
			int[][] b = backtrakingSearch(board, blankSpaces);
			System.out.println(Arrays.deepToString(board).replaceAll("],","],\r\n"));
			System.out.println("effort: "+assignationCounter);
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
		if (isCompleteAssigment(assigmentPosition, assigment))
			return true;
		int var = selectUnassignedVariable(assigmentPosition, assigment, csp);
		int row, column;
		for (int value : orderDomainValues(var, assigment, csp)) {
			if (isPosibleValue(value, var, csp)) {
				assigment[assigmentPosition][0] = var;
				assigment[assigmentPosition][1] = value;
				row = (int) Math.floor(var / csp.length);
				column = (int) Math.floor(var % csp.length);
				csp[row][column] = value;
				assignationCounter++;
				if (recursingBacktraking(++assigmentPosition, assigment, csp)) {
					return true;
				} else {
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
//					System.err.println("ColumnCheck value:" + value);
//					System.err.println("row:" + i + " ,col:" + column);
//					System.err.println("ColumnCheck: KO");
					return false;
				}
			}
			if (csp[row][i] != 0) {
				if (!rowValueList.contains(csp[row][i])) {
					rowValueList.add(csp[row][i]);
				} else {
//					System.err.println("RowCheck value:" + value);
//					System.err.println("row:" + row + " ,col:" + i);
//					System.err.println("RowCheck: KO");
					return false;
				}
			}
		}
		int blockNumber = (int) (subBlockSize
				* Math.floor(Math.floor(var / csp.length) / subBlockSize));
		blockNumber+=((int) Math.floor(var % csp.length) / subBlockSize);
		rowSubBlock = (int) (subBlockSize*(Math.floor(blockNumber / subBlockSize)));
		columnSubBlock = (int) (subBlockSize*Math.floor(blockNumber % subBlockSize));
		for (int rowBlock = rowSubBlock; rowBlock < rowSubBlock+ subBlockSize; rowBlock++) {
			for (int colBlock = columnSubBlock; colBlock < columnSubBlock + subBlockSize; colBlock++) {
				if (csp[rowBlock][colBlock] != 0) {
					if (!blockValueList.contains(csp[rowBlock][colBlock])) {
						blockValueList.add(csp[rowBlock][colBlock]);
					} else {
//						System.err.println("BlockCheck value:" + value);
//						System.err.println("block:" + blockNumber + " ,row:"
//								+ rowBlock + " ,col:" + colBlock);
//						System.err.println("BlockCheck: KO");
						return false;
					}
				}
			}
		}
		return true;
	}

	private static int[] orderDomainValues(int var, int[][] assigment,
			int[][] csp) {
		int[] domainValues = new int[csp.length];
		for (int i = 0; i < domainValues.length; i++) {
			domainValues[i] = i + 1;
		}
		return domainValues;
	}

	private static int selectUnassignedVariable(int assigmentPosition,
			int[][] assigment, int[][] csp) {
		int pos = -1;
		if (assigmentPosition - 1 >= 0)
			pos = assigment[assigmentPosition - 1][0];
		int row, column;//, subBlockSize = (int) Math.sqrt(csp.length);
		do {
			pos++;
			row = (int) Math.floor(pos / csp.length);
			column = (int) Math.floor(pos % csp.length);
		} while (csp[row][column] != 0 && pos < csp.length*csp.length);
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
