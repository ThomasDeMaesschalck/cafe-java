package be.hogent.cafe.model.reporting;
import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.Cafe;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;


public class MakePDFSalesReport {

    //exception handling nog af te werken

    private static String reportsDirectory = Cafe.getReportsDirectory();
    private static final Logger logger = LogManager.getLogger(Cafe.class.getName());
    public static final String DEST =  reportsDirectory + "/salesreport.pdf";

    public static boolean createPDF(Map<Beverage, Integer> sales, String waiterName, LocalDate date) throws IOException {
        File file = new File(DEST);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(DEST);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);

        //Add paragraphs to the document

        if (!(date == null)) {
            document.add(new Paragraph("Waiter sales report: " + waiterName + " for " + date));

        } else {
            document.add(new Paragraph("Waiter sales report: " + waiterName + " since start of employment"));
        }

        final double[] totalSales = {0}; //final double array ipv double om hieronder met lambda te kunnen werken

        sales.keySet().forEach(beverage -> {
            double subtotal = (beverage.getPrice() * sales.get(beverage));
            totalSales[0] += subtotal;
            document.add(new Paragraph("Sold " + sales.get(beverage) + "x " + beverage.getBeverageName() + " subtotal: " + subtotal));
        });

        document.add(new Paragraph("Total: " + totalSales[0]));

        //Close document
        document.close();
        if (file.exists()) {
            return true;
        } else
            {
            logger.error("PDF file creation failed");
            return false;
        }
    }
}
