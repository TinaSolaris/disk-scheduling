import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScanFile {
    private final String filePath;
    private ArrayList<SourceDiskRequestData> items;

    public ScanFile(String filePath) {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("The filePath should not be null, empty, or consist of white-space characters only");

        this.filePath = filePath;
    }

    public void scan() throws Exception {
        this.items = new ArrayList<>();

        Scanner fileScan = null;

        try {
            fileScan = new Scanner(new File(filePath));

            int lineNo = 0;
            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();
                readLine(lineNo, line);
                lineNo++;
            }
        }
        finally {
            if (fileScan != null)
                fileScan.close();
        }
    }

    private void readLine(int lineNo, String line) {
        if (lineNo == 0 && line.startsWith("request_no"))
            return;

        try {
            var item = new SourceDiskRequestData(line);
            items.add(item);
        }
        catch (Exception ex) {
            System.out.println("The line: '" + line + "' cannot be parsed: " + ex.getMessage());
        }
    }

    public List<SourceDiskRequestData> getItems() {
        return items;
    }
}