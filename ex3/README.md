# **自动生成语法分析程序**

姓名：唐雅妍
学号：23320143
电子邮件：tangyy77@mail2.sysu.edu.cn
联系电话： 18024200442
完成日期：2026/6/5

### 项目目录结构

```text
E:.
│  build.bat          // 编译所有 Java 源文件
│  doc.bat            // 生成 Javadoc 文档
│  gen.bat            // 根据 JFlex/CUP 规范生成代码
│  run.bat            // 运行编译器
│  test.bat           // 批量运行测试用例
│  yaccgen.pdf        // 项目说明或实验要求文档
│
├─bin/                // 编译生成的 .class 文件
├─doc/                // Javadoc 生成目录
├─lib/                // 第三方依赖库
├─simple_test/        // 简单测试程序
├─src
│  ├─java
│  │  │
│  │  │  JavaSymbol.java
│  │  │      // 扩展 CUP Symbol
│  │  │
│  │  │  Main.java
│  │  │      // 编译器入口程序
│  │  │
│  │  │  OberonParserTest.java
│  │  │      // Parser 测试程序
│  │  │
│  │  │  OberonScanner.java
│  │  │      // JFlex 生成的词法分析器
│  │  │
│  │  │  Parser.java
│  │  │      // Java CUP 生成的语法分析器
│  │  │
│  │  │  PendingEdges.java
│  │  │      // 调用图构建过程中暂存边信息
│  │  │
│  │  │  sym.java
│  │  │      // CUP 自动生成的终结符编号表
│  │  │
│  │  ├─exceptions/
│  │  │      // 自定义异常类，及错误恢复类
│  │  │
│  │  └─mySymbol/
│  │         // 符号表与类型系统实现
│  │
│  ├─javacup/
│  │      // CUP 语法文件 (*.cup)
│  │
│  └─jflex/
│         // JFlex 词法规则文件 (*.flex)
│
└─testcases/
       // 测试用例目录
       // 包含正确程序与错误程序
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
