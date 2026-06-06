
/**
 * 它接受一个或多个输入文件，并将它们传递给扫描器，直到所有文件都被扫描完为止。
 */
public class TestScanner {
    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) {
            System.out.println("Usage : java TestScanner [ --encoding <name> ] <inputfile(s)>");
          }
          else {
            int firstFilePos = 0;
            String encodingName = "UTF-8";
            if (argv[0].equals("--encoding")) {
              firstFilePos = 2;
              encodingName = argv[1];
              try {
                // Side-effect: is encodingName valid?
                java.nio.charset.Charset.forName(encodingName);
              } catch (Exception e) {
                System.out.println("Invalid encoding '" + encodingName + "'");
                return;
              }
            }
            for (int i = firstFilePos; i < argv.length; i++) {
              OberonScanner scanner = null;
              try {
                java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
                java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
                scanner = new OberonScanner(reader);
                do {
                  System.out.print(scanner.yylex());
                } while (!scanner.yyatEOF());
      
              }
              catch (java.io.FileNotFoundException e) {
                System.out.println("File not found : \""+argv[i]+"\"");
              }
              catch (java.io.IOException e) {
                System.out.println("IO error scanning file \""+argv[i]+"\"");
                System.out.println(e);
              }
              catch (Exception e) {
                System.out.println("Unexpected exception:");
                e.printStackTrace();
              }
            }
          }
    }
}
