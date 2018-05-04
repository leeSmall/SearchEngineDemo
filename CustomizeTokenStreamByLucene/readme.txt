基于Lucene实现一个英文按照空白字符进行分词的分词器
步骤如下：
1.建立自己的Attribute接口MyCharAttribute
2.建立自定义attribute接口MyCharAttribute的实现类MyCharAttributeImpl
3. 建立分词器MyWhitespaceTokenizer：实现对英文按空白字符进行分词
4.建立分项过滤器：把大写字母转换为小写字母
5. 建立分析器

博客学习地址：http://www.cnblogs.com/leeSmall/p/8993185.html
