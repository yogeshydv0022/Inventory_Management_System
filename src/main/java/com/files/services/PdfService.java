package com.files.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.files.exception.BillNotFoundException;
import com.files.model.Bill;
import com.files.model.ProductBill;
import com.files.repository.BillRepository;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


@Service
public class PdfService {
	
	@Autowired
	private BillRepository billRepository;
	
	public void generateBillPdf(Bill bill, boolean paymentStatus) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf, PageSize.A4);

			// Add Invoice Title
			document.add(new Paragraph("Invoice").setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));

			// Add Inventory Management System Title
			document.add(new Paragraph("Inventory Management System").setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER));

			// Add Today's Date
			String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			document.add(new Paragraph("Date: " + todayDate).setFontSize(12).setTextAlignment(TextAlignment.RIGHT));

			// Add Generated At Date
			document.add(new Paragraph("Generated At: " + bill.getGeneratedAt()).setFontSize(12)
					.setTextAlignment(TextAlignment.RIGHT));

			// Add Table with Bill Details
			float[] columnWidths = { 1, 2, 1, 1, 1 };
			Table table = new Table(columnWidths);
			table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

			table.addHeaderCell(new Cell().add(new Paragraph("S.No")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(
					new Cell().add(new Paragraph("Product Name")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(new Cell().add(new Paragraph("Price")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(
					new Cell().add(new Paragraph("Quantity")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(
					new Cell().add(new Paragraph("Subtotal")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

			List<ProductBill> productBills = bill.getProducts();
			for (int i = 0; i < productBills.size(); i++) {
				ProductBill productBill = productBills.get(i);
				table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
				table.addCell(new Cell().add(new Paragraph(productBill.getProductName())));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(productBill.getPrice()))));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(productBill.getQuantity()))));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(productBill.getSubtotal()))));
			}

			table.addCell(new Cell(1, 4).add(new Paragraph("Total Amount :-").setBold()));
			table.addCell(new Cell().add(new Paragraph("Rs " + bill.getGrandTotal()).setBold()));

			document.add(table);
			
			
			 // Add Payment Status
            document.add(new Paragraph("Payment Status: " + (bill.isPaymentStatus() ? "Paid" : "Pending"))
                    .setFontSize(12).setBold().setTextAlignment(TextAlignment.LEFT));

            if(bill.isPaymentStatus()) {
            	// Add Thank You Message
    			document.add(new Paragraph("Thankyou for your purchase!").setFontSize(14).setBold()
    					.setTextAlignment(TextAlignment.CENTER));

    			// Add Visit Again Message
    			document.add(
    					new Paragraph("Visit Again!").setFontSize(12).setItalic().setTextAlignment(TextAlignment.CENTER));
    			
    			document.close();
            }else {
            	document.close();
            }

//			// Add Thank You Message
//			document.add(new Paragraph("Thankyou for your purchase!").setFontSize(14).setBold()
//					.setTextAlignment(TextAlignment.CENTER));
//
//			// Add Visit Again Message
//			document.add(
//					new Paragraph("Visit Again!").setFontSize(12).setItalic().setTextAlignment(TextAlignment.CENTER));
//			
			

			// Save Pdf Location
			String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";

			// Ensure the Downloads directory exists; create if it doesn't
			File downloadsDir = new File(downloadsPath);
			if (!downloadsDir.exists()) {
				downloadsDir.mkdirs(); // Create directories if they don't exist
			}

			// Construct the full path for invoice.pdf
			String pdfFilePath = downloadsPath + File.separator + "invoice.pdf";

			// Save the PDF file to the disk
			try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
				fos.write(baos.toByteArray());
				System.out.println("PDF saved successfully to: " + pdfFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	
	}
	
	 @Transactional(readOnly = true)
	    public byte[] generatePdfForBillId(Long billId) throws IOException {
	        Bill bill = billRepository.findById(billId)
	                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + billId));

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	        try (PdfWriter writer = new PdfWriter(baos);
	             PdfDocument pdf = new PdfDocument(writer);
	             Document document = new Document(pdf, PageSize.A4)) {

	            // Add Title
	            Color skyBlue = new DeviceRgb(135, 206, 235);
	            document.add(new Paragraph("Inventory Management System")
	                    .setFontSize(24)
	                    .setBold()
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setFontColor(skyBlue)
	                    .setMarginBottom(20)); // Add some margin below the title

	            // Add Invoice Title
	            document.add(new Paragraph("Invoice").setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));

	            // Add Bill Details
	            document.add(new Paragraph("Bill ID: " + bill.getId()).setFontSize(12).setBold()
	                    .setTextAlignment(TextAlignment.LEFT));

	            String generatedAt = bill.getGeneratedAt().formatted(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
	            document.add(new Paragraph("Generated At: " + generatedAt).setFontSize(12)
	                    .setTextAlignment(TextAlignment.LEFT));

	            // Add Table with Bill Details
	            float[] columnWidths = {1, 2, 1, 1, 1};
	            Table table = new Table(columnWidths);
	            table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

	            table.addHeaderCell(new Paragraph("S.No").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Product Name").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Price").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Quantity").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Subtotal").setBackgroundColor(ColorConstants.LIGHT_GRAY));

	            List<ProductBill> productBills = bill.getProducts();
	            for (int i = 0; i < productBills.size(); i++) {
	                ProductBill productBill = productBills.get(i);
	                table.addCell(String.valueOf(i + 1));
	                table.addCell(productBill.getProductName());
	                table.addCell(String.valueOf(productBill.getPrice()));
	                table.addCell(String.valueOf(productBill.getQuantity()));
	                table.addCell(String.valueOf(productBill.getSubtotal()));
	            }

	            document.add(table);

	            // Add Total Amount
	            document.add(new Paragraph("Total Amount: Rs " + bill.getGrandTotal()).setBold());

	            // Add Payment Status with Color
	            Color paymentColor = bill.isPaymentStatus() ? ColorConstants.GREEN : ColorConstants.RED;
	            String paymentText = bill.isPaymentStatus() ? "Paid" : "Pending";
	            document.add(new Paragraph("Payment Status: " + paymentText)
	                    .setFontSize(12).setBold().setTextAlignment(TextAlignment.LEFT).setFontColor(paymentColor));

	            // Add Page Break
	            document.add(new Paragraph().add("\n"));
	        }

	        return baos.toByteArray();
	    }
	 
	 
	 @Transactional
	    public byte[] generatePdfFor_BillId(Long billId) throws IOException {
	        Bill bill = billRepository.findById(billId)
	                .orElseThrow(() -> new BillNotFoundException("Bill not found with id: " + billId));

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	        try (PdfWriter writer = new PdfWriter(baos);
	             PdfDocument pdf = new PdfDocument(writer);
	             Document document = new Document(pdf, PageSize.DEFAULT)) {

	            // Add Title
	            Color skyBlue = new DeviceRgb(135, 206, 235);
	            document.add(new Paragraph("Inventory Management System")
	                    .setFontSize(24)
	                    .setBold()
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setFontColor(skyBlue)
	                    .setMarginBottom(20)); // Add some margin below the title

	            // Add Invoice Title
	            document.add(new Paragraph("Invoice").setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));

	            // Add Bill Details
	            document.add(new Paragraph("Bill ID: " + bill.getId()).setFontSize(12).setBold()
	                    .setTextAlignment(TextAlignment.LEFT));

	            String generatedAt = bill.getGeneratedAt().formatted(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
	            document.add(new Paragraph("Generated At: " + generatedAt).setFontSize(12)
	                    .setTextAlignment(TextAlignment.LEFT));

	            // Add Table with Bill Details
	            float[] columnWidths = {1, 2, 1, 1, 1};
	            Table table = new Table(columnWidths);
	            table.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

	            table.addHeaderCell(new Paragraph("S.No").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Product Name").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Price").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Quantity").setBackgroundColor(ColorConstants.LIGHT_GRAY));
	            table.addHeaderCell(new Paragraph("Subtotal").setBackgroundColor(ColorConstants.LIGHT_GRAY));

	            List<ProductBill> productBills = bill.getProducts();
	            for (int i = 0; i < productBills.size(); i++) {
	                ProductBill productBill = productBills.get(i);
	                table.addCell(String.valueOf(i + 1));
	                table.addCell(productBill.getProductName());
	                table.addCell(String.valueOf(productBill.getPrice()));
	                table.addCell(String.valueOf(productBill.getQuantity()));
	                table.addCell(String.valueOf(productBill.getSubtotal()));
	            }

	            document.add(table);

	            // Add Total Amount
	            document.add(new Paragraph("Total Amount: Rs " + bill.getGrandTotal()).setBold().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20));

	            // Add Current Date on the right side
	            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	            document.add(new Paragraph("Date: " + currentDate).setFontSize(12)
	            		.setTextAlignment(TextAlignment.LEFT).setMarginTop(20));
	            
	            
	            // Add Payment Status with Color
	            Color paymentColor = bill.isPaymentStatus() ? ColorConstants.GREEN : ColorConstants.RED;
	            String paymentText = bill.isPaymentStatus() ? "Paid" : "Pending";

	            Paragraph paymentStatusParagraph = new Paragraph("Payment Status: ");
	            Text paymentStatusText = new Text(paymentText).setFontColor(paymentColor).setBold();
	            paymentStatusParagraph.add(paymentStatusText);
	            document.add(paymentStatusParagraph);


//	                String path = "classpath:static/Images/Text_Signature.png";

	            // Add Signature Image
//	            String signatureImagePath = path; // Replace with your actual path
//	            
//	            Image signatureImage = new Image(ImageDataFactory.create(signatureImagePath));
//	            signatureImage.setWidth(UnitValue.createPercentValue(30)); // Adjust the width of the image as needed
//	            document.add(signatureImage.setRelativePosition(5,0,10,0).setMarginTop(1));
	        
	            // Add Thank You Message
	            document.add(new Paragraph("Thank you for using RITPL SHOP").setFontSize(14).setBold()
	            		.setTextAlignment(TextAlignment.CENTER).setMarginTop(20));
	            
	            // Add Visit Again Message
	            document.add(new Paragraph("Visit Again!!").setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER));
	        }

	        return baos.toByteArray();
	    }
	
}
