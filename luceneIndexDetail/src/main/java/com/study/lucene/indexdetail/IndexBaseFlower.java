package com.study.lucene.indexdetail;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * Lucene索引创建代码示例
 * @author THINKPAD
 *
 */
public class IndexBaseFlower {
	public static void main(String[] args) throws IOException {
		// 创建使用的分词器
		Analyzer analyzer = new IKAnalyzer4Lucene7(true);
		// 索引配置对象
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// 设置索引库的打开模式：新建、追加、新建或追加
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		// 索引存放目录
		// 存放到文件系统中
		Directory directory = FSDirectory
				.open((new File("f:/test/indextest")).toPath());

		// 存放到内存中
		// Directory directory = new RAMDirectory();

		// 创建索引写对象
		IndexWriter writer = new IndexWriter(directory, config);

		// 创建document
		Document doc = new Document();
		// 往document中添加 商品id字段
		doc.add(new StoredField("prodId", "p0001"));

		// 往document中添加 商品名称字段
		String name = "ThinkPad X1 Carbon 20KH0009CD/25CD 超极本轻薄笔记本电脑联想";
		doc.add(new TextField("name", name, Store.YES));

		// 将文档添加到索引
		writer.addDocument(doc);

		// 删除文档
		// writer.deleteDocuments(terms);

		// 修改文档
		// writer.updateDocument(term, doc);

		// 刷新
		writer.flush();

		// 提交
		writer.commit();

		// 回滚
		// writer.rollback();

		// 关闭 会提交
		writer.close();
		directory.close();
	}

}
