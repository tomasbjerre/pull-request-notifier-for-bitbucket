package se.bjurr.prnfb.listener;

import static com.atlassian.bitbucket.pull.PullRequestAction.OPENED;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.listener.PrnfbPullRequestEventListener.setInvoker;
import static se.bjurr.prnfb.settings.PrnfbNotificationBuilder.prnfbNotificationBuilder;
import static se.bjurr.prnfb.settings.PrnfbSettingsDataBuilder.prnfbSettingsDataBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.bjurr.prnfb.http.Invoker;
import se.bjurr.prnfb.http.UrlInvoker;
import se.bjurr.prnfb.service.PrnfbRenderer;
import se.bjurr.prnfb.service.PrnfbRendererFactory;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;
import se.bjurr.prnfb.settings.ValidationException;

import com.atlassian.bitbucket.event.pull.PullRequestEvent;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.google.common.base.Function;

public class PrnfbPullRequestEventListenerTest {

 @Mock
 private PrnfbRendererFactory prnfbRendererFactory;
 @Mock
 private PullRequestService pullRequestService;
 private final ExecutorService executorService = new FakeExecutorService();
 @Mock
 private SettingsService settingsService;
 private PrnfbPullRequestEventListener sut;
 private final List<UrlInvoker> invokedUrls = newArrayList();
 @Mock
 private PullRequestEvent pullRequestOpenedEvent;
 @Mock
 private PullRequest pullRequest;
 private PrnfbSettingsData pluginSettingsData;
 private PrnfbNotification notification1;
 private PrnfbNotification notification2;
 @Mock
 private PrnfbRenderer renderer;
 @Mock
 private PullRequestRef fromRef;
 @Mock
 private PullRequestRef toRef;

 @Before
 public void before() throws ValidationException {
  initMocks(this);
  sut = new PrnfbPullRequestEventListener(prnfbRendererFactory, pullRequestService, executorService, settingsService);
  setInvoker(new Invoker() {
   @Override
   public void invoke(UrlInvoker urlInvoker) {
    invokedUrls.add(urlInvoker);
   }
  });

  when(pullRequest.getFromRef())//
    .thenReturn(fromRef);
  when(pullRequest.getFromRef().getLatestCommit())//
    .thenReturn("latestCFrom");
  when(pullRequest.getFromRef().getId())//
    .thenReturn("IFrom");

  when(pullRequest.getToRef())//
    .thenReturn(toRef);
  when(pullRequest.getToRef().getLatestCommit())//
    .thenReturn("latestCTo");
  when(pullRequest.getToRef().getId())//
    .thenReturn("ITo");

  when(pullRequestOpenedEvent.getPullRequest())//
    .thenReturn(pullRequest);
  when(pullRequestOpenedEvent.getPullRequest().isClosed())//
    .thenReturn(false);
  when(pullRequestOpenedEvent.getAction())//
    .thenReturn(OPENED);

  pluginSettingsData = prnfbSettingsDataBuilder()//
    .build();
  when(settingsService.getPrnfbSettingsData())//
    .thenReturn(pluginSettingsData);

  notification1 = prnfbNotificationBuilder()//
    .withUrl("http://not1.com/")//
    .withTrigger(PrnfbPullRequestAction.OPENED)//
    .build();
  notification2 = prnfbNotificationBuilder(notification1)//
    .withUrl("http://not2.com/")//
    .build();
  List<PrnfbNotification> notifications = newArrayList(notification1, notification2);
  when(settingsService.getNotifications())//
    .thenReturn(notifications);

  when(prnfbRendererFactory.create(any(), any(), any(), any()))//
    .thenReturn(renderer);
  when(renderer.render(any(), any(), any(), any()))//
    .thenAnswer(new Answer<String>() {
     @Override
     public String answer(InvocationOnMock invocation) throws Throwable {
      return (String) invocation.getArguments()[0];
     }
    });
 }

 @Test
 public void testThatPullRequestOpenedCanTriggerNotification() {

  sut.handleEventAsync(pullRequestOpenedEvent);

  assertInvokedUrls("http://not1.com/", "http://not2.com/");
 }

 private void assertInvokedUrls(String... expectedUrls) {
  Iterable<String> urls = transform(invokedUrls, new Function<UrlInvoker, String>() {
   @Override
   public String apply(UrlInvoker input) {
    return input.getUrlParam();
   }
  });
  assertThat(urls)//
    .containsOnly(expectedUrls);
 }

}
