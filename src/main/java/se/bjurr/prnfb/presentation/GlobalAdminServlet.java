package se.bjurr.prnfb.presentation;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Maps.newHashMap;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.bjurr.prnfb.service.UserCheckService;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

public class GlobalAdminServlet extends HttpServlet {
 private static final long serialVersionUID = 3846987953228399693L;
 private final LoginUriProvider loginUriProvider;
 private final TemplateRenderer renderer;
 private final RepositoryService repositoryService;
 private final UserCheckService userCheckService;
 private final UserManager userManager;

 public GlobalAdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer,
   RepositoryService repositoryService, UserCheckService userCheckService) {
  this.userManager = userManager;
  this.loginUriProvider = loginUriProvider;
  this.renderer = renderer;
  this.repositoryService = repositoryService;
  this.userCheckService = userCheckService;
 }

 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response) {
  try {
   UserProfile user = this.userManager.getRemoteUser(request);
   if (user == null) {
    response.sendRedirect(this.loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    return;
   }

   final Optional<Repository> repository = getRepository(request.getPathInfo());
   boolean isSystemAdmin = this.userCheckService.isSystemAdmin(user.getUserKey());
   String projectKey = null;
   String repositorySlug = null;
   if (repository.isPresent()) {
    projectKey = repository.get().getProject().getKey();
    repositorySlug = repository.get().getSlug();
   }
   boolean isAdmin = this.userCheckService.isAdmin(user.getUserKey(), projectKey, repositorySlug);

   Map<String, Object> context = newHashMap();
   if (repository.isPresent()) {
    context = of( //
      "repository", repository.orNull(), //
      "isAdmin", isAdmin, //
      "isSystemAdmin", isSystemAdmin);
   } else {
    context = of( //
      "isAdmin", isAdmin, //
      "isSystemAdmin", isSystemAdmin);
   }

   response.setContentType("text/html;charset=UTF-8");
   this.renderer.render( //
     "admin.vm", //
     context, //
     response.getWriter());
  } catch (Exception e) {
   propagate(e);
  }
 }

 private URI getUri(HttpServletRequest request) {
  StringBuffer builder = request.getRequestURL();
  if (request.getQueryString() != null) {
   builder.append("?");
   builder.append(request.getQueryString());
  }
  return URI.create(builder.toString());
 }

 @VisibleForTesting
 Optional<Repository> getRepository(String pathInfo) {
  if (pathInfo == null || !pathInfo.contains("/") || pathInfo.endsWith("prnfb/admin")
    || pathInfo.endsWith("prnfb/admin/")) {
   return absent();
  }
  String[] components = pathInfo.split("/");
  if (components.length == 0) {
   return absent();
  }
  String project = components[components.length - 2];
  String repoSlug = components[components.length - 1];
  final Repository repository = checkNotNull(this.repositoryService.getBySlug(project, repoSlug), //
    "Did not find " + project + " " + repoSlug);
  return Optional.of(repository);
 }
}