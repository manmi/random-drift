package org.randomdrift;

//The basic encapsulation of a Random Vector

public class RandomVector {

	private final int dimension;
	private float[] randomArray;
	

	public RandomVector(int dimension) {
		this.dimension = dimension;
	}

	public float[] getRandomArray() {
		return randomArray;
	}

	public void setRandomArray(float[] randomArray) {
		this.randomArray = randomArray;
	}

	public int getDimension() {
		return dimension;
	}

	public boolean hasOnlyOnes() {
		for (int i = 0; i < dimension; i++) {
			if (randomArray[i] != 1.0f || randomArray[i] != 0.0f)
				return false;
		}
		return true;
	}

	public boolean isZeroVector() {
		for (int i = 0; i < dimension; i++) {
			if (randomArray[i] != 0.0f)
				return false;
		}
		return true;
	}

	
	@Override
	public String toString() {

		StringBuffer elementBuffer = new StringBuffer();
		elementBuffer.append('{');
		for (float f : randomArray) {
			elementBuffer.append(f);
			elementBuffer.append('|');
		}
		elementBuffer.deleteCharAt(elementBuffer.length() - 1);
		elementBuffer.append('}');

		return ("Dimension:" + dimension + "|Elements:"
				+ elementBuffer.toString() + ".");

	}

	public RandomVector addOneVector(RandomVector randomVector) {
		for (int i = 0; i < dimension; i++) {
			randomArray[i] = randomArray[i] + randomVector.randomArray[i];
			if (randomArray[i] > 1.0f)
				randomArray[i] = 1.0f;

		}
		return this;
	}

	public float dotProduct(RandomVector randomVector) {

		float dotProduct = 0.0f;

		for (int i = 0; i < dimension; i++) {
			dotProduct += randomArray[i] * randomVector.randomArray[i];
		}
		return dotProduct;
	}
	
	public void scaleVector(float scaleFactor){	
		for(int i = 0; i < dimension; i++){
			randomArray[i] = randomArray[i] * scaleFactor;
		}
	}
	
	public void add(RandomVector randomVector){
		
		for(int i = 0; i < dimension; i++){
			randomArray[i] += randomVector.randomArray[i];
		}
	}
	
	public void normalize(){
		float sumOfComponents = 0.0f;
		for(int i = 0; i < dimension; i++){
			sumOfComponents += (randomArray[i]*randomArray[i]);
		}
		float sqrtSumOfComponents = (float) Math.sqrt(sumOfComponents);
		for(int i = 0; i < dimension; i++){
			randomArray[i] = randomArray[i]/sqrtSumOfComponents;
		}
	}
	
	//This should be in factory
	public RandomVector getHaar1D(){
		RandomVector haarVector = new RandomVector(this.dimension/2); //Assumed dimension is always even number 2^N
		float[] haarArray = new float[this.dimension/2];
		for(int i = 0, j = 0; i < dimension; i+= 2, j++){
			haarArray[j] = (randomArray[i] + randomArray[i+1])/2;
		}
		haarVector.setRandomArray(haarArray);
		//haarVector.normalize();
		return haarVector;
	}
	
}
