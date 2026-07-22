package com.stea.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.stea.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ArticleService articleService;
    private final MouvementStockService mouvementStockService;
    private final CommandeService commandeService;
    private final LivraisonService livraisonService;

    // ======================== EXCEL EXPORTS ========================

    public byte[] exportArticlesExcel() throws IOException {
        List<ArticleResponse> data = articleService.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Articles");

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);

            String[] headers = {"#", "Reference", "Designation", "Categorie", "Type", "Prix Unitaire", "Stock", "Seuil", "Service"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (ArticleResponse a : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(a.getReference() != null ? a.getReference() : "");
                row.createCell(2).setCellValue(a.getDesignation());
                row.createCell(3).setCellValue(a.getCategorie() != null ? a.getCategorie() : "");
                row.createCell(4).setCellValue(a.getType() != null ? a.getType() : "");
                row.createCell(5).setCellValue(a.getPrixUnitaire() != null ? a.getPrixUnitaire().doubleValue() : 0);
                row.createCell(6).setCellValue(a.getQuantiteStock() != null ? a.getQuantiteStock() : 0);
                row.createCell(7).setCellValue(a.getSeuilAlerte() != null ? a.getSeuilAlerte() : 0);
                row.createCell(8).setCellValue(a.getServiceNom() != null ? a.getServiceNom() : "");
            }

            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(5, 4500);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    public byte[] exportMouvementsExcel() throws IOException {
        List<MouvementStockResponse> data = mouvementStockService.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Mouvements de Stock");

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);

            String[] headers = {"#", "Date", "Type", "Ref. Article", "Designation", "Quantite", "Source", "Destination", "Statut"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (MouvementStockResponse m : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getId());
                row.createCell(1).setCellValue(m.getDateMouvement() != null ? m.getDateMouvement().toString() : "");
                row.createCell(2).setCellValue(m.getType() != null ? m.getType() : "");
                row.createCell(3).setCellValue(m.getArticleReference() != null ? m.getArticleReference() : "");
                row.createCell(4).setCellValue(m.getArticleDesignation() != null ? m.getArticleDesignation() : "");
                row.createCell(5).setCellValue(m.getQuantite() != null ? m.getQuantite() : 0);
                row.createCell(6).setCellValue(m.getEmplacementSource() != null ? m.getEmplacementSource() : "");
                row.createCell(7).setCellValue(m.getEmplacementDestination() != null ? m.getEmplacementDestination() : "");
                row.createCell(8).setCellValue(m.getStatut() != null ? m.getStatut() : "");
            }

            sheet.setColumnWidth(4, 6000);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    public byte[] exportCommandesExcel() throws IOException {
        List<CommandeResponse> data = commandeService.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Commandes");

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);

            String[] headers = {"#", "Numero", "Client", "Type Flux", "Total", "Statut", "Paiement", "Fournisseur"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (CommandeResponse c : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getNumero() != null ? c.getNumero() : "");
                row.createCell(2).setCellValue(c.getClient() != null ? c.getClient() : "");
                row.createCell(3).setCellValue(c.getTypeFlux() != null ? c.getTypeFlux() : "");
                row.createCell(4).setCellValue(c.getTotal() != null ? c.getTotal().doubleValue() : 0);
                row.createCell(5).setCellValue(c.getStatut() != null ? c.getStatut() : "");
                row.createCell(6).setCellValue(c.getStatutPaiement() != null ? c.getStatutPaiement() : "");
                row.createCell(7).setCellValue(c.getFournisseurNom() != null ? c.getFournisseurNom() : "");
            }

            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(4, 4000);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    public byte[] exportLivraisonsExcel() throws IOException {
        List<LivraisonResponse> data = livraisonService.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Livraisons");

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);

            String[] headers = {"#", "N Bon", "Date Prevue", "Destinataire", "Contenu", "Vehicule", "N Colis", "Statut", "Commande"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (LivraisonResponse l : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(l.getId());
                row.createCell(1).setCellValue(l.getNumeroBon() != null ? l.getNumeroBon() : "");
                row.createCell(2).setCellValue(l.getDatePrevue() != null ? l.getDatePrevue().toString() : "");
                row.createCell(3).setCellValue(l.getDestinataire() != null ? l.getDestinataire() : "");
                row.createCell(4).setCellValue(l.getContenu() != null ? l.getContenu() : "");
                row.createCell(5).setCellValue(l.getVehicule() != null ? l.getVehicule() : "");
                row.createCell(6).setCellValue(l.getNombreColis() != null ? l.getNombreColis() : 0);
                row.createCell(7).setCellValue(l.getStatut() != null ? l.getStatut() : "");
                row.createCell(8).setCellValue(l.getCommandeNumero() != null ? l.getCommandeNumero() : "");
            }

            sheet.setColumnWidth(4, 5000);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    // ======================== PDF EXPORTS ========================

    public byte[] exportArticlesPdf() {
        List<ArticleResponse> data = articleService.findAll();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, bos);

        addPdfHeader(document, "LISTE DES ARTICLES", "Articles - STEA");
        document.open();

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{5, 18, 28, 15, 14, 10, 10});

        String[] headers = {"#", "Reference", "Designation", "Categorie", "Prix Unit.", "Stock", "Seuil"};
        for (String h : headers) {
            addHeaderCell(table, h);
        }

        for (ArticleResponse a : data) {
            addTextCell(table, String.valueOf(a.getId()));
            addTextCell(table, a.getReference() != null ? a.getReference() : "-");
            addTextCell(table, a.getDesignation());
            addTextCell(table, a.getCategorie() != null ? a.getCategorie() : "-");
            addTextCell(table, a.getPrixUnitaire() != null ? String.format("%,.0f F CFA", a.getPrixUnitaire()) : "0 F CFA");
            addTextCell(table, String.valueOf(a.getQuantiteStock() != null ? a.getQuantiteStock() : 0));
            addTextCell(table, String.valueOf(a.getSeuilAlerte() != null ? a.getSeuilAlerte() : 0));
        }

        document.add(table);
        addPdfFooter(document, data.size() + " article(s)");
        document.close();
        return bos.toByteArray();
    }

    public byte[] exportMouvementsPdf() {
        List<MouvementStockResponse> data = mouvementStockService.findAll();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, bos);

        addPdfHeader(document, "MOUVEMENTS DE STOCK", "Mouvements de stock - STEA");
        document.open();

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{12, 22, 15, 22, 8, 15, 15});

        String[] headers = {"Date", "Type", "Ref. Art.", "Designation", "Qte", "Source", "Dest."};
        for (String h : headers) {
            addHeaderCell(table, h);
        }

        for (MouvementStockResponse m : data) {
            addTextCell(table, m.getDateMouvement() != null ? m.getDateMouvement().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")) : "");
            addTextCell(table, m.getType() != null ? m.getType() : "");
            addTextCell(table, m.getArticleReference() != null ? m.getArticleReference() : "-");
            addTextCell(table, m.getArticleDesignation() != null ? m.getArticleDesignation() : "-");
            addTextCell(table, String.valueOf(m.getQuantite() != null ? m.getQuantite() : 0));
            addTextCell(table, m.getEmplacementSource() != null ? m.getEmplacementSource() : "-");
            addTextCell(table, m.getEmplacementDestination() != null ? m.getEmplacementDestination() : "-");
        }

        document.add(table);
        addPdfFooter(document, data.size() + " mouvement(s)");
        document.close();
        return bos.toByteArray();
    }

    public byte[] exportCommandesPdf() {
        List<CommandeResponse> data = commandeService.findAll();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, bos);

        addPdfHeader(document, "LISTE DES COMMANDES", "Commandes - STEA");
        document.open();

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{18, 25, 22, 15, 10, 25});

        String[] headers = {"Numero", "Client", "Total", "Statut", "Paiement", "Fournisseur"};
        for (String h : headers) {
            addHeaderCell(table, h);
        }

        for (CommandeResponse c : data) {
            addTextCell(table, c.getNumero() != null ? c.getNumero() : "-");
            addTextCell(table, c.getClient() != null ? c.getClient() : "-");
            addTextCell(table, c.getTotal() != null ? String.format("%,.0f F CFA", c.getTotal()) : "0 F CFA");
            addTextCell(table, c.getStatut() != null ? c.getStatut() : "-");
            addTextCell(table, c.getStatutPaiement() != null ? c.getStatutPaiement().replace("_", " ") : "-");
            addTextCell(table, c.getFournisseurNom() != null ? c.getFournisseurNom() : "-");
        }

        document.add(table);
        addPdfFooter(document, data.size() + " commande(s)");
        document.close();
        return bos.toByteArray();
    }

    public byte[] exportLivraisonsPdf() {
        List<LivraisonResponse> data = livraisonService.findAll();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate(), 20, 20, 20, 20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, bos);

        addPdfHeader(document, "LISTE DES LIVRAISONS", "Livraisons - STEA");
        document.open();

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{18, 22, 22, 15, 8, 25});

        String[] headers = {"N Bon", "Date Prevue", "Destinataire", "Contenu", "Colis", "Commande"};
        for (String h : headers) {
            addHeaderCell(table, h);
        }

        for (LivraisonResponse l : data) {
            addTextCell(table, l.getNumeroBon() != null ? l.getNumeroBon() : "-");
            addTextCell(table, l.getDatePrevue() != null ? l.getDatePrevue().toString() : "-");
            addTextCell(table, l.getDestinataire() != null ? l.getDestinataire() : "-");
            addTextCell(table, l.getContenu() != null ? l.getContenu() : "-");
            addTextCell(table, String.valueOf(l.getNombreColis() != null ? l.getNombreColis() : 0));
            addTextCell(table, l.getCommandeNumero() != null ? l.getCommandeNumero() : "-");
        }

        document.add(table);
        addPdfFooter(document, data.size() + " livraison(s)");
        document.close();
        return bos.toByteArray();
    }

    // ======================== PDF HELPERS ========================

    private void addPdfHeader(com.lowagie.text.Document document, String title, String subtitle) {
        try {
            com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 16, java.awt.Color.DARK_GRAY);
            com.lowagie.text.Font subtitleFont = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 10, java.awt.Color.GRAY);

            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            p.add(new Chunk(title, titleFont));
            p.add(new Chunk("\n" + subtitle, subtitleFont));
            p.setSpacingAfter(5);

            document.add(p);
            document.add(new com.lowagie.text.pdf.draw.LineSeparator());
            document.add(new Chunk("\n"));
        } catch (Exception e) {
            log.warn("Error adding PDF header: {}", e.getMessage());
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 9, java.awt.Color.WHITE)));
        cell.setBackgroundColor(new java.awt.Color(37, 99, 235));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addTextCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 8)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addPdfFooter(com.lowagie.text.Document document, String summary) {
        try {
            document.add(new Chunk("\n"));
            com.lowagie.text.Font footerFont = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 9, java.awt.Color.GRAY);
            Paragraph footer = new Paragraph();
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.add(new Chunk(summary + " | Genere le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), footerFont));
            document.add(footer);
        } catch (Exception e) {
            log.warn("Error adding PDF footer: {}", e.getMessage());
        }
    }
}
