package com.study.lucene.searchdetail;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * @Description: lucene 搜索基本流程示例
 * @author liguangsheng
 * @date 2018年5月11日
 *
 */

public class SearchBaseFlow {

	public static void main(String[] args)
			throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
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
		// 查询生成器（解析输入生成Query查询对象）
		QueryParser parser = new QueryParser(filedName, analyzer);
		// 通过parse解析输入（分词），生成query对象
		Query query = parser.parse("Thinkpad");
		// 搜索，得到TopN的结果（结果中有命中总数，topN的scoreDocs（评分文档（文档id，评分）））
		TopDocs topDocs = indexSearcher.search(query, 10); // 前10条

		// 获得总命中数
		System.out.println(topDocs.totalHits);
		// 遍历topN结果的scoreDocs,取出文档id对应的文档信息
		for (ScoreDoc sdoc : topDocs.scoreDocs) {
			// 根据文档id取存储的文档
			Document hitDoc = indexSearcher.doc(sdoc.doc);
			// 取文档的字段
			System.out.println(hitDoc.get(filedName));
		}

		// 使用完毕，关闭、释放资源
		indexReader.close();
		directory.close();
	}
}
