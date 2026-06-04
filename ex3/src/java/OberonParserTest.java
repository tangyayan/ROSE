import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Test runner for Oberon parser error output.
 *
 * For each .obr file:
 *   - Reads the first line to extract expected exception type and line numbers
 *   - Runs the parser and captures stderr
 *   - Verifies that the expected exception type appears in stderr
 *   - Verifies that every expected line number appears in stderr
 *
 * Expected first-line formats:
 *   (* Error: IllegalSymbolException at line 11. *)
 *   (* Error: SemanticException at line 33, 41. *)
 *   (* NoError *)  ->  stderr must be empty
 */
public class OberonParserTest {

    /**
     * Parsed expectation from the first line of the .obr file
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
     * Result of running a test on a single .obr file
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
     * Pattern to parse the first line of the .obr file for expected error type and line numbers.
     * Example: (* Error: SemanticException at line 33, 41. *)
     */
    private static final Pattern HEADER_PATTERN =
        Pattern.compile(
            "\\(\\*\\s*Error:\\s*(\\w+)\\s+at\\s+line\\s+([\\d,\\s]+)\\.\\s*\\*\\)"
        );

    // (* NoError *)
    private static final Pattern NO_ERROR_PATTERN =
        Pattern.compile("\\(\\*\\s*NoError\\s*\\*\\)");

    // stderr line:  SomeException at line 5: message
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

        File[] obrFiles = dir.listFiles(
            (d, name) -> name.toLowerCase().endsWith(".obr")
        );
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
     * Run the test for a single .obr file
     * @param obrFile the .obr file to test
     * @return a TestResult indicating pass/fail and details of any failure
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
     * Parse the first line of the .obr file to extract the expected error type and line numbers.
     * @param f the .obr file to read
     * @return an Expectation object representing the parsed expectation
     * @throws IOException if there is an error reading the file
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
     * Run the Oberon parser on the given .obr file, capturing anything printed to System.err.
     * @param obrFile the .obr file to parse
     * @return the captured stderr output as a string 
     * @throws Exception if the parser throws an exception during parsing (still captures stderr output)
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
     * Validate the parser's stderr output against the expected exception type and line numbers.
     * @param filename the name of the .obr file being tested (for reporting purposes)
     * @param exp the parsed expectation from the .obr file's first line
     * @param stderr the captured stderr output from running the parser on the .obr file
     * @return a TestResult indicating whether the test passed and details of any failure
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