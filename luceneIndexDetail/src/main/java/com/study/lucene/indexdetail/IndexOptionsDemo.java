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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * 索引选项选择
 * 
 * @author THINKPAD
 *
 */
public class IndexOptionsDemo {

	public static void main(String[] args) {
		// 创建使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);

		// 索引配置对象
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		try ( // 索引存放到文件系统中
				Directory directory = FSDirectory.open((new File("f:/test/indextest")).toPath());

				// 创建索引写对象
				IndexWriter writer = new IndexWriter(directory, config);) {

			// 准备document
			Document doc = new Document();
			// 字段content
			String name = "content";
			String value = "张三说的确实在理";
			FieldType type = new FieldType();
			// 设置是否存储该字段
			type.setStored(true); // 请试试不存储的结果
			// 设置是否对该字段分词
			type.setTokenized(true); // 请试试不分词的结果
			// 设置该字段的索引选项
			type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // 请尝试不同的选项的效果
			type.freeze(); // 使不可更改

			Field field = new Field(name, value, type);
			// 添加字段
			doc.add(field);
			// 加入到索引中
			writer.addDocument(doc);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
