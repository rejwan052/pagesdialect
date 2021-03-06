package net.sourceforge.pagesdialect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.dialect.AbstractXHTMLEnabledDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.attr.StandardEachAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor;

/**
 * Custom Thymeleaf dialect with some pagination, sorting and export utilities.
 */
public class PagesDialect extends AbstractXHTMLEnabledDialect {
    
    // Processor precedences for this dialect.
    public static final int SEPARATE_ATTR_PRECEDENCE = StandardEachAttrProcessor.ATTR_PRECEDENCE + 1; // Need to be run after th:each processor
    public static final int PAGINATE_ATTR_PRECEDENCE = StandardEachAttrProcessor.ATTR_PRECEDENCE - 1; // Need to be run before th:each processor
    public static final int EXPORT_ATTR_PRECEDENCE = PAGINATE_ATTR_PRECEDENCE - 1; // Run before pages:paginate processor and after pages:sort processor
    public static final int SORT_ATTR_PRECEDENCE = StandardTextAttrProcessor.ATTR_PRECEDENCE + 1; // Need to be run after th:text processor

    // Default value por page requests parameter
    public static final String PAGE_PARAMETER_DEFAULT = "page";

    // Configuration attributes to override default parameters.
    public static final String PAGE_PARAMETER = "pageParameter";
    public static final String SORT_PARAMETER = "sortParameter";
    public static final String SORT_TYPE_PARAMETER = "sortTypeParameter";
    public static final String EXPORT_DIV_ID = "exportDivId";

    // i18n keys. Can be overriden by configuration.
    public static final String I18N_ONE_RESULT = "pagesdialect.oneResult";
    public static final String I18N_RESULTS = "pagesdialect.results";
    public static final String I18N_PREVIOUS = "pagesdialect.previous";
    public static final String I18N_NEXT = "pagesdialect.next";
    public static final String I18N_PAGE = "pagesdialect.page";
    public static final String I18N_FIRST = "pagesdialect.first";
    public static final String I18N_LAST = "pagesdialect.last";
    public static final String I18N_NONE = "pagesdialect.none";
    public static final String I18N_EXPORT_PDF = "pagesdialect.exportPdf";
    public static final String I18N_EXPORT_EXCEL = "pagesdialect.exportExcel";

    private Map<String, String> properties = new HashMap<String, String>();

    private Set<TypeFormatter> typeFormatters;

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setTypeFormatters(Set<TypeFormatter> typeFormatters) {
        this.typeFormatters = typeFormatters;
    }

    public Set<TypeFormatter> getTypeFormatters() {
        return typeFormatters;
    }

    @Override
    public String getPrefix() {
        return "pages";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        Set<IProcessor> attrProcessors = new HashSet<IProcessor>();
        attrProcessors.add(new PaginateAttrProcessor("paginate", this));
        attrProcessors.add(new PaginatedAttrProcessor("paginated", this));
        attrProcessors.add(new SortLinkAttrProcessor("sortLink", this));
        attrProcessors.add(new SortAttrProcessor("sort", this));
        attrProcessors.add(new ExportAttrProcessor("pdf", this, ExportPerformer.PDF_FORMAT));
        attrProcessors.add(new ExportAttrProcessor("excel", this, ExportPerformer.EXCEL_FORMAT));
        attrProcessors.add(new SeparateAttrProcessor("separate"));
        return attrProcessors;
    }
    
    public String getPageParameter() {
        if (properties.containsKey(PagesDialect.PAGE_PARAMETER)) {
            return properties.get(PagesDialect.PAGE_PARAMETER);
        } else {
            return PAGE_PARAMETER_DEFAULT;
        }
    }
}
