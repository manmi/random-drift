package org.randomdrift;

//The basic encapsulation of a Random Vector

public class RandomVector {

	private final int dimension;
	private float[] randomArray;
	private boolean isDocVector = false;
	private String docPath = null;
	private String term = null;

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

	public boolean isDocVector() {
		return isDocVector;
	}

	public void setDocVector(boolean isDocVector) {
		this.isDocVector = isDocVector;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
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
}
