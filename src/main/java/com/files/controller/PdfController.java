package com.files.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.files.services.PdfService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/ims/pdf")
public class PdfController {
	
	@Autowired
	private PdfService pdfService;
		
	 //Read Or Show Pdf By Id
    @GetMapping(value = "/v1/{billId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generateBillPdf(@PathVariable("billId") Long billId,HttpServletResponse response) throws IOException {

        byte[] pdfBytes = pdfService.generatePdfFor_BillId(billId);

        // Set the HTTP headers
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill_" + billId + ".pdf");

        // Write the PDF bytes to the response
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
    
    //Downolad Pdf By Id
    @GetMapping(value = "/v2/{billId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> downloadBillPdf(@PathVariable Long billId) {
        try {
            byte[] pdfBytes = pdfService.generatePdfFor_BillId(billId);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill_" + billId + ".pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).contentLength(pdfBytes.length).body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}
