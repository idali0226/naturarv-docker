package se.nrm.dina.web.portal.controllers;

import java.io.IOException; 
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author idali
 */
//@Stateless
//@LocalBean
@ViewScoped
@Named
public class IdleMonitorController {

    public void idleListener() throws IOException {
        FacesContext ctx = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.invalidate();

        ctx.getExternalContext().redirect("/");
    }
}
