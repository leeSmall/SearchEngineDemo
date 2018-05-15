package com.study.lucene.searchdetail;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;


/**
 * @Description: QueryParser示例,查询解析器会对输入的查询短语进行分词
 * @author liguangsheng
 * @date 2018年5月12日
 *
 */
   
public class QueryParserDemo {

	public static void main(String[] args)
			throws IOException, ParseException, QueryNodeException {
		// 使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);
		// 索引存储目录
		Directory directory = FSDirectory.open(Paths.get("f:/test/indextest"));
		// 索引读取器
		IndexReader indexReader = DirectoryReader.open(directory);
		// 索引搜索器
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		/**
		 * 用法1 传统解析器-单默认字段 QueryParser：
		 */
		// 要搜索的默认字段
		String defaultFiledName = "name";
		// 查询生成器（解析输入生成Query查询对象）
		QueryParser parser = new QueryParser(defaultFiledName, analyzer);
		// parser.setPhraseSlop(2);
		// 通过parse解析输入，生成query对象
		Query query1 = parser.parse(
				"(name:\"联想笔记本电脑\" OR simpleIntro:英特尔) AND type:电脑 AND price:999900");
		// 等同query1，如果没有指明要搜索的字段则使用默认值name
		Query query2 = parser.parse(
				"(\"联想笔记本电脑\" OR simpleIntro:英特尔) AND type:电脑 AND price:999900");

		System.out.println("************** query1  ************");
		doSearch(query1, indexSearcher);

		System.out.println("************** query2  ************");
		doSearch(query2, indexSearcher);

		Query query3 = parser.parse(
				"(\"联想笔记本电脑\" OR simpleIntro:英特尔) AND type:电脑 AND price:[800000 TO 1000000]");

		System.out.println("************** query3  ************");
		doSearch(query3, indexSearcher);

		// 为什么query3查不出结果？？？ 该如何改
		BooleanQuery bquery = new BooleanQuery.Builder()
				.add(parser
						.parse("(\"联想笔记本电脑\" OR simpleIntro:英特尔) AND type:电脑 "),
						Occur.MUST)
				.add(IntPoint.newRangeQuery("price", 800000, 1000000),
						Occur.MUST)
				.build();

		System.out.println("************** bquery  ************");
		doSearch(bquery, indexSearcher);

		/**
		 * 用法2  传统解析器-多默认字段  MultiFieldQueryParser：
		 */
		String[] multiDefaultFields = { "name", "type", "simpleIntro" };
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(
				multiDefaultFields, analyzer);
		// 设置默认的操作
		multiFieldQueryParser.setDefaultOperator(Operator.OR);
		Query query4 = multiFieldQueryParser.parse("笔记本电脑 AND price:1999900");

		System.out.println("************** query4  ************");
		doSearch(query4, indexSearcher);

		/**
		 *  用法3  新解析框架的标准解析器：StandardQueryParser：
		 */
		StandardQueryParser queryParserHelper = new StandardQueryParser(
				analyzer);
		// 设置默认字段
		// queryParserHelper.setMultiFields(CharSequence[] fields);
		// queryParserHelper.setPhraseSlop(8);
		// Query query = queryParserHelper.parse("a AND b", "defaultField");
		Query query5 = queryParserHelper.parse(
				"(\"联想笔记本电脑\" OR simpleIntro:英特尔) AND type:电脑 AND price:1999900",
				"name");

		System.out.println("************** query5  ************");
		doSearch(query5, indexSearcher);

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
