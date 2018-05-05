package com.study.lucene.ikanalyzer.Integrated;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 * IKAnalyzer分词器集成测试:
 * 细粒度切分：把词分到最细
 * 智能切分：根据词库进行拆分符合我们的语言习惯
 * 
 * @author THINKPAD
 *
 */
public class IKAnalyzerTest {
	private static void doToken(TokenStream ts) throws IOException {
		ts.reset();
		CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
		while (ts.incrementToken()) {
			System.out.print(cta.toString() + "|");
		}
		System.out.println();
		ts.end();
		ts.close();
	}

	public static void main(String[] args) throws IOException {

		String etext = "Analysis is one of the main causes of slow indexing. Simply put, the more you analyze the slower analyze the indexing (in most cases).";
		String chineseText = "张三说的确实在理。";
		/**
		 * ikanalyzer 中文分词器 因为Analyzer的createComponents方法API改变了 需要我们自己实现
		 * 分析器IKAnalyzer4Lucene7和分词器IKTokenizer4Lucene7
		 */
		// IKAnalyzer 细粒度切分
		try (Analyzer ik = new IKAnalyzer4Lucene7();) {
			TokenStream ts = ik.tokenStream("content", etext);
			System.out.println("IKAnalyzer中文分词器 细粒度切分，英文分词效果：");
			doToken(ts);
			ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 细粒度切分，中文分词效果：");
			doToken(ts);
		}

		// IKAnalyzer 智能切分
		try (Analyzer ik = new IKAnalyzer4Lucene7(true);) {
			TokenStream ts = ik.tokenStream("content", etext);
			System.out.println("IKAnalyzer中文分词器 智能切分，英文分词效果：");
			doToken(ts);
			ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 智能切分，中文分词效果：");
			doToken(ts);
		}
	}
}
