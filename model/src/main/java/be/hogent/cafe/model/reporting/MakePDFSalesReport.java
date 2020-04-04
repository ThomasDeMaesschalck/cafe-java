package be.hogent.cafe.model.reporting;
import be.hogent.cafe.model.Beverage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class MakePDFSalesReport {

    public static final String DEST = "reports/salesreport.pdf";

    public static void createPDF(Map<Beverage, Integer> sales, String waiterName, LocalDate date) throws IOException {
        File file = new File(DEST);
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {throw new IllegalStateException("Couldn't create dir: " + parent); }
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(DEST);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);

        //Add paragraphs to the document

        if(!(date == null)) {
            document.add(new Paragraph("Waiter sales report: " + waiterName + " for " + date ));

        }
        else
        {
            document.add(new Paragraph("Waiter sales report: " + waiterName + " since start of employment" ));
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

    }
}

