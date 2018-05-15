package com.study.lucene.indexdetail;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * @Description: 索引更新
 * @author liguangsheng
 * @date 2018年5月11日
 *
 */

public class IndexUpdateDemo {

	public static void main(String[] args) {
		// 创建使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);

		// 索引配置对象
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		try (
				// 索引存放目录
				// 存放到文件系统中
				Directory directory = FSDirectory.open((new File("f:/test/indextest")).toPath());

				// 存放到内存中
				// Directory directory = new RAMDirectory();

				// 创建索引写对象
				IndexWriter writer = new IndexWriter(directory, config);) {

			// Term term = new Term("prodId", "p0001");
			Term term = new Term("type", "笔记本电脑");

			// 准备document
			Document doc = new Document();
			// 商品id：字符串，不索引、但存储
			String prodId = "p0003";
			FieldType onlyStoredType = new FieldType();
			onlyStoredType.setTokenized(false);
			onlyStoredType.setIndexOptions(IndexOptions.NONE);
			onlyStoredType.setStored(true);
			onlyStoredType.freeze();
			doc.add(new Field("prodId", prodId, onlyStoredType));

			writer.updateDocument(term, doc);

			// Term term = new Term("name", "笔记本电脑");
			// writer.deleteDocuments(term);

			writer.flush();

			writer.commit();
			System.out.println("执行更新完毕。");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
