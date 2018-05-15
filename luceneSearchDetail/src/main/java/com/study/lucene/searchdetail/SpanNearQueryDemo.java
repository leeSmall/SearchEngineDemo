package com.study.lucene.searchdetail;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;


/**
 * @Description: SpanNearQuery 临近查询（跨度查询）
 * @author liguangsheng
 * @date 2018年5月12日
 *
 */
public class SpanNearQueryDemo {

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

		// SpanNearQuery 临近查询
		SpanTermQuery tq1 = new SpanTermQuery(new Term("name", "thinkpad"));
		SpanTermQuery tq2 = new SpanTermQuery(new Term("name", "carbon"));
		SpanNearQuery spanNearQuery = new SpanNearQuery(
				new SpanQuery[] { tq1, tq2 }, 1, true);

		System.out.println("************** SpanNearQuery 临近查询  ************");
		doSearch(spanNearQuery, indexSearcher);

		// 下面的例子词是反序的
		SpanNearQuery spanNearQuery2 = new SpanNearQuery(
				new SpanQuery[] { tq2, tq1 }, 1, true);

		System.out.println(
				"************** SpanNearQuery 临近查询 2 1,true************");
		doSearch(spanNearQuery2, indexSearcher);

		SpanNearQuery spanNearQuery3 = new SpanNearQuery(
				new SpanQuery[] { tq2, tq1 }, 3, true);

		System.out.println(
				"************** SpanNearQuery 临近查询 3  3, true************");
		doSearch(spanNearQuery3, indexSearcher);

		SpanNearQuery spanNearQuery4 = new SpanNearQuery(
				new SpanQuery[] { tq2, tq1 }, 3, false);

		System.out.println(
				"************** SpanNearQuery 临近查询 4  3, false************");
		doSearch(spanNearQuery4, indexSearcher);

		// SpanNearQuery 临近查询 gap slop 使用 1
		SpanTermQuery ctq1 = new SpanTermQuery(new Term("name", "张三"));
		SpanTermQuery ctq2 = new SpanTermQuery(new Term("name", "在理"));
		SpanNearQuery.Builder spanNearQueryBuilder = SpanNearQuery
				.newOrderedNearQuery("name");
		spanNearQueryBuilder.addClause(ctq1).addGap(0).setSlop(2)
				.addClause(ctq2);

		System.out.println("************** SpanNearQuery 临近查询  ************");
		doSearch(spanNearQueryBuilder.build(), indexSearcher);

		// SpanNearQuery 临近查询 gap slop 使用 2
		SpanNearQuery.Builder spanNearQueryBuilder2 = SpanNearQuery
				.newOrderedNearQuery("name");
		spanNearQueryBuilder2.addClause(ctq1).addGap(2).setSlop(0)
				.addClause(ctq2);

		System.out.println("************** SpanNearQuery 临近查询  ************");
		doSearch(spanNearQueryBuilder2.build(), indexSearcher);

		// SpanNearQuery 临近查询 gap slop 使用 3
		SpanNearQuery.Builder spanNearQueryBuilder3 = SpanNearQuery
				.newOrderedNearQuery("name");
		spanNearQueryBuilder3.addClause(ctq1).addGap(1).setSlop(1)
				.addClause(ctq2);

		System.out.println("************** SpanNearQuery 临近查询  ************");
		doSearch(spanNearQueryBuilder3.build(), indexSearcher);

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
