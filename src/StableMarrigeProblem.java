import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

class StableMarrigeProblem {

	public static void main(String[] args) {
		char[] wifes = null, husbands = null;
		int[] pmWifes = null, pmHusbands = null;
		int[][] pWifes = null, pHusbands = null;
		int[][] inverse = null;
		int cWife = 0, cHusband = 0;
		Stack<Integer> freeHusband = new Stack<Integer>();
		Scanner sc = new Scanner(System.in);
		int numTest = sc.nextInt();
		numTest = numTest <= 27 && numTest >= 0 ? numTest : 0;
		for (int i = 0; i < numTest; i++) {
			cWife = cHusband = 0;
			int numCouple = sc.nextInt();
			husbands = new char[numCouple];
			wifes = new char[numCouple];
			pmHusbands = new int[numCouple];
			pmWifes = new int[numCouple];
			pHusbands = new int[numCouple][numCouple];
			pWifes = new int[numCouple][numCouple];
			inverse = new int[numCouple][numCouple];
			for (int j = 0; j < numCouple * 2; j++) {
				char name = sc.next().toCharArray()[0];
				if (name >= 'a' && name <= 'z') {
					freeHusband.add(cHusband);
					husbands[cHusband++] = name;
				} else {
					wifes[cWife++] = name;
				}
			}
			Arrays.sort(husbands);
			for (int j = 0; j < numCouple; j++) {
				String line = sc.next();
				char husband = line.toCharArray()[0];
				int hPosition = getPositionOf(husbands, husband);
				for (int j2 = 0; j2 < numCouple; j2++) {
					char wife = line.toCharArray()[2 + j2];
					pHusbands[hPosition][j2] = getPositionOf(wifes, wife);
				}
			}
			for (int j = 0; j < numCouple; j++) {
				String line = sc.next();
				char wife = line.toCharArray()[0];
				int hPosition = getPositionOf(wifes, wife);
				for (int j2 = 0; j2 < numCouple; j2++) {
					char husband = line.toCharArray()[2 + j2];
					pWifes[hPosition][j2] = getPositionOf(husbands, husband);
					inverse[hPosition][pWifes[hPosition][j2]] = j2;
				}
			}
			perfectMatchAlgorithm(pWifes, pHusbands, pmWifes, pmHusbands,
					inverse, freeHusband);
			for (int j = 0; j < pmHusbands.length; j++) {				
				System.out.print(husbands[j] + " " + wifes[pmHusbands[j]]);
				if(j < pmHusbands.length-1) System.out.print("\n");
			}
			if (i < numTest - 1)
				System.out.print("\n\n");
		}
		sc.close();
		System.exit(0);
	}

	private static void perfectMatchAlgorithm(int[][] pWifes,
			int[][] pHusbands, int[] pmWifes, int[] pmHusbands,
			int[][] inverse, Stack<Integer> freeHusband) {

		Arrays.fill(pmHusbands, -1);
		Arrays.fill(pmWifes, -1);

		while (!freeHusband.isEmpty()) {
			int hb = freeHusband.pop();
			int wf = getNextGirl(pHusbands, hb);
			if (pmWifes[wf] == -1) {
				pmWifes[wf] = hb;
				pmHusbands[hb] = wf;
			} else if (inverse[wf][hb] < inverse[wf][pmWifes[wf]]) {
				freeHusband.push(pmWifes[wf]);
				pmWifes[wf] = hb;
				pmHusbands[hb] = wf;
			} else {
				freeHusband.push(hb);
			}
		}

	}

	private static int getNextGirl(int[][] pHusbands, int hb) {
		for (int i = 0; i < pHusbands[hb].length; i++) {
			if (pHusbands[hb][i] != -1) {
				int tmp = pHusbands[hb][i];
				pHusbands[hb][i] = -1;
				return tmp;
			}
		}
		return 0;
	}

	private static int getPositionOf(char[] list, char obj) {
		for (int i = 0; i < list.length; i++)
			if (list[i] == obj)
				return i;
		return -1;
	}
}
