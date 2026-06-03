import exceptions.*;
import java.io.*;
import java.util.*;

/**
 * 测试异常处理
 */
public class TestScannerException {
    
    // 定义每个测试文件的预期异常
    private static final Map<String, String> EXPECTED_EXCEPTIONS = new HashMap<>();
    
    static {
        EXPECTED_EXCEPTIONS.put("../testcases/gcd001.obr", "IllegalSymbolException");
        EXPECTED_EXCEPTIONS.put("../testcases/gcd002.obr", "IllegalIntegerException");
        EXPECTED_EXCEPTIONS.put("../testcases/gcd003.obr", "IllegalIntegerRangeException");
        EXPECTED_EXCEPTIONS.put("../testcases/gcd004.obr", "IllegalOctalException");
        EXPECTED_EXCEPTIONS.put("../testcases/gcd005.obr", "IllegalIdentifierLengthException");
        EXPECTED_EXCEPTIONS.put("../testcases/gcd006.obr", "MismatchedCommentException");
    }
    
    public static void main(String[] args) throws Exception {
        String[] testFiles = {
            "../testcases/gcd001.obr",
            "../testcases/gcd002.obr",
            "../testcases/gcd003.obr",
            "../testcases/gcd004.obr",
            "../testcases/gcd005.obr",
            "../testcases/gcd006.obr",
        };
        
        System.out.println("========== Oberon Scanner Exception Test ==========\n");
        
        int passCount = 0;
        int failCount = 0;
        
        for (String testFile : testFiles) {
            boolean result = testScannerFile(testFile);
            if (result) {
                passCount++;
            } else {
                failCount++;
            }
        }
        
        System.out.println("\n========== Test Summary ==========");
        System.out.println("Total: " + testFiles.length);
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.println("==================================\n");
    }
    
    /**
     * 测试单个文件的词法分析和异常捕获
     * @return 测试是否通过
     */
    public static boolean testScannerFile(String filePath) {
        System.out.println("Testing: " + filePath);
        
        String expectedException = EXPECTED_EXCEPTIONS.getOrDefault(filePath, "NO_EXCEPTION");
        System.out.println("Expected Exception: " + expectedException);
        
        OberonScanner scanner = null;
        String actualException = "NO_EXCEPTION";
        boolean testPassed = false;
        
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Error! File not found: " + filePath);
                System.out.println();
                return false;
            }
            
            FileInputStream stream = new FileInputStream(file);
            Reader reader = new InputStreamReader(stream, "UTF-8");
            scanner = new OberonScanner(reader);
            
            // 逐个扫描词法单元
            while (!scanner.yyatEOF()) {
                try {
                    scanner.yylex();
                } catch (IllegalSymbolException e) {
                    actualException = "IllegalSymbolException";
                    break;
                } catch (IllegalIntegerException e) {
                    actualException = "IllegalIntegerException";
                    break;
                } catch (IllegalIntegerRangeException e) {
                    actualException = "IllegalIntegerRangeException";
                    break;
                } catch (IllegalOctalException e) {
                    actualException = "IllegalOctalException";
                    break;
                } catch (IllegalIdentifierLengthException e) {
                    actualException = "IllegalIdentifierLengthException";
                    break;
                } catch (MismatchedCommentException e) {
                    actualException = "MismatchedCommentException";
                    break;
                } catch (LexicalException e) {
                    actualException = e.getClass().getSimpleName();
                    break;
                } catch (Exception e) {
                    actualException = e.getClass().getSimpleName();
                    System.out.println("\nError! Caught: " + actualException + " - " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
            
            stream.close();
            reader.close();
            
        } catch (FileNotFoundException e) {
            actualException = "FileNotFoundException";
            System.out.println("Error! File not found: " + filePath);
        } catch (IOException e) {
            actualException = "IOException";
            System.out.println("Error! IO error: " + e.getMessage());
        } catch (Exception e) {
            actualException = e.getClass().getSimpleName();
            System.out.println("Error! Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 验证结果
        if (expectedException.equals(actualException)) {
            System.out.println("\nTEST PASSED");
            testPassed = true;
        } else {
            System.out.println("\nTEST FAILED");
            System.out.println("   Expected: " + expectedException);
            System.out.println("   Actual:   " + actualException);
            testPassed = false;
        }
        
        System.out.println("-".repeat(60));
        System.out.println();
        return testPassed;
    }
}