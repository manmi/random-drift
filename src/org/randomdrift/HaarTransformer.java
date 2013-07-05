package org.randomdrift;

//This is a mine field, many places where you can get array index out of bounds.

public class HaarTransformer {
	
	public float[] getForward(float[] input, int sumLen){
		float[] output = new float[input.length];
		for(int i = 0; i < sumLen/2; i++){
			output[i] = (input[2*i] + input[2*i+1])/2;
			output[i + sumLen/2] = input[2*i] - output[i];
		}
		if(sumLen < input.length){
			for(int i = sumLen; i < input.length; i++){
				output[i] = input[i];
			}
		}
		return output;
	}
	
	public float[] getReverse(float[] input, int sumLen, float threshold){
		float[] output = new float[input.length];
		for(int i = 0; i < sumLen * 2; i+=2){
			if(Math.abs(input[sumLen+i/2])<threshold){
				output[i] = input[i/2] + input[sumLen + i/2];
				output[i+1] = input[i/2] - input[sumLen + i/2];
			}else{
				output[i] = input[i/2] + Math.copySign(threshold, input[sumLen + i/2]);
				output[i+1] = input[i/2] - Math.copySign(threshold, input[sumLen + i/2]);
			}
		}
		
		//sumLen has to be necessary than input length
		for(int i = sumLen * 2; i < input.length; i++){
			output[i] = input[i];
		}
		return output;
	}
}
