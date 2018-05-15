package com.study.lucene.indexdetail;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import com.study.lucene.ikanalyzer.Integrated.IKAnalyzer4Lucene7;

/**
 * 索引的创建
 * 
 * @author THINKPAD
 *
 */
public class IndexWriteDemo {

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

			// 准备document
			Document doc = new Document();
			// 商品id：字符串，不索引、但存储
			String prodId = "p0001";
			FieldType onlyStoredType = new FieldType();
			onlyStoredType.setTokenized(false);
			onlyStoredType.setIndexOptions(IndexOptions.NONE);
			onlyStoredType.setStored(true);
			onlyStoredType.freeze();
			doc.add(new Field("prodId", prodId, onlyStoredType));

			// 等同下一行
			// doc.add(new StoredField("prodId", prodId));

			// 商品名称：字符串，分词索引(存储词频、位置、偏移量)、存储
			String name = "ThinkPad X1 Carbon 20KH0009CD/25CD 超极本轻薄笔记本电脑联想";
			FieldType indexedAllStoredType = new FieldType();
			indexedAllStoredType.setStored(true);
			indexedAllStoredType.setTokenized(true);
			indexedAllStoredType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			indexedAllStoredType.freeze();

			doc.add(new Field("name", name, indexedAllStoredType));

			// 图片链接：仅存储
			String imgUrl = "http://www.cnblogs.com/aaa";
			doc.add(new Field("imgUrl", imgUrl, onlyStoredType));

			// 商品简介：文本，分词索引（不需要支持短语、临近查询）、存储，结果中支持高亮显示
			String simpleIntro = "集成显卡 英特尔 酷睿 i5-8250U 14英寸";
			FieldType indexedTermVectorsStoredType = new FieldType();
			indexedTermVectorsStoredType.setStored(true);
			indexedTermVectorsStoredType.setTokenized(true);
			indexedTermVectorsStoredType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			indexedTermVectorsStoredType.setStoreTermVectors(true);
			indexedTermVectorsStoredType.setStoreTermVectorPositions(true);
			indexedTermVectorsStoredType.setStoreTermVectorOffsets(true);
			indexedTermVectorsStoredType.freeze();

			doc.add(new Field("simpleIntro", simpleIntro, indexedTermVectorsStoredType));

			// 价格，整数，单位分，不索引、存储、要支持排序
			int price = 999900;
			FieldType numericDocValuesType = new FieldType();
			numericDocValuesType.setTokenized(false);
			numericDocValuesType.setIndexOptions(IndexOptions.NONE);
			numericDocValuesType.setStored(true);
			numericDocValuesType.setDocValuesType(DocValuesType.NUMERIC);
			numericDocValuesType.setDimensions(1, Integer.BYTES);
			numericDocValuesType.freeze();

			doc.add(new MyIntField("price", price, numericDocValuesType));

			// 与下两行等同
			// doc.add(new StoredField("price", price));
			// doc.add(new NumericDocValuesField("price", price));

			// 类别：字符串，索引不分词，不存储、支持分类统计,多值
			FieldType indexedDocValuesType = new FieldType();
			indexedDocValuesType.setTokenized(false);
			indexedDocValuesType.setIndexOptions(IndexOptions.DOCS);
			indexedDocValuesType.setDocValuesType(DocValuesType.SORTED_SET);
			indexedDocValuesType.freeze();

			doc.add(new Field("type", "电脑", indexedDocValuesType) {
				@Override
				public BytesRef binaryValue() {
					return new BytesRef((String) this.fieldsData);
				}
			});
			doc.add(new Field("type", "笔记本电脑", indexedDocValuesType) {
				@Override
				public BytesRef binaryValue() {
					return new BytesRef((String) this.fieldsData);
				}
			});

			// 等同下四行
			// doc.add(new StringField("type", "电脑", Store.NO));
			// doc.add(new SortedSetDocValuesField("type", new BytesRef("电脑")));
			// doc.add(new StringField("type", "笔记本电脑", Store.NO));
			// doc.add(new SortedSetDocValuesField("type", new
			// BytesRef("笔记本电脑")));

			// 商家 索引(不分词)，存储、按面（分类）查询
			String fieldName = "shop";
			String value = "联想官方旗舰店";
			doc.add(new StringField(fieldName, value, Store.YES));
			doc.add(new SortedDocValuesField(fieldName, new BytesRef(value)));

			// 上架时间：数值，排序需要
			long upShelfTime = System.currentTimeMillis();
			doc.add(new NumericDocValuesField("upShelfTime", upShelfTime));

			writer.addDocument(doc);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static class MyIntField extends Field {

		public MyIntField(String fieldName, int value, FieldType type) {
			super(fieldName, type);
			this.fieldsData = Integer.valueOf(value);
		}

		@Override
		public BytesRef binaryValue() {
			byte[] bs = new byte[Integer.BYTES];
			NumericUtils.intToSortableBytes((Integer) this.fieldsData, bs, 0);
			return new BytesRef(bs);
		}
	}

}
