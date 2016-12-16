package eu.rd9.webencode.page;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import eu.rd9.webencode.config.Config;
import eu.rd9.webencode.workers.WorkerManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by renne on 16.12.2016.
 */
@WebServlet(urlPatterns = "/*", name = "WebEncodeUIServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = WebEncodeUI.class, productionMode = false)
public class WebEncodeUIServlet extends VaadinServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        Config config = Config.getInstance();

        WorkerManager workerManager = new WorkerManager();
        workerManager.start();
    }
}
