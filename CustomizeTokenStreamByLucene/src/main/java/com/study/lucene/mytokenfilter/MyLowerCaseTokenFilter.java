package com.study.lucene.mytokenfilter;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import com.study.lucene.myattribute.MyCharAttribute;

/**
 * 4.建立分项过滤器：把大写字母转换为小写字母
 * @author THINKPAD
 *
 */
public class MyLowerCaseTokenFilter extends TokenFilter {

	public MyLowerCaseTokenFilter(TokenStream input) {
		super(input);
	}

	MyCharAttribute charAttr = this.addAttribute(MyCharAttribute.class);

	@Override
	public boolean incrementToken() throws IOException {
		boolean res = this.input.incrementToken();
		if (res) {
			char[] chars = charAttr.getChars();
			int length = charAttr.getLength();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					chars[i] = Character.toLowerCase(chars[i]);
				}
			}
		}
		return res;
	}
}
