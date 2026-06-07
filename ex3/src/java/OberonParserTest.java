import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * 自动化测试工具，用于验证 Oberon 代码解析器的正确性。
 */
public class OberonParserTest {

    /**
     * 从 .obr 文件第一行解析出的预期结果，包括是否期望无错误、期望的异常类型和行号列表。
     */
    static class Expectation {
        final boolean noError;
        final String  exceptionType;   // e.g. "SemanticException"
        final List<Integer> lines;     // expected line numbers

        Expectation(boolean noError, String exceptionType, List<Integer> lines) {
            this.noError       = noError;
            this.exceptionType = exceptionType;
            this.lines         = lines;
        }
    }

    /**
     * 测试结果对象，包含测试文件名、是否通过以及失败详情（如果未通过）。
     */
    static class TestResult {
        final String  filename;
        final boolean passed;
        final String  detail;   // failure reason, empty when passed

        TestResult(String filename, boolean passed, String detail) {
            this.filename = filename;
            this.passed   = passed;
            this.detail   = detail;
        }
    }

    /**
     * .obr 文件第一行的格式，如 (* Error: SomeException at line N, M. *), 用于提取预期的异常类型和行号列表。
     */
    private static final Pattern HEADER_PATTERN =
        Pattern.compile(
            "\\(\\*\\s*Error:\\s*(\\w+)\\s+at\\s+line\\s+([\\d,\\s]+)\\.\\s*\\*\\)"
        );

    // (* NoError *)
    private static final Pattern NO_ERROR_PATTERN =
        Pattern.compile("\\(\\*\\s*NoError\\s*\\*\\)");

    /**
     * 用于从 parser 的 stderr 输出中提取异常类型和行号的模式，如 "SemanticException at line 42"。
     */
    private static final Pattern STDERR_LINE_PATTERN =
        Pattern.compile("(\\w+)\\s+at\\s+line\\s+(\\d+)");

    public static void main(String[] argv) throws Exception {

        if (argv.length < 1) {
            System.err.println("Usage: java OberonParserTest <directory-with-obr-files>");
            System.exit(1);
        }

        File dir = new File(argv[0]);
        if (!dir.isDirectory()) {
            System.err.println("Not a directory: " + argv[0]);
            System.exit(1);
        }

        // File[] obrFiles = dir.listFiles(
        //     (d, name) -> name.toLowerCase().endsWith(".obr")
        // );
        Pattern p = Pattern.compile("gcd(\\d+)\\.obr", Pattern.CASE_INSENSITIVE);
        File[] obrFiles = dir.listFiles((d, name) -> {
            Matcher m = p.matcher(name);
            return m.matches() && Integer.parseInt(m.group(1)) >= 8;
        });
        if (obrFiles == null || obrFiles.length == 0) {
            System.out.println("No .obr files found in: " + argv[0]);
            return;
        }

        Arrays.sort(obrFiles, Comparator.comparing(File::getName));

        int passed = 0, failed = 0;
        List<TestResult> failures = new ArrayList<>();

        for (File f : obrFiles) {
            TestResult result = runTest(f);
            if (result.passed) {
                passed++;
                System.out.println("[PASS] " + result.filename);
            } else {
                failed++;
                failures.add(result);
                System.out.println("[FAIL] " + result.filename);
                System.out.println("       " + result.detail);
            }
        }

        System.out.println();
        System.out.println("Results: " + passed + " passed, " + failed + " failed"
                           + " out of " + (passed + failed) + " tests.");

        if (failed > 0) {
            System.out.println();
            System.out.println("=== Failure Summary ===");
            for (TestResult r : failures) {
                System.out.println("  " + r.filename + ": " + r.detail);
            }
            System.exit(1);   // non-zero exit so CI/scripts can detect failure
        }
    }

    /**
     * 运行单个测试：解析预期结果，执行 parser，捕获 stderr 输出，并验证结果是否符合预期。
     * @param obrFile 要测试的 .obr 文件
     * @return 一个 TestResult 对象
     */
    private static TestResult runTest(File obrFile) {

        String filename = obrFile.getName();

        // 1. Parse expectation from first line
        Expectation expectation;
        try {
            expectation = parseExpectation(obrFile);
        } catch (IOException e) {
            return new TestResult(filename, false,
                "Could not read file: " + e.getMessage());
        }

        // 2. Run parser, capture stderr
        String stderrOutput;
        try {
            stderrOutput = runParser(obrFile);
        } catch (Exception e) {
            return new TestResult(filename, false,
                "Parser threw unexpected exception: " + e.getMessage());
        }

        // 3. Validate
        return validate(filename, expectation, stderrOutput);
    }

    /**
     * 从 .obr 文件第一行解析预期结果
     * @param f 要解析的 .obr 文件
     * @return 一个 Expectation 对象，包含是否期望无错误、期望的异常类型和行号列表
     * @throws IOException
     */
    private static Expectation parseExpectation(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), "UTF-8"))) {

            String firstLine = br.readLine();
            if (firstLine == null) {
                // Empty file — treat as no error expected
                return new Expectation(true, null, Collections.emptyList());
            }

            // (* NoError *)
            if (NO_ERROR_PATTERN.matcher(firstLine).find()) {
                return new Expectation(true, null, Collections.emptyList());
            }

            // (* Error: SomeException at line N, M. *)
            Matcher m = HEADER_PATTERN.matcher(firstLine);
            if (m.find()) {
                String exType = m.group(1).trim();
                String lineNumbers = m.group(2);
                List<Integer> lines = new ArrayList<>();
                for (String part : lineNumbers.split(",")) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        lines.add(Integer.parseInt(trimmed));
                    }
                }
                return new Expectation(false, exType, lines);
            }

            // First line doesn't match any known format — skip validation
            return new Expectation(true, null, Collections.emptyList());
        }
    }

    /**
     * 运行 parser 解析 .obr 文件，并捕获其 stderr 输出。
     * @param obrFile 要解析的 .obr 文件
     * @return parser 运行期间捕获的 stderr 输出
     * @throws Exception 
     */
    private static String runParser(File obrFile) throws Exception {

        // Redirect System.err to a buffer for the duration of the parse
        PrintStream originalErr = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream captureStream = new PrintStream(buffer, true, "UTF-8");
        System.setErr(captureStream);

        try {
            Reader reader = new InputStreamReader(
                new FileInputStream(obrFile), "UTF-8"
            );
            Parser p = new Parser(new OberonScanner(reader));
            p.parse();
        } catch (Exception e) {
            // Parser may throw — still want to inspect whatever was printed
            // Write the exception itself to the captured stream so callers
            // can inspect it if needed (not counted as a test error on its own)
            captureStream.println(e.getClass().getSimpleName() + " at line 0: " + e.getMessage());
        } finally {
            captureStream.flush();
            System.setErr(originalErr);
        }

        return buffer.toString("UTF-8");
    }

    /**
     * 验证 parser 的 stderr 输出是否符合预期结果。
     * @param filename 测试文件名（用于报告）
     * @param exp 预期结果对象，包含是否期望无错误、期望的异常类型和行号列表
     * @param stderr parser 运行期间捕获的 stderr 输出
     * @return 一个 TestResult 对象
     */
    private static TestResult validate(String filename,
                                       Expectation exp,
                                       String stderr) {
        // Case 1: no error expected
        if (exp.noError) {
            if (stderr.trim().isEmpty()) {
                return new TestResult(filename, true, "");
            } else {
                return new TestResult(filename, false,
                    "Expected no errors but got stderr:\n       " +
                    stderr.trim().replace("\n", "\n       "));
            }
        }

        // Case 2: specific exception + line numbers expected

        // Collect all (exceptionType, lineNumber) pairs from stderr
        List<String> stderrExTypes = new ArrayList<>();
        List<Integer> stderrLines  = new ArrayList<>();

        for (String line : stderr.split("\n")) {
            Matcher m = STDERR_LINE_PATTERN.matcher(line);
            if (m.find()) {
                stderrExTypes.add(m.group(1).trim());
                stderrLines.add(Integer.parseInt(m.group(2).trim()));
            }
        }

        // Check exception type appears at least once
        boolean typeFound = stderrExTypes.contains(exp.exceptionType);
        if (!typeFound) {
            return new TestResult(filename, false,
                "Expected exception type '" + exp.exceptionType
                + "' not found in stderr. Actual types: " + stderrExTypes);
        }

        // Check every expected line number appears in stderr
        List<Integer> missingLines = new ArrayList<>();
        for (int expectedLine : exp.lines) {
            if (!stderrLines.contains(expectedLine)) {
                missingLines.add(expectedLine);
            }
        }
        if (!missingLines.isEmpty()) {
            return new TestResult(filename, false,
                "Expected line number(s) " + missingLines
                + " not found in stderr. Actual lines: " + stderrLines);
        }

        return new TestResult(filename, true, "");
    }
}