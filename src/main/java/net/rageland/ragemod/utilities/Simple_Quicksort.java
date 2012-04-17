package net.rageland.ragemod.utilities;

public class Simple_Quicksort {
	/**
	 * Ein einfach Quicksort für ein Array vom Typ int so geschreiben, dass
	 * Sonar nicht meckert :)
	 * 
	 * Wie du sprechen ins Deutsch? - Pandaz
	 * 
	 * @author Jan-Henrik Brommundt
	 * @version 1.0
	 */
	private int[] a;

	public Simple_Quicksort() {
		this.a = new int[0];
	}

	private void exchange(int i, int j) {
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	private void quicksort(int low, int high) {
		int i = low, j = high;
		int x = a[(low + high) >>> 1];
		while (i <= j) {
			while (a[i] < x) {
				i++;
			}
			while (a[j] > x) {
				j--;
			}
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		if (low < j) {
			quicksort(low, j);
		}
		if (i < high) {
			quicksort(i, high);
		}
	}

	/**
	 * 
	 * Ein einfach Quicksort
	 * 
	 * Das ist nicht!
	 * 
	 * @param a
	 *            das zu sortierende Array
	 * @return das sortierte Array
	 */
	public int[] sort(int[] k) {
		this.a = k.clone();
		quicksort(0, a.length - 1);
		return a.clone();
	}

}