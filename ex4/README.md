# **递归下降程序**

姓名：唐雅妍
学号：23320143
电子邮件：tangyy77@mail2.sysu.edu.cn
联系电话： 18024200442
完成日期：2026/6/7

### 项目目录结构

```text
E:.
│  build.bat          // 编译所有 Java 源文件
│  doc.bat            // 生成 Javadoc 文档
│  gen.bat            // 根据 JFlex 规范生成代码
│  run.bat            // 运行编译器
│  test.bat           // 批量运行测试用例
│  scheme.pdf         // 实验报告
|
├─bin/                // 编译生成的 .class 文件目录
├─doc/                // Javadoc 生成目录
├─lib/                // 第三方依赖库
├─src
│  │  Main.java               // 程序入口
│  │  oberon.flex             // JFlex 词法规则文件 (*.flex)
│  │  OberonScanner.java      // JFlex 生成的词法分析器
│  │  Parser.java             // 递归下降语法分析器实现
│  │  ParserTest.java         // Parser 测试程序
│  │  Symbols.java            // JFlex 返回类型，Parser 使用
│  │  Token.java              // Token 数据结构定义
│  │
│  ├─exceptions/             // 自定义异常类，及错误恢复类
│  │
│  ├─LLTable/                // FIRST/FOLLOW 集及 LL(1) 分析表相关实现
│  │
│  ├─mySymbol/               // 符号表与类型系统实现
│  │
│  └─random_produce/         // Oberon-0 随机测试程序生成器
│
└─testcases/                 // 测试用例目录，包含正确程序与错误程序
```


```

```

### 快速开始

1) 生成词法和语法分析器

```bat
gen.bat
```

2) 编译项目

```bat
build.bat
```

3) 运行编译器

```bat
run.bat
```

4) 批量测试

```bat
test.bat
```

文档生成

```bat
doc.bat
```
