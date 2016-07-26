package se.bjurr.prnfb.presentation;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;
import static se.bjurr.prnfb.test.Podam.populatedInstanceOf;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.service.ButtonsService;
import se.bjurr.prnfb.service.SettingsService;
import se.bjurr.prnfb.service.UserCheckService;
import se.bjurr.prnfb.settings.PrnfbButton;

public class ButtonServletTest {
 private PrnfbButton button1;
 private PrnfbButton button2;
 private ButtonDTO buttonDto1;
 private ButtonDTO buttonDto2;
 @Mock
 private ButtonsService buttonsService;
 @Mock
 private SettingsService settingsService;
 private ButtonServlet sut;
 @Mock
 private UserCheckService userCheckService;

 @Before
 public void before() throws Exception {
  initMocks(this);
  when(this.userCheckService.isViewAllowed())//
    .thenReturn(true);
  when(this.userCheckService.isAdminAllowed(anyString(), anyString()))//
    .thenReturn(true);
  this.sut = new ButtonServlet(this.buttonsService, this.settingsService, this.userCheckService);

  this.buttonDto1 = populatedInstanceOf(ButtonDTO.class);
  this.button1 = toPrnfbButton(this.buttonDto1);
  this.buttonDto2 = populatedInstanceOf(ButtonDTO.class);
  this.button2 = toPrnfbButton(this.buttonDto2);
 }

 @Test
 public void testThatButtonCanBeCreated() throws Exception {
  ButtonDTO button = createButton();
  PrnfbButton prnfbButton = createPrnfbButton(button);
  when(this.settingsService.addOrUpdateButton(prnfbButton))//
    .thenReturn(prnfbButton);

  this.sut.create(button);

  verify(this.settingsService)//
    .addOrUpdateButton(prnfbButton);
 }

 @Test
 public void testThatButtonCanBeDeleted() throws Exception {
  when(this.settingsService.getButton(this.button1.getUuid()))//
    .thenReturn(this.button1);

  this.sut.delete(this.button1.getUuid());

  verify(this.settingsService)//
    .deleteButton(this.button1.getUuid());
 }

 @Test
 public void testThatButtonCanBeListed() {
  when(this.settingsService.getButtons())//
    .thenReturn(newArrayList(this.button1, this.button2));
  allowAll();

  Response actual = this.sut.get();
  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();
  assertThat(actualList)//
    .containsOnly(this.buttonDto1, this.buttonDto2);
 }

 @Test
 public void testThatButtonCanBeListedForAPr() throws Exception {
  Integer repositoryId = 2;
  Long pullRequestId = 3L;
  when(this.buttonsService.getButtons(repositoryId, pullRequestId))//
    .thenReturn(newArrayList(this.button1, this.button2));
  allowAll();

  Response actual = this.sut.get(repositoryId, pullRequestId);

  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();
  assertThat(actualList)//
    .containsOnly(this.buttonDto1, this.buttonDto2);
 }

 @Test
 public void testThatButtonCanBeListedPerProject() throws Exception {
  when(this.settingsService.getButtons(this.buttonDto1.getProjectKey().orNull()))//
    .thenReturn(newArrayList(this.button1));

  Response actual = this.sut.get(this.buttonDto1.getProjectKey().orNull());
  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();

  assertThat(actualList)//
    .containsOnly(this.buttonDto1);
 }

 @Test
 public void testThatButtonCanBeListedPerProjectAndRepo() throws Exception {
  when(
    this.settingsService.getButtons(this.buttonDto1.getProjectKey().orNull(), this.buttonDto1.getRepositorySlug()
      .orNull()))//
    .thenReturn(newArrayList(this.button1));

  Response actual = this.sut
    .get(this.buttonDto1.getProjectKey().orNull(), this.buttonDto1.getRepositorySlug().orNull());
  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();

  assertThat(actualList)//
    .containsOnly(this.buttonDto1);
 }

 @Test
 public void testThatButtonCanBePressed() throws Exception {
  Integer repositoryId = 1;
  Long pullRequestId = 2L;
  UUID buttonUuid = UUID.randomUUID();
  PrnfbButton button = createPrnfbButton(createButton());
  when(this.settingsService.getButton(buttonUuid))//
    .thenReturn(button);
  when(this.userCheckService.isAllowedUseButton(button))//
    .thenReturn(true);
  this.sut.press(repositoryId, pullRequestId, buttonUuid);

  verify(this.buttonsService)//
    .handlePressed(repositoryId, pullRequestId, buttonUuid);
 }

 @Test
 public void testThatButtonCanBeUpdated() throws Exception {
  ButtonDTO button = createButton();
  PrnfbButton prnfbButton = createPrnfbButton(button);
  when(this.settingsService.addOrUpdateButton(prnfbButton))//
    .thenReturn(prnfbButton);

  this.sut.create(button);

  verify(this.settingsService)//
    .addOrUpdateButton(prnfbButton);
 }

 private void allowAll() {
  when(this.userCheckService.filterAllowed(anyListOf(PrnfbButton.class)))//
    .thenAnswer(new Answer<List<PrnfbButton>>() {
     @SuppressWarnings("unchecked")
     @Override
     public List<PrnfbButton> answer(InvocationOnMock invocation) throws Throwable {
      return (List<PrnfbButton>) invocation.getArguments()[0];
     }
    });
 }

 private ButtonDTO createButton() {
  ButtonDTO button = new ButtonDTO();
  button.setName("title");
  button.setUserLevel(EVERYONE);
  button.setUuid(UUID.randomUUID());
  button.setConfirmation("off");
  button.setProjectKey("p1");
  button.setRepositorySlug("r1");
  return button;
 }

 private PrnfbButton createPrnfbButton(ButtonDTO button) {
  PrnfbButton prnfbButton = new PrnfbButton(button.getUUID(), button.getName(), button.getUserLevel(), button.getConfirmation(), "p1", "r1");
  return prnfbButton;
 }

}
