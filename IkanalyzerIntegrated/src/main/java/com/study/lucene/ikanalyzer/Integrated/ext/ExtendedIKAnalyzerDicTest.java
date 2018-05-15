package com.study.lucene.ikanalyzer.Integrated.ext;


import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * 扩展 IKAnalyzer的词典测试
 * 
 *
 */
public class ExtendedIKAnalyzerDicTest {

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
		String chineseText = "厉害了我的国一经播出，受到各方好评，强烈激发了国人的爱国之情、自豪感！";
		// IKAnalyzer 细粒度切分
		try (Analyzer ik = new IKAnalyzer4Lucene7();) {
			TokenStream ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 细粒度切分，中文分词效果：");
			doToken(ts);
		}

		// IKAnalyzer 智能切分
		try (Analyzer ik = new IKAnalyzer4Lucene7(true);) {
			TokenStream ts = ik.tokenStream("content", chineseText);
			System.out.println("IKAnalyzer中文分词器 智能切分，中文分词效果：");
			doToken(ts);
		}
	}
}
