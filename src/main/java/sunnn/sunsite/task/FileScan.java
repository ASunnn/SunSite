package sunnn.sunsite.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sunnn.sunsite.util.InputDataScanner;
import sunnn.sunsite.util.SunSiteProperties;

@Component
public class FileScan {

    private final InputDataScanner scanner;

    @Autowired
    public FileScan(InputDataScanner scanner) {
        this.scanner = scanner;
    }

    @Scheduled(cron = "0 0/15 * * * *")
    public void inputDataScan() {
        scanner.scan(SunSiteProperties.scanAutoFill);
    }
}
