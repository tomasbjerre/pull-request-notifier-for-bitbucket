package se.bjurr.prnfb.presentation;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;

public class GlobalAdminServlet extends HttpServlet {
 private static final long serialVersionUID = 3846987953228399693L;
 private final LoginUriProvider loginUriProvider;
 private final TemplateRenderer renderer;
 private final UserManager userManager;

 public GlobalAdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
  this.userManager = userManager;
  this.loginUriProvider = loginUriProvider;
  this.renderer = renderer;
 }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
  UserProfile user = userManager.getRemoteUser(request);
  if (user == null) {
   response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
   return;
  }
  response.setContentType("text/html;charset=utf-8");
  renderer.render("admin.vm", response.getWriter());
 }

 private URI getUri(HttpServletRequest request) {
  StringBuffer builder = request.getRequestURL();
  if (request.getQueryString() != null) {
   builder.append("?");
   builder.append(request.getQueryString());
  }
  return URI.create(builder.toString());
 }
}