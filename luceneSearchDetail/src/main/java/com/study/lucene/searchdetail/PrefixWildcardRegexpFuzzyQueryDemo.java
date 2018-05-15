package com.study.lucene.searchdetail;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;


/**
 * @Description: PrefixQuery前缀查询/WildcardQuery通配符查询/RegexpQuery正则表达式查询/FuzzyQuery模糊查询
 * @author liguangsheng
 * @date 2018年5月12日
 *
 */
public class PrefixWildcardRegexpFuzzyQueryDemo {

	public static void main(String[] args) throws IOException, ParseException {
		// 使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);
		// 索引存储目录
		Directory directory = FSDirectory.open(Paths.get("f:/test/indextest"));
		// 索引读取器
		IndexReader indexReader = DirectoryReader.open(directory);
		// 索引搜索器
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		// String name = "ThinkPad X1 Carbon 20KH0009CD/25CD 超极本轻薄笔记本电脑联想";

		// PrefixQuery 前缀查询
		PrefixQuery prefixQuery = new PrefixQuery(new Term("name", "think"));
		System.out.println("********** PrefixQuery 前缀查询  ***********");
		doSearch(prefixQuery, indexSearcher);

		// WildcardQuery 通配符查询
		WildcardQuery wildcardQuery = new WildcardQuery(
				new Term("name", "think*"));

		System.out.println("********** WildcardQuery 通配符  ***********");
		doSearch(wildcardQuery, indexSearcher);

		// WildcardQuery 通配符查询
		WildcardQuery wildcardQuery2 = new WildcardQuery(
				new Term("name", "厉害了???"));
		System.out.println("********** WildcardQuery 通配符  ***********");
		doSearch(wildcardQuery2, indexSearcher);

		// RegexpQuery 正则表达式查询
		RegexpQuery regexpQuery = new RegexpQuery(new Term("name", "厉害.{4}"));
		System.out.println("**********RegexpQuery 正则表达式查询***********");
		doSearch(regexpQuery, indexSearcher);

		// FuzzyQuery 模糊查询
		FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("name", "thind"));
		System.out.println("**********FuzzyQuery 模糊查询***********");
		doSearch(fuzzyQuery, indexSearcher);

		// FuzzyQuery 模糊查询
		FuzzyQuery fuzzyQuery2 = new FuzzyQuery(new Term("name", "thinkd"), 2);
		System.out.println("**********FuzzyQuery 模糊查询***********");
		doSearch(fuzzyQuery2, indexSearcher);

		// FuzzyQuery 模糊查询
		FuzzyQuery fuzzyQuery3 = new FuzzyQuery(new Term("name", "thinkpaddd"));
		System.out.println("**********FuzzyQuery 模糊查询***********");
		doSearch(fuzzyQuery3, indexSearcher);

		// FuzzyQuery 模糊查询
		FuzzyQuery fuzzyQuery4 = new FuzzyQuery(new Term("name", "thinkdaddd"));
		System.out.println("**********FuzzyQuery 模糊查询***********");
		doSearch(fuzzyQuery4, indexSearcher);

		// 使用完毕，关闭、释放资源
		indexReader.close();
		directory.close();
	}

	private static void doSearch(Query query, IndexSearcher indexSearcher)
			throws IOException {
		// 打印输出查询
		System.out.println("query:  " + query.toString());

		// 搜索，得到TopN的结果（结果中有命中总数，topN的scoreDocs（评分文档（文档id，评分）））
		TopDocs topDocs = indexSearcher.search(query, 10); // 前10条

		System.out.println("**** 查询结果 ");
		// 获得总命中数
		System.out.println("总命中数：" + topDocs.totalHits);
		// 遍历topN结果的scoreDocs,取出文档id对应的文档信息
		for (ScoreDoc sdoc : topDocs.scoreDocs) {
			// 根据文档id取存储的文档
			Document hitDoc = indexSearcher.doc(sdoc.doc);
			System.out.println("-------------- docId=" + sdoc.doc + ",score="
					+ sdoc.score);
			// 取文档的字段
			System.out.println("prodId:" + hitDoc.get("prodId"));
			System.out.println("name:" + hitDoc.get("name"));
			System.out.println("simpleIntro:" + hitDoc.get("simpleIntro"));
			System.out.println("price:" + hitDoc.get("price"));

			System.out.println();
		}

	}
}
