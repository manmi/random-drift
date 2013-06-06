package org.randomdrift.test;

import org.randomdrift.RandomVector;
import org.randomdrift.RandomVectorFactory;

public class TestRandomVectorFactory {

	public static void main(String[] args) {
		RandomVectorFactory rvFactory = new RandomVectorFactory(64, 0.1f);
		RandomVector aZeroVector = rvFactory.getZeroVector();
		System.out.println(aZeroVector);

		for (int i = 0; i < 5; i++) {
			RandomVector randomVector = rvFactory.getOneVector();
			System.out.println(randomVector);
		}
	}

}
