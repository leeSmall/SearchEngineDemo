package com.study.lucene.mytokenizer;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;

import com.study.lucene.myattribute.MyCharAttribute;


/**
 * 3. 建立分词器MyWhitespaceTokenizer：实现对英文按空白字符进行分词
 * @author THINKPAD
 *
 */
public class MyWhitespaceTokenizer  extends Tokenizer {


	// 需要记录的属性
	// 词
	MyCharAttribute charAttr = this.addAttribute(MyCharAttribute.class);

	// 存词的出现位置

	// 存放词的偏移

	//
	char[] buffer = new char[255];
	int length = 0;
	int c;

	@Override
	public boolean incrementToken() throws IOException {
		// 清除所有的词项属性
		clearAttributes();
		length = 0;
		while (true) {
			c = this.input.read();

			if (c == -1) {
				if (length > 0) {
					// 复制到charAttr
					this.charAttr.setChars(buffer, length);
					return true;
				} else {
					return false;
				}
			}

			if (Character.isWhitespace(c)) {
				if (length > 0) {
					// 复制到charAttr
					this.charAttr.setChars(buffer, length);
					return true;
				}
			}

			buffer[length++] = (char) c;
		}
	}


}
