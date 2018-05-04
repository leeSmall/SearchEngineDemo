package com.study.lucene.myattribute;

import org.apache.lucene.util.Attribute;

/**
 * 1.建立自己的Attribute接口MyCharAttribute
 * @author THINKPAD
 *
 */
public interface MyCharAttribute extends Attribute {
	void setChars(char[] buffer, int length);

	char[] getChars();

	int getLength();

	String getString();
}