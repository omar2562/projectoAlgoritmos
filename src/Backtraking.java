import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
	/**
	 * Counter for each time that a value is selected for a variable(unknown
	 * value in SUDOKU)
	 */
	private static long assignationCounter;
	/**
	 * Counter for each time that a value is assigned to a variable(unknown
	 * value in SUDOKU)
	 */
	private static long selectionCounter;
	private static long MAX_DEEP_TREE = 1 * 100 * 100 * 100;
	/**
	 * Contains all possible values to assign in each row of the SUDOKU
	 */
	private static List<List<Integer>> rowValueList;
	/**
	 * Contains all possible values to assign in each column of the SUDOKU
	 */
	private static List<List<Integer>> columnValueList;
	/**
	 * Contains all possible values to assign in each sub-block of the SUDOKU
	 */
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
			initializeVariableForwardChecking(boardSize);
			board = new int[boardSize * boardSize][boardSize * boardSize];
			for (int rowCounter = 0; rowCounter < board.length; rowCounter++) {
				for (int columnCounter = 0; columnCounter < board.length; columnCounter++) {
					board[rowCounter][columnCounter] = sc.nextInt();
					if (board[rowCounter][columnCounter] == 0) {
						blankSpaces++;
					} else {
						/*
						 * remove from the variables for the Forward Checking
						 * heuristic the values that will be in the board at the
						 * beginning because they can not be used.
						 */
						rowValueList.get(rowCounter).remove(
								new Integer(board[rowCounter][columnCounter]));
						columnValueList.get(columnCounter).remove(
								new Integer(board[rowCounter][columnCounter]));
						int var = rowCounter * board.length + columnCounter;
						int blockNumber = getSubBlockNumber(boardSize, var);
						blockValueList.get(blockNumber).remove(
								new Integer(board[rowCounter][columnCounter]));
					}
				}
			}
			assignationCounter = 0;
			selectionCounter = 0;
			String line = "";
			for (int i = 0; i < Math.sqrt(board.length); i++) {
				line += "---";
			}
			line += "+";
			String fullLine = "";
			for (int i = 0; i < Math.sqrt(board.length); i++) {
				fullLine += line;
			}
			if (backtrakingSearch(board, blankSpaces)) {
				for (int i = 0; i < board.length; i++) {
					if (i % Math.sqrt(board.length) == 0 && i > 0)
						System.out.print("\n" + fullLine);
					System.out.println();
					for (int j = 0; j < board.length; j++) {
						if (j % Math.sqrt(board.length) == 0 && j > 0)
							System.out.print("|");
						System.out.print(board[i][j] < 10 ? " " + board[i][j]
								: board[i][j]);
						System.out.print(" ");
					}
				}
				System.out.println();
			} else {
				System.out.println("Too many attempts, no solution");
			}
			// validateSudoku(board);
			System.out.println("Effort: " + assignationCounter);
			boardSize = -1;
			blankSpaces = 0;
		}

		sc.close();
		System.exit(0);
	}

	/**
	 * 
	 * @param boardSize
	 *            size of the board in a SUDOKU n x n its n
	 * @param var
	 *            is the variable in the board
	 * @return the number of sub-block where the variable belongs.
	 */
	private static int getSubBlockNumber(int boardSize, int var) {
		int fullBoardSize = boardSize * boardSize;
		int blockNumber = (int) (boardSize * Math.floor(Math.floor(var
				/ fullBoardSize)
				/ boardSize));
		blockNumber += ((int) Math.floor(var % fullBoardSize) / boardSize);
		return blockNumber;
	}

	/**
	 * Initialize the 3 list and fill them with all possible values.
	 * 
	 * @param boardSize
	 *            size of the board, in a sudoku n x n, it will be n.
	 */
	private static void initializeVariableForwardChecking(int boardSize) {
		rowValueList = new ArrayList<List<Integer>>(boardSize * boardSize);
		columnValueList = new ArrayList<List<Integer>>(boardSize * boardSize);
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
	}

	/**
	 * Create a matrix that contains the variable with the values, initialize it
	 * with -1.
	 * 
	 * @param csp
	 *            board of the SUDOKU
	 * @param blankSpaces
	 *            number of unknown variables of the SUDOKU, or variable with 0
	 * @return TRUE if the SUDOKU was solved
	 */
	private static boolean backtrakingSearch(int[][] csp, int blankSpaces) {
		int[][] assigment = new int[blankSpaces][2];
		for (int i = 0; i < assigment.length; i++) {
			Arrays.fill(assigment[i], -1);
		}
		return recursingBacktraking(0, assigment, csp);
	}

	/**
	 * Solve the SUDOKU with the algorithm of Backtraking
	 * 
	 * @param assigmentPosition
	 *            pointer for the matrix assigment, where the possible variable
	 *            and value will be assigned.
	 * @param assigment
	 *            matrix with the solution for the SUDOKU
	 * @param csp
	 *            board of SUDOKU
	 * @return
	 */
	private static boolean recursingBacktraking(int assigmentPosition,
			int[][] assigment, int[][] csp) {
		int row, column, randomValue;
		if (selectionCounter > MAX_DEEP_TREE) {
			return false;
		}
		if (isCompleteAssigment(assigmentPosition, assigment))
			return true;
		int var = selectUnassignedVariable(assigmentPosition, assigment, csp);

		List<Integer> possibleValueList = new ArrayList<Integer>(
				Arrays.asList(orderDomainValues(var, assigment, csp)));
		while (!possibleValueList.isEmpty()) {
			/*
			 * remove the value of the list of possible values
			 */
			randomValue = possibleValueList.get(r.nextInt(possibleValueList
					.size()));
			possibleValueList.remove(new Integer(randomValue));

			selectionCounter++;
			if (isPosibleValue(randomValue, var, csp)) {
				assigment[assigmentPosition][0] = var;
				assigment[assigmentPosition][1] = randomValue;
				row = (int) Math.floor(var / csp.length);
				column = (int) Math.floor(var % csp.length);
				csp[row][column] = randomValue;
				assignationCounter++;
				/*
				 * Remove the value assigned from the lists of possible values
				 */
				rowValueList.get(row).remove(new Integer(csp[row][column]));
				columnValueList.get(column).remove(
						new Integer(csp[row][column]));
				int blockNumber = getSubBlockNumber(
						(int) Math.sqrt(csp.length), var);
				blockValueList.get(blockNumber).remove(
						new Integer(csp[row][column]));

				if (recursingBacktraking(++assigmentPosition, assigment, csp)) {
					return true;
				} else {
					/*
					 * Add the value assigned from the lists of possible values,
					 * because it does not solve the SUDOKU
					 */
					rowValueList.get(row).add(new Integer(csp[row][column]));
					columnValueList.get(column).add(
							new Integer(csp[row][column]));
					blockValueList.get(blockNumber).add(
							new Integer(csp[row][column]));
					/*
					 * Delete the variable assigned to the board an the matrix
					 * of assigments
					 */
					assigmentPosition--;
					csp[row][column] = 0;
					assigment[assigmentPosition][0] = -1;
					assigment[assigmentPosition][1] = -1;
				}
			}
		}
		return false;
	}

	/**
	 * Analyze the a value can fit on the SUDOKU variable
	 * 
	 * @param value
	 *            value to analyze
	 * @param var
	 *            variable free on the SUDOKU
	 * @param csp
	 *            board of the SUDOKU
	 * @return TRUE if the value pass all the restrictions
	 */
	private static boolean isPosibleValue(int value, int var, int[][] csp) {
		int row, column, subBlockSize, rowSubBlockStart, columnSubBlockStart;
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
					return false;
				}
			}
			if (csp[row][i] != 0) {
				if (!rowValueList.contains(csp[row][i])) {
					rowValueList.add(csp[row][i]);
				} else {
					return false;
				}
			}
		}
		int blockNumber = getSubBlockNumber(subBlockSize, var);
		rowSubBlockStart = (int) (subBlockSize * (Math.floor(blockNumber
				/ subBlockSize)));
		columnSubBlockStart = (int) (subBlockSize * Math.floor(blockNumber
				% subBlockSize));
		for (int rowBlock = rowSubBlockStart; rowBlock < rowSubBlockStart
				+ subBlockSize; rowBlock++) {
			for (int colBlock = columnSubBlockStart; colBlock < columnSubBlockStart
					+ subBlockSize; colBlock++) {
				if (csp[rowBlock][colBlock] != 0) {
					if (!blockValueList.contains(csp[rowBlock][colBlock])) {
						blockValueList.add(csp[rowBlock][colBlock]);
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Get all possible values that for a variable. There are implemented 2
	 * ways: <li>For the Forward Checking Heuristic with the method
	 * {@link getRemainingValues} . <li>Adding all values from 1 to N, where
	 * N=n*n in a board n x n
	 * 
	 * @param var
	 *            variable free on the SUDOKU
	 * @param csp
	 *            board of the SUDOKU
	 * @param assigment
	 *            list with the assignments done in the SUDOKU board.
	 * @return a list with all possible values
	 */
	private static Integer[] orderDomainValues(int var, int[][] assigment,
			int[][] csp) {
		Integer[] domainValues = null;
		if (FORDWARD_CHECKING) {
			Set<Integer> joinList = getRemainingValues(var, csp);
			domainValues = new Integer[joinList.size()];
			domainValues = joinList.toArray(new Integer[0]);
		} else {
			domainValues = new Integer[csp.length];
			for (int i = 0; i < domainValues.length; i++) {
				domainValues[i] = i + 1;
			}
		}
		return domainValues;
	}

	/**
	 * remove the values in joinList if the other unassigned variable in the
	 * row, column and sub-block where var belongs will get without possible
	 * values to assign.
	 * 
	 * @param var
	 *            variable free on the SUDOKU.
	 * @param csp
	 *            board of the SUDOKU.
	 * @param joinList
	 *            a set with the possible values for var.
	 */
	private static void leftOtherVariableWithoutPossibleValues(int var,
			int[][] csp, Set<Integer> joinList) {
		int subBlockSize = (int) Math.sqrt(csp.length);
		int row = (int) Math.floor(var / csp.length);
		int column = (int) Math.floor(var % csp.length);
		int blockNumber = getSubBlockNumber(subBlockSize, var);
		Integer variableAffected;
		List<Integer> variableToCheck = new ArrayList<Integer>();
		Set<Integer> possibleValues;
		for (int i = 0; i < csp.length; i++) {
			if (csp[i][column] == 0 && i != row) {
				variableAffected = i * csp.length + column;
				variableToCheck.add(variableAffected);
			}
			if (csp[row][i] == 0 && i != column) {
				variableAffected = row * csp.length + i;
				variableToCheck.add(variableAffected);
			}
		}
		int rowSubBlockStart = (int) (subBlockSize * (Math.floor(blockNumber
				/ subBlockSize)));
		int columnSubBlockStart = (int) (subBlockSize * Math.floor(blockNumber
				% subBlockSize));
		for (int rowBlock = rowSubBlockStart; rowBlock < rowSubBlockStart
				+ subBlockSize; rowBlock++) {
			for (int colBlock = columnSubBlockStart; colBlock < columnSubBlockStart
					+ subBlockSize; colBlock++) {
				if (csp[rowBlock][colBlock] == 0 && rowBlock != row
						&& colBlock != column) {
					variableAffected = rowBlock * csp.length + colBlock;
					if (!variableToCheck.contains(variableAffected)) {
						variableToCheck.add(variableAffected);
					}
				}
			}
		}
		for (Integer varAffected : variableToCheck) {
			possibleValues = new HashSet<>(getPossibleValues(varAffected, csp));
			if (possibleValues.size() == 1) {
				possibleValues.retainAll(joinList);
				if (possibleValues.size() == 1)
					joinList.removeAll(possibleValues);
			}
		}
	}

	/**
	 * Get in a set the intersection of all possible values in the lists:
	 * rowValueList,columnValueList,blockValueList
	 * 
	 * @param var
	 *            variable free on the SUDOKU.
	 * @param csp
	 *            board of the SUDOKU.
	 * @return
	 */
	private static Set<Integer> getPossibleValues(int var, int[][] csp) {
		int subBlockSize = (int) Math.sqrt(csp.length);
		int row = (int) Math.floor(var / csp.length);
		int column = (int) Math.floor(var % csp.length);
		int blockNumber = getSubBlockNumber(subBlockSize, var);
		Set<Integer> joinList = new HashSet<Integer>(rowValueList.get(row));
		joinList.retainAll(columnValueList.get(column));
		joinList.retainAll(blockValueList.get(blockNumber));
		return joinList;
	}

	/**
	 * get all possible values for the Forward Checking heuristic with the
	 * intersection of all possible values in the lists:
	 * rowValueList,columnValueList,blockValueList. And remove all values that
	 * will make other variable in their scope(row,column and sub-block) get
	 * without possible value.
	 * 
	 * @param var
	 *            variable free on the SUDOKU
	 * @param csp
	 *            board of the SUDOKU
	 * @return a list with all possible values for the Forward Checking
	 *         heuristic.
	 */
	private static Set<Integer> getRemainingValues(int var, int[][] csp) {
		Set<Integer> joinList = getPossibleValues(var, csp);
		leftOtherVariableWithoutPossibleValues(var, csp, joinList);
		return joinList;
	}

	/**
	 * They are two ways to select the next variable: <li>Minimum Remaing Values
	 * Heuristic, where the variable with less possible values is selected. <li>
	 * Catch the variable in order, starting with the (0,0) until (n,n)
	 * 
	 * @param assigmentPosition
	 *            pointer for the matrix assigment, where the possible variable
	 *            and value will be assigned.
	 * @param assigment
	 *            list with the assignments done in the SUDOKU board.
	 * @param csp
	 *            board of the SUDOKU
	 * @return the next free variable in the SUDOKU board.
	 */
	private static int selectUnassignedVariable(int assigmentPosition,
			int[][] assigment, int[][] csp) {
		int pos = -1;
		int row, column;
		if (MIN_REMAINING_VALUES) {
			int minRemainingValue = Integer.MAX_VALUE;
			List<Integer> posMinRemainingValue = new ArrayList<Integer>();
			for (pos = 0; pos < csp.length * csp.length; pos++) {
				row = (int) Math.floor(pos / csp.length);
				column = (int) Math.floor(pos % csp.length);
				if (csp[row][column] == 0) {
					int tempMinVal = getRemainingValues(pos, csp).size();
					if (tempMinVal < minRemainingValue) {
						minRemainingValue = tempMinVal;
						posMinRemainingValue.clear();
						posMinRemainingValue.add(pos);
					} else if (tempMinVal == minRemainingValue) {
						posMinRemainingValue.add(pos);
					}
				}
			}
			pos = posMinRemainingValue.get(r.nextInt(posMinRemainingValue
					.size()));
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

	/**
	 * 
	 * @param assigmentPosition
	 *            pointer for the matrix assigment, where the possible variable
	 *            and value will be assigned.
	 * @param assigment
	 *            list with the assignments done in the SUDOKU board.
	 * @return TRUE if the assigment matrix if full that means that the SUDOKU
	 *         is solved
	 */
	private static boolean isCompleteAssigment(int assigmentPosition,
			int[][] assigment) {
		if (assigmentPosition >= assigment.length)
			return true;
		return false;
	}

	/**
	 * Analyze if a SUDOKU board is solved right.
	 * 
	 * @param board
	 */
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
			valueList.clear();
		}
		System.out.println("Sudoku: OK");
	}
}
