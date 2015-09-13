package se.bjurr.prnfb.admin;

import static com.google.common.base.Throwables.propagate;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static se.bjurr.prnfb.admin.ConfigResource.isAdminAllowed;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.templaterenderer.TemplateRenderer;

public class AdminServlet extends HttpServlet {
 private static final long serialVersionUID = 3846987953228399693L;
 private final LoginUriProvider loginUriProvider;
 private final TemplateRenderer renderer;
 private final UserManager userManager;
 private final SecurityService securityService;
 private final PluginSettingsFactory pluginSettingsFactory;

 public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer,
   SecurityService securityService, PluginSettingsFactory pluginSettingsFactory) {
  this.userManager = userManager;
  this.loginUriProvider = loginUriProvider;
  this.renderer = renderer;
  this.securityService = securityService;
  this.pluginSettingsFactory = pluginSettingsFactory;
 }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
  UserProfile user = userManager.getRemoteUser(request);
  if (user == null) {
   response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
   return;
  }
  try {
   if (!isAdminAllowed(userManager, request, securityService, pluginSettingsFactory)) {
    response.sendError(SC_FORBIDDEN,
      "You are not allowed to edit configuration " + loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    return;
   }
  } catch (Exception e) {
   propagate(e);
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