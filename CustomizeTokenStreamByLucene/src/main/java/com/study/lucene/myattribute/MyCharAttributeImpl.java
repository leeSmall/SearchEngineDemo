package com.study.lucene.myattribute;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;

/**
 * 2.建立自定义attribute接口MyCharAttribute的实现类MyCharAttributeImpl
 * 注意：MyCharAttributeImpl一定要和MyCharAttribute放在一个包下，否则会出现没有MyCharAttribute的实现类，
 * 这是由org.apache.lucene.util.AttributeFactory.DefaultAttributeFactory.findImplClass(Class<? extends Attribute>)这个方法决定的
 * @author THINKPAD
 *
 */
public class MyCharAttributeImpl extends AttributeImpl implements MyCharAttribute {
	private char[] chatTerm = new char[255];
	private int length = 0;

	@Override
	public void setChars(char[] buffer, int length) {
		this.length = length;
		if (length > 0) {
			System.arraycopy(buffer, 0, this.chatTerm, 0, length);
		}
	}

	public char[] getChars() {
		return this.chatTerm;
	}

	public int getLength() {
		return this.length;
	}

	@Override
	public String getString() {
		if (this.length > 0) {
			return new String(this.chatTerm, 0, length);
		}
		return null;
	}

	@Override
	public void clear() {
		this.length = 0;
	}

	@Override
	public void reflectWith(AttributeReflector reflector) {

	}

	@Override
	public void copyTo(AttributeImpl target) {

	}
}
