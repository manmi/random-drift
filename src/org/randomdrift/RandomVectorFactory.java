package org.randomdrift;

import java.util.Random;

public class RandomVectorFactory {

	final float density;
	final int dimension;
	Random random;
	HaarTransformer haarTransformer;

	public RandomVectorFactory(int dimension, float density) {
		this.dimension = dimension;
		this.density = density;
		this.haarTransformer = new HaarTransformer();
		//this.random = new Random();
		this.random = new Random(9);
	}
	
	public RandomVector getHaarForward(RandomVector inputRV, int sumLen){
		RandomVector rv = new RandomVector(inputRV.getDimension());
		float[] sdArray = haarTransformer.getForward(inputRV.getRandomArray(), sumLen);
		rv.setRandomArray(sdArray);
		return rv;
	}
	
	public RandomVector enhanceFeatureHaar(RandomVector inputRV, int sumLen){
		RandomVector inputCopy = getCopy(inputRV);
		float[] randomArray = inputCopy.getRandomArray();
		//set the s to zero
		for(int i = 0; i < sumLen; i++){
			randomArray[i] = 0;
		}
		inputCopy.setRandomArray(randomArray);
		return getHaarReverse(inputCopy, sumLen, 100.0f);
	}
	
	public RandomVector enhanceLatencyHaar(RandomVector inputRV, int sumLen){
		RandomVector inputCopy = getCopy(inputRV);
		float[] randomArray = inputCopy.getRandomArray();
		//set the d's to zero
		for(int i = sumLen - 1; i < randomArray.length; i++){
			randomArray[i] = 0.0f;
		}
		inputCopy.setRandomArray(randomArray);
		return getHaarReverse(inputCopy, sumLen, 100.0f);
	}
	
	public RandomVector getHaarReverse(RandomVector inputRV, int sumLen, float threshold){
		RandomVector rv = new RandomVector(inputRV.getDimension());
		float[] sdArray = haarTransformer.getReverse(inputRV.getRandomArray(), sumLen, threshold);
		rv.setRandomArray(sdArray);
		return rv;
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
