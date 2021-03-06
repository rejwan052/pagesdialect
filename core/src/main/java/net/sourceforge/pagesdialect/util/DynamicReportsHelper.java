package net.sourceforge.pagesdialect.util;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * Assist in generating dynamic reports Excel and PDF.
 */
public class DynamicReportsHelper {

    String reportType;
    HttpServletResponse response;
    String title;
    String filename;

    /**
     * @param reportType "excel" or "pdf".
     * @param title report title or null.
     * @param filename export filename.
     * @param response Servlet response to write to.
     */
    public DynamicReportsHelper(String reportType, String title, String filename, HttpServletResponse response) {
        this.reportType = reportType;
        this.response = response;
        this.title = title;
        this.filename = filename;
    }

    /**
     * Trigger the exportation.
     * @param items list of beans to export.
     * @param columns columns to export.
     */
    public void export(List items, ColumnBuilder... columns) {
        try {
            StyleBuilder titleStyle = stl.style().setFontSize(10).bold();
            StyleBuilder cellStyle = stl.style().setBorder(stl.penThin()).setFontSize(8).setPadding(1);
            StyleBuilder headerStyle = stl.style().setBorder(stl.penThin()).setFontSize(8).bold().setPadding(1);
            JasperReportBuilder report = report().setColumnStyle(cellStyle).setColumnTitleStyle(headerStyle).columns(columns).setDataSource(new JRBeanCollectionDataSource(items));
            if (title != null) {
                report = report.title(cmp.text(title).setStyle(titleStyle));
            }
            String file = filename + "_" + PagesDialectUtil.now();
            if ("excel".equals(reportType)) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".xls\"");
                response.setContentType("application/vnd.ms-excel");
                JasperXlsExporterBuilder xlsExporter = DynamicReports.export.xlsExporter(response.getOutputStream()).setDetectCellType(true).setIgnorePageMargins(true).setWhitePageBackground(false).setRemoveEmptySpaceBetweenColumns(true);
                report.setIgnorePagination(true);
                report.toXls(xlsExporter);
            } else if ("pdf".equals(reportType)) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
                response.setContentType("application/pdf");
                report.toPdf(response.getOutputStream());
            } else {
                throw new IllegalArgumentException("Report type not valid");
            }
        } catch (DRException ex) {
            throw new TemplateProcessingException("There was an error generating report", ex);
        } catch (IOException ex) {
            throw new TemplateProcessingException("There was an error generating report", ex);
        }
    }
}
