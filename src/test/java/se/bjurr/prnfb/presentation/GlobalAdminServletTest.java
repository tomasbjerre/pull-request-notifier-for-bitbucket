package se.bjurr.prnfb.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import se.bjurr.prnfb.service.UserCheckService;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

public class GlobalAdminServletTest {

  @Mock private LoginUriProvider loginUriProvider;
  @Mock private TemplateRenderer renderer;
  @Mock private RepositoryService repositoryService;
  private GlobalAdminServlet sut;
  @Mock private UserCheckService userCheckService;
  @Mock private UserManager userManager;

  @Before
  public void before() {
    initMocks(this);
    this.sut =
        new GlobalAdminServlet(
            this.userManager,
            this.loginUriProvider,
            this.renderer,
            this.repositoryService,
            this.userCheckService);
  }

  @Test
  public void testGetRepository() {
    assertThat(this.sut.getRepository(null).orNull()) //
        .isNull();
    assertThat(this.sut.getRepository("").orNull()) //
        .isNull();
    assertThat(this.sut.getRepository("/").orNull()) //
        .isNull();

    Repository repository = mock(Repository.class);
    when(this.repositoryService.getBySlug("p", "r")) //
        .thenReturn(repository);

    assertThat(this.sut.getRepository("p/r").orNull()) //
        .isSameAs(repository);

    assertThat(this.sut.getRepository("some/path/prnfb/admin").orNull()) //
        .isNull();
  }
}
