Will become a heading
==============

Will become a sub heading
--------------

*This will be Italic*

**This will be Bold**

- This will be a list item
- This will be a list item

    Add a indent and this will end up as code

Lemmagen4J
==============


Project overview
--------------

This project - Lemmagen4J is just a rewrite of C#.NET opensource Lemmagen project (*http://lemmatise.ijs.si/*) into Java language.

The algorithm is therefore more thoroughly explained in the paper: JURŠIČ, Matjaž, MOZETIČ, Igor, ERJAVEC, Tomaž, LAVRAČ, Nada. LemmaGen : multilingual lemmatisation with induced Ripple-Down rules. J. univers. comput. sci. (Print), 2010, vol. 16, no. 9, str. 1190-1214.


Developer guidelines
--------------

In the data folder, there are:
- training files (english, slovene): wfl-me-*-.tbl
- built Lemmagen4J models (english, slovene): lemmagen*.obj
- memory usage report

In the lib folder there is built Lemmagen4J as a jar file, which can be used as a library in your applications.

Generally, you would need *.obj and *.jar files to include Lemmagen4J into your application.

How to use Lemmagen4J
--------------

    //load model (you can also use InputStream)
    Lemmatizer lm = LemmagenFactory.instance("data/lemmagenENModel.obj");
    //lemmatize words
    String lemmatized = lm.Lemmatize("word-to-lemmatize");