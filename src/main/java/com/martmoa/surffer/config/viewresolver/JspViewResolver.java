package com.martmoa.surffer.config.viewresolver;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Component
public class JspViewResolver extends InternalResourceViewResolver {

    /**
     * Use '/WEB-INF/views/' for the prefix and '.jsp' for the suffix.
     */
    public JspViewResolver() {
        super();
        setPrefix("/WEB-INF/views/");
        setSuffix(".jsp");
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        if (viewName.isEmpty() || viewName.endsWith("/")) {
            viewName += "index";
        }
        return super.buildView(viewName);
    }




}