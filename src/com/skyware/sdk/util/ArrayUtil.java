package com.skyware.sdk.util;

public class ArrayUtil {

	public static boolean contains(int[] arr, int key) {
		for (int i : arr) {
			if(i == key)
				return true;
		}
		return false;
	}

	public static int findIndex(int[] arr, int key) {
		for (int i = 0 ; i < arr.length ; i++) {
			if(arr[i] == key)
				return i;
		}
		return -1;
	}
}
