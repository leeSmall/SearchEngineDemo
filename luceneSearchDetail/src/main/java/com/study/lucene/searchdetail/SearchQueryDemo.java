package com.study.lucene.searchdetail;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * @Description: 搜索查询示例
 * @author liguangsheng
 * @date 2018年5月12日
 *
 */

public class SearchQueryDemo {

	public static void main(String[] args) throws IOException, ParseException {
		// 使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);
		// 索引存储目录
		Directory directory = FSDirectory.open(Paths.get("f:/test/indextest"));
		// 索引读取器
		IndexReader indexReader = DirectoryReader.open(directory);
		// 索引搜索器
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 要搜索的字段
		String filedName = "name";

		// 1、词项查询
		Query query1 = new TermQuery(new Term(filedName, "thinkpad"));
		System.out.println("************** 词项查询 ******************");
		doSearch(query1, indexSearcher);

		// 2、布尔查询
		Query query2 = new TermQuery(new Term("simpleIntro", "英特尔"));
		BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
		booleanQueryBuilder.add(query1, Occur.SHOULD);
		booleanQueryBuilder.add(query2, Occur.MUST);
		BooleanQuery booleanQuery = booleanQueryBuilder.build();

		// 可像下一行这样写
		// BooleanQuery booleanQuery = new BooleanQuery.Builder()
		// .add(query1, Occur.SHOULD).add(query2, Occur.MUST).build();

		System.out.println("************** 布尔查询 ******************");
		doSearch(booleanQuery, indexSearcher);

		// 3、PhraseQuery 短语查询
		// String name = "ThinkPad X1 Carbon 20KH0009CD/25CD 超极本轻薄笔记本电脑联想";
		PhraseQuery phraseQuery1 = new PhraseQuery("name", "thinkpad", "carbon");
		System.out.println("************** phrase 短语查询  ******************");
		doSearch(phraseQuery1, indexSearcher);

		PhraseQuery phraseQuery2 = new PhraseQuery(1, "name", "thinkpad", "carbon");
		System.out.println("************** phrase 短语查询  ******************");
		doSearch(phraseQuery2, indexSearcher);

		// slop示例 3表示最大可以移动的位置，移动的过程中只要匹配短语carbon thinkpad即可
		PhraseQuery phraseQuery2Slop = new PhraseQuery(3, "name", "carbon", "thinkpad");
		System.out.println("********** phrase slop 短语查询  ***************");
		doSearch(phraseQuery2Slop, indexSearcher);

		PhraseQuery phraseQuery3 = new PhraseQuery("name", "笔记本电脑", "联想");
		System.out.println("************** phrase 短语查询  ******************");
		doSearch(phraseQuery3, indexSearcher);

		// slop示例
		PhraseQuery phraseQuery3Slop = new PhraseQuery(2, "name", "联想", "笔记本电脑");
		System.out.println("************** phrase s 短语查询  ******************");
		doSearch(phraseQuery3Slop, indexSearcher);

		PhraseQuery phraseQuery4 = new PhraseQuery.Builder().add(new Term("name", "笔记本电脑"), 4) // 4、5是这个词的位置，和 0、1等同
				.add(new Term("name", "联想"), 5).build();
		System.out.println("********** phrase Builder 1 短语查询  **************");
		doSearch(phraseQuery4, indexSearcher);

		// 等同 phraseQuery4
		PhraseQuery phraseQuery5 = new PhraseQuery.Builder().add(new Term("name", "笔记本电脑"), 0) // 4、5是这个词的位置，和 0、1等同
				.add(new Term("name", "联想"), 1).build();
		System.out.println("*********** phrase Builder 2  短语查询  ***********");
		doSearch(phraseQuery5, indexSearcher);

		// 4 MultiPhraseQuery 多重短语查询
		Term[] terms = new Term[2];
		terms[0] = new Term("name", "笔记本");
		terms[1] = new Term("name", "笔记本电脑");
		Term t = new Term("name", "联想");
		MultiPhraseQuery multiPhraseQuery = new MultiPhraseQuery.Builder().add(terms).add(t).build();
		System.out.println("************** multiPhraseQuery 短语查询  ******************");
		doSearch(multiPhraseQuery, indexSearcher);

		// 对比 PhraseQuery在同位置加入多个词 ，同位置的多个词都需匹配，所以查不出。
		PhraseQuery pquery = new PhraseQuery.Builder().add(terms[0], 0).add(terms[1], 0).add(t, 1).build();
		System.out.println("************** multiPhraseQuery  对比 PhraseQuery 短语查询  ******************");
		doSearch(pquery, indexSearcher);

		// 使用完毕，关闭、释放资源
		indexReader.close();
		directory.close();
	}

	private static void doSearch(Query query, IndexSearcher indexSearcher) throws IOException {
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
			System.out.println("-------------- docId=" + sdoc.doc + ",score=" + sdoc.score);
			// 取文档的字段
			System.out.println("prodId:" + hitDoc.get("prodId"));
			System.out.println("name:" + hitDoc.get("name"));
			System.out.println("simpleIntro:" + hitDoc.get("simpleIntro"));
			System.out.println("price:" + hitDoc.get("price"));

			System.out.println();
		}

	}
}
