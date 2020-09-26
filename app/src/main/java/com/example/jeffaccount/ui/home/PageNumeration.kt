package com.example.jeffaccount.ui.home

import com.example.jeffaccount.model.Company
import com.itextpdf.text.pdf.*
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfName.FONT
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;


internal class PageNumeration : PdfPageEventHelper() {
    /** The template with the total number of pages.  */
    var total: PdfTemplate? = null
    private var normal: Font? = null
    private var normalSmall: Font? = null
    private val company: Company? = null

    /**
     * Creates the PdfTemplate that will hold the total number of pages.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper.onOpenDocument
     */
    override fun onOpenDocument(writer: PdfWriter, document: Document?) {
        total = writer.directContent.createTemplate(30f, 12f)
    }

    /**
     * Adds a header to every page
     * @see com.itextpdf.text.pdf.PdfPageEventHelper.onEndPage
     */
    override fun onEndPage(writer: PdfWriter, document: Document) {
        val table = PdfPTable(3)
        try {
            table.setWidths(intArrayOf(24, 24, 2))
            table.defaultCell.fixedHeight = 10f
            table.defaultCell.border = Rectangle.TOP
            var cell = PdfPCell()
            cell.border = 0
            cell.borderWidthTop = 1f
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.phrase = Phrase("some text", normalSmall)
            table.addCell(cell)
            cell = PdfPCell()
            cell.border = 0
            cell.borderWidthTop = 1f
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.phrase = Phrase(
                String.format("Page %d of", writer.pageNumber),
                normal
            )
            table.addCell(cell)
            cell = PdfPCell(Image.getInstance(total))
            cell.border = 0
            cell.borderWidthTop = 1f
            table.addCell(cell)
            table.setTotalWidth(
                document.getPageSize().getWidth()
                        - document.leftMargin() - document.rightMargin()
            )
            table.writeSelectedRows(
                0, -1, document.leftMargin(),
                document.bottomMargin() - 15, writer.directContent
            )
        } catch (de: DocumentException) {
            throw ExceptionConverter(de)
        }
    }

    /**
     * Fills out the total number of pages before the document is closed.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper.onCloseDocument
     */
    override fun onCloseDocument(writer: PdfWriter, document: Document?) {
        ColumnText.showTextAligned(
            total, Element.ALIGN_LEFT,
            Phrase((writer.pageNumber - 1).toString(), normal), 2f, 2f, 0f
        )
    }

    init {
//        try {
//            normal = Font(BaseFont.createFont(FONT.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 8)
//            normalSmall =
//                Font(BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 6)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }
}