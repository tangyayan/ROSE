import java.io.*;

/**
 * 运行入口
 */
public class Main {
    public static void main(String[] argv) throws Exception {

        Reader reader =
            new InputStreamReader(
                new FileInputStream(argv[0]),
                "UTF-8"
            );

        Parser p = new Parser(new OberonScanner(reader));

        p.module();
    }
}