package com.luceneanalyzer.use.standardanalyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * Lucene core模块中的 StandardAnalyzer英文分词器使用
 * 英文分词效果好，中文分词效果不好
 * @author THINKPAD
 *
 */
public class LuceneStandardAnalyzerTest {

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
		// Lucene core模块中的 StandardAnalyzer 英文分词器
		try (Analyzer ana = new StandardAnalyzer();) {
			TokenStream ts = ana.tokenStream("coent", etext);
			System.out.println("标准分词器，英文分词效果：");
			doToken(ts);
			ts = ana.tokenStream("content", chineseText);
			System.out.println("标准分词器，中文分词效果：");
			doToken(ts);
		} catch (IOException e) {

		}

	}
}
