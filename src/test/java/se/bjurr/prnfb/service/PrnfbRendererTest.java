package se.bjurr.prnfb.service;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.APPROVED;
import static se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR.NONE;
import static se.bjurr.prnfb.service.PrnfbVariable.*;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.SecurityService;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.http.HttpResponse;
import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.ValidationException;

public class PrnfbRendererTest {

  @Mock private ApplicationUser applicationUser;
  @Mock private PullRequestParticipant author;
  private ClientKeyStore clientKeyStore;
  private final ENCODE_FOR encodeFor = NONE;
  @Mock private PullRequestRef fromRef;
  private PrnfbNotification prnfbNotification;
  @Mock private ApplicationPropertiesService propertiesService;
  @Mock private PullRequest pullRequest;
  private PrnfbPullRequestAction pullRequestAction;
  @Mock private RepositoryService repositoryService;
  private SecurityService securityService;
  private final Boolean shouldAcceptAnyCertificate = true;
  private PrnfbRenderer sut;
  @Mock private ApplicationUser user;
  private final Map<PrnfbVariable, Supplier<String>> variables = newHashMap();

  @Before
  public void before() throws ValidationException {
    initMocks(this);
    prnfbNotification =
        prnfbNotificationBuilder() //
            .withUrl("http://hej.com") //
            .withTrigger(APPROVED) //
            .build();
    sut =
        new PrnfbRenderer(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            securityService);

    when(pullRequest.getFromRef()) //
        .thenReturn(fromRef);
    when(pullRequest.getFromRef().getLatestCommit()) //
        .thenReturn("latestCommitHash");

    PrnfbVariable.setInvoker(
        new Invoker() {
          @Override
          public HttpResponse invoke(UrlInvoker toInvoke) {
            HttpResponse response = null;
            try {
              response = new HttpResponse(new URI("http://fake.om/"), 200, "theResponse");
            } catch (final URISyntaxException e) {
              e.printStackTrace();
            }
            toInvoke.setResponse(response);
            return response;
          }
        });
  }

  @Test
  public void testThatEverythingCanBeRendered() throws UnsupportedEncodingException {
    final String actual =
        sut.getRenderedStringResolved(
            "asd ${" + EVERYTHING_URL.name() + "} asd",
            encodeFor,
            sut.regexp(EVERYTHING_URL),
            EVERYTHING_URL.resolve(
                pullRequest,
                pullRequestAction,
                applicationUser,
                repositoryService,
                propertiesService,
                prnfbNotification,
                variables,
                clientKeyStore,
                shouldAcceptAnyCertificate,
                securityService));

    for (final PrnfbVariable v : PrnfbVariable.values()) {
      if (v != EVERYTHING_URL && v != PULL_REQUEST_DESCRIPTION) {
        assertThat(actual) //
            .containsOnlyOnce(v.name() + "=${" + v.name() + "}") //
            .doesNotContain(EVERYTHING_URL.name()) //
            .doesNotContain(PULL_REQUEST_DESCRIPTION.name());
      }
    }

    assertThat(actual) //
        .startsWith("asd ");
  }

  @Test
  public void testThatDollarInStringCanBeRendered() throws UnsupportedEncodingException {
    final String actual =
        sut.getRenderedStringResolved(
            "asd ${" + PULL_REQUEST_TITLE.name() + "} asd",
            encodeFor,
            sut.regexp(PULL_REQUEST_TITLE),
            "BNRSD-387 Fix circular reference logging on $host errors in RSD abstract client");

    assertThat(actual) //
        .isEqualTo(
            "asd BNRSD-387 Fix circular reference logging on $host errors in RSD abstract client asd");
  }

  @Test
  public void testThatIfAVariableChrashesOnResolveItWillBeEmpty() {
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_AUTHOR_EMAIL + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatIfAVariableChrashesResolvedToNullOnResolveItWillBeEmpty() {
    when(pullRequest.getAuthor()) //
        .thenReturn(author);
    when(pullRequest.getAuthor().getUser()) //
        .thenReturn(user);
    when(pullRequest.getAuthor().getUser().getEmailAddress()) //
        .thenReturn(null);
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_AUTHOR_EMAIL + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatInjectionUrlCanBeRendered() throws ValidationException {
    prnfbNotification =
        prnfbNotificationBuilder(prnfbNotification) //
            .withInjectionUrl("http://getValueFrom.com/") //
            .withTrigger(APPROVED) //
            .build();
    sut =
        new PrnfbRenderer(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            securityService);

    final String actual =
        sut.render(
            "my ${" + INJECTION_URL_VALUE + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my theResponse string");
  }

  @Test
  public void testThatVariableRegexCanBeRendered() throws ValidationException {
    when(pullRequest.getFromRef().getDisplayId()) //
        .thenReturn("feature/hello-world");
    prnfbNotification =
        prnfbNotificationBuilder(prnfbNotification)
            .withVariableName("PULL_REQUEST_FROM_BRANCH")
            .withVariableRegex("(?:hello)-(.+)")
            .build();

    sut =
        new PrnfbRenderer(
            pullRequest,
            pullRequestAction,
            applicationUser,
            repositoryService,
            propertiesService,
            prnfbNotification,
            variables,
            securityService);

    final String actual =
        sut.render(
            "my ${" + VARIABLE_REGEX_MATCH + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual).isEqualTo("my world string");
  }

  @Test
  public void testThatMergeCommitCanBeRenderedIfExists() {
    variables.put(PULL_REQUEST_MERGE_COMMIT, Suppliers.ofInstance("mergeHash"));
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_MERGE_COMMIT + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my mergeHash string");
  }

  @Test
  public void testThatMergeCommitCanBeRenderedIfNotExists() {
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_MERGE_COMMIT + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my  string");
  }

  @Test
  public void testThatStringCanBeRendered() {
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_FROM_HASH + "} string",
            encodeFor,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my latestCommitHash string");
  }

  @Test
  public void testThatStringCanBeRenderedForUrl() {
    variables.put(PULL_REQUEST_COMMENT_TEXT, Suppliers.ofInstance("the comment"));
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_COMMENT_TEXT + "} string",
            ENCODE_FOR.URL,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my the+comment string");
  }

  @Test
  public void testThatStringCanBeRenderedForHtml() {
    variables.put(PULL_REQUEST_COMMENT_TEXT, Suppliers.ofInstance("the\ncomment \""));
    final String actual =
        sut.render(
            "my ${" + PULL_REQUEST_COMMENT_TEXT + "} string",
            ENCODE_FOR.HTML,
            clientKeyStore,
            shouldAcceptAnyCertificate);
    assertThat(actual) //
        .isEqualTo("my the<br />comment &quot; string");
  }
}
