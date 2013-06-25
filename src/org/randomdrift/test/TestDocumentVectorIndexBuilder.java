package org.randomdrift.test;

import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.index.CorruptIndexException;
import org.randomdrift.DocVectorIndexBuilder;
import org.randomdrift.LuceneIndexProfiler;
import org.randomdrift.TermVectorIndexBuilder;

public class TestDocumentVectorIndexBuilder {

	public static void main(String[] args) throws CorruptIndexException,
			IOException {

		String luceneIndexPath = "E:\\Corpora\\Toi2\\index";
		// Build the term vectors

		LuceneIndexProfiler indexProfiler = new LuceneIndexProfiler(
				luceneIndexPath);
		long startTime = System.currentTimeMillis();
		indexProfiler.profile();
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to profile lucene index: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		TermVectorIndexBuilder termIndexBuilder = new TermVectorIndexBuilder(
				luceneIndexPath, indexProfiler.getTotalNumberOfTerms(),
				indexProfiler.getNumTermsInDoc(),
				indexProfiler.getTermGlobalFreq(),
				indexProfiler.getDocIDPathMap());
		startTime = System.currentTimeMillis();
		termIndexBuilder.buildTermVectorsAll();
		termIndexBuilder.buildTermHaarVectors();
		endTime = System.currentTimeMillis();
		System.out.println("Time taken to build complete term vector: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		DocVectorIndexBuilder docIndexBuilder = new DocVectorIndexBuilder(
				luceneIndexPath, termIndexBuilder.getTermVectors(1),
				indexProfiler.getDocIDPathMap());
		docIndexBuilder.buildDoctVectorsAll();
		endTime = System.currentTimeMillis();
		System.out.println("Time taken to build complete doc vectors: "
				+ ((endTime - startTime) / 1000) + " seconds.");

		//docIndexBuilder.buildHaar1DIndex();

		String[] classes = { "classes\\business.txt", "classes\\crime.txt",
				"classes\\decor.txt", "classes\\entertainment.txt",
				"classes\\fashion.txt", "classes\\food.txt",
				"classes\\health.txt", "classes\\politics.txt",
				"classes\\religon.txt", "classes\\scitech.txt",
				"classes\\lifestyle.txt", "classes\\sports.txt",
				"classes\\travel.txt" };

		// String[] docToClassify = {
		// "classes\\business.txt",
		// "classes\\crime.txt",
		// "classes\\decor.txt",
		// "classes\\entertainment.txt",
		// "classes\\fashion.txt",
		// "classes\\food.txt",
		// "classes\\health.txt",
		// "classes\\politics.txt",
		// "classes\\religon.txt",
		// "classes\\scitech.txt",
		// "classes\\sexadvice.txt",
		// "classes\\sports.txt",
		// "classes\\travel.txt"
		// };

		String[] docToClassify = { "out\\19288983.txt", "out\\19289044.txt",
				"out\\19289051.txt", "out\\19289084.txt", "out\\19289100.txt",
				"out\\19289137.txt", "out\\19289166.txt", "out\\19289243.txt",
				"out\\19289308.txt", "out\\19289323.txt", "out\\19289332.txt",
				"out\\19289378.txt", "out\\19289380.txt", "out\\19289385.txt",
				"out\\19289391.txt", "out\\19289400.txt", "out\\19289431.txt",
				"out\\19289445.txt", "out\\19289502.txt", "out\\19289532.txt",
				"out\\19289593.txt", "out\\19289622.txt", "out\\19289624.txt",
				"out\\19289647.txt", "out\\19289721.txt", "out\\19289742.txt",
				"out\\19289744.txt", "out\\19289763.txt", "out\\19289781.txt",
				"out\\19289806.txt", "out\\19289833.txt", "out\\19290055.txt",
				"out\\19290090.txt", "out\\19290093.txt", "out\\19290104.txt",
				"out\\19290117.txt", "out\\19290118.txt", "out\\19290157.txt",
				"out\\19290163.txt", "out\\19290176.txt", "out\\19290200.txt",
				"out\\19290202.txt", "out\\19313200.txt", "out\\19313227.txt",
				"out\\19313245.txt", "out\\19313260.txt", "out\\19313267.txt",
				"out\\19313272.txt", "out\\19313300.txt", "out\\19313320.txt",
				"out\\19313321.txt", "out\\19313334.txt", "out\\19313336.txt",
				"out\\19313349.txt", "out\\19313360.txt", "out\\19313370.txt",
				"out\\19313379.txt", "out\\19313381.txt", "out\\19313397.txt",
				"out\\19313398.txt", "out\\19313408.txt", "out\\19313411.txt",
				"out\\19313414.txt", "out\\19313423.txt", "out\\19313426.txt",
				"out\\19313429.txt", "out\\19313438.txt", "out\\19313439.txt",
				"out\\19313454.txt", "out\\19313456.txt", "out\\19313461.txt",
				"out\\19313469.txt", "out\\19313484.txt", "out\\19313492.txt",
				"out\\19313494.txt", "out\\19313497.txt", "out\\19313499.txt",
				"out\\19313502.txt", "out\\19313507.txt", "out\\19313508.txt",
				"out\\19313511.txt", "out\\19313512.txt", "out\\19313515.txt",
				"out\\19313517.txt", "out\\19313519.txt", "out\\19313523.txt",
				"out\\19313526.txt", "out\\19313529.txt", "out\\19313536.txt",
				"out\\19313545.txt", "out\\19313548.txt", "out\\19313552.txt",
				"out\\19313557.txt", "out\\19313559.txt", "out\\19313569.txt",
				"out\\19313576.txt", "out\\19313591.txt", "out\\19313597.txt",
				"out\\19313601.txt", "out\\19313646.txt" };

		int numResults = 5;

		// for(int i = 0; i < docToClassify.length; i++){
		// StringBuffer resultString = new StringBuffer();
		// String[] resultClass = new String[numResults];
		// float[] score = new float[numResults];
		// String docToClassifyPath = docToClassify[i];
		// for(int j = 0; j < classes.length; j++){
		// float simScore = docIndexBuilder.compareDocuments(docToClassifyPath,
		// classes[j]);
		// String classStr = classes[j];
		// for(int k = 0; k < numResults; k++){
		// if(simScore > score[k]){
		// float tmp = score[k];
		// score[k] = simScore;
		// String tmpClass = resultClass[k];
		// resultClass[k] = classStr;
		// simScore = tmp;
		// classStr = tmpClass;
		// }
		// }
		// }
		// for(int l = 0; l < numResults; l++){
		// resultString.append(resultClass[l] + ",");
		// }
		// System.out.println(docToClassify[i] + "," + resultString.toString());
		// }

		// Now do the classification
		for (int i = 0; i < docToClassify.length; i++) {
			String docToClassifyPath = docToClassify[i];
			float top1 = 0.0f;
			String top1Class = null;
			float top2 = 0.0f;
			String top2Class = null;
			float top3 = 0.0f;
			String top3Class = null;
			float top4 = 0.0f;
			String top4Class = null;
			float top5 = 0.0f;
			String top5Class = null;

			for (int j = 0; j < classes.length; j++) {
				float simScore = docIndexBuilder.compareDocuments(
						docToClassifyPath, classes[j]);
				if (simScore > top1) {
					top5 = top4;
					top5Class = top4Class;
					top4 = top3;
					top4Class = top3Class;
					top3 = top2;
					top3Class = top2Class;
					top2 = top1;
					top2Class = top1Class;
					top1 = simScore;
					top1Class = classes[j];
				} else if (simScore > top2) {
					top5 = top4;
					top5Class = top4Class;
					top4 = top3;
					top4Class = top3Class;
					top3 = top2;
					top3Class = top2Class;
					top2 = simScore;
					top2Class = classes[j];
				} else if (simScore > top3) {
					top5 = top4;
					top5Class = top4Class;
					top4 = top3;
					top4Class = top3Class;
					top3 = simScore;
					top3Class = classes[j];
				} else if (simScore > top4) {
					top5 = top4;
					top5Class = top4Class;
					top4 = simScore;
					top4Class = classes[j];
				} else if (simScore > top5) {
					top5 = simScore;
					top5Class = classes[j];
				}
			}
			System.out.println(docToClassifyPath + ", " + top1Class + ", "
					+ top2Class + ", " + top3Class + ", " + top4Class + ", "
					+ top5Class);
//			System.out.println(docIndexBuilder.getDocRandomVector(
//					docToClassifyPath).toString());
		}

//		for (int k = 0; k < classes.length; k++) {
//			System.out
//					.println(classes[k]
//							+ " | "
//							+ docIndexBuilder.getDocRandomVector(classes[k])
//									.toString());
//		}
	}

}
