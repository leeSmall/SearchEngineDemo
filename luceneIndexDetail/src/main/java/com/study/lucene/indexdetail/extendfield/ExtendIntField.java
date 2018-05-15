package com.study.lucene.indexdetail.extendfield;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

/**
 * 
 * @Description: 扩展整型Field
 * @author liguangsheng
 * @date 2018年5月11日
 *
 */
public class ExtendIntField extends Field {
	public ExtendIntField(String fieldName, int value, FieldType type) {
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
