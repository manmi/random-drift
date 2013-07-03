package org.randomdrift;

import java.util.Random;

public class RandomVectorFactory {

	final float density;
	final int dimension;
	Random random;

	public RandomVectorFactory(int dimension, float density) {
		this.dimension = dimension;
		this.density = density;
		this.random = new Random();
	}

	public RandomVector getZeroVector() {
		RandomVector randomVector = new RandomVector(dimension);
		float[] randomArray = new float[dimension];
		randomVector.setRandomArray(randomArray);
		return randomVector;
	}

	public RandomVector getOneVector() {
		RandomVector randomVector = new RandomVector(dimension);
		float[] randomArray = new float[dimension];
		int nonZeroEntries = (int) (density * dimension);
		if (nonZeroEntries == 0)
			nonZeroEntries = 1;

		for (int i = 0; i < nonZeroEntries; i++) {
			if(i%2 == 0)
				randomArray[i] = 1.0f;
			else
				randomArray[i] = -1.0f;
		}
		randomVector.setRandomArray(shuffleArray(randomArray));
		return randomVector;
	}

	public float getDensity() {
		return density;
	}

	public int getDimension() {
		return dimension;
	}

	public float[] shuffleArray(float[] array) {
		//Random random = new Random();
		for (int i = array.length - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			float a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
		return array;
	}
	
	public RandomVector getCopy(RandomVector copyOf){
		RandomVector copy = new RandomVector(dimension);
		float[] randomArrayCopy = new float[dimension];
		float[] randomArrayCopyOf = copyOf.getRandomArray();
		assert(this.dimension == randomArrayCopy.length);
		for(int i = 0; i < randomArrayCopyOf.length; i++){
			randomArrayCopy[i] = randomArrayCopyOf[i];
		}
		copy.setRandomArray(randomArrayCopy);
		return copy;
	}
}
