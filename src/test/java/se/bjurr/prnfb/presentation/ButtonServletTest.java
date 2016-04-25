package se.bjurr.prnfb.presentation;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static se.bjurr.prnfb.settings.USER_LEVEL.EVERYONE;
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
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class ButtonServletTest {
 private ButtonServlet sut;
 @Mock
 private ButtonsService buttonsService;
 @Mock
 private SettingsService settingsService;
 @Mock
 private UserCheckService userCheckService;
 private ButtonDTO buttonDto1;
 private ButtonDTO buttonDto2;
 private PrnfbButton button1;
 private PrnfbButton button2;

 @Before
 public void before() throws Exception {
  initMocks(this);
  when(userCheckService.isViewAllowed())//
    .thenReturn(true);
  when(userCheckService.isAdminAllowed())//
    .thenReturn(true);
  sut = new ButtonServlet(buttonsService, settingsService, userCheckService);

  buttonDto1 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  button1 = toPrnfbButton(buttonDto1);
  buttonDto2 = new PodamFactoryImpl().manufacturePojo(ButtonDTO.class);
  button2 = toPrnfbButton(buttonDto2);
 }

 @Test
 public void testThatButtonCanBeCreated() throws Exception {
  ButtonDTO button = createButton();

  sut.create(button);

  PrnfbButton prnfbButton = createPrnfbButton(button);
  verify(settingsService)//
    .addOrUpdateButton(prnfbButton);
 }

 @Test
 public void testThatButtonCanBeListed() {
  when(settingsService.getButtons())//
    .thenReturn(newArrayList(button1, button2));
  allowAll();

  Response actual = sut.get();
  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();
  assertThat(actualList)//
    .containsExactly(buttonDto1, buttonDto2);
 }

 @Test
 public void testThatButtonCanBeListedForAPr() throws Exception {
  Integer repositoryId = 2;
  Long pullRequestId = 3L;
  when(buttonsService.getButtons(repositoryId, pullRequestId))//
    .thenReturn(newArrayList(button1, button2));
  allowAll();

  Response actual = sut.get(repositoryId, pullRequestId);

  @SuppressWarnings("unchecked")
  Iterable<ButtonDTO> actualList = (Iterable<ButtonDTO>) actual.getEntity();
  assertThat(actualList)//
    .containsExactly(buttonDto1, buttonDto2);
 }

 @Test
 public void testThatButtonCanBeUpdated() throws Exception {
  ButtonDTO button = createButton();

  sut.update(button);

  PrnfbButton prnfbButton = createPrnfbButton(button);
  verify(settingsService)//
    .addOrUpdateButton(prnfbButton);
 }

 @Test
 public void testThatButtonCanBeDeleted() throws Exception {
  ButtonDTO button = createButton();

  sut.delete(button.getUUID());

  verify(settingsService)//
    .deleteButton(button.getUUID());
 }

 @Test
 public void testThatButtonCanBePressed() throws Exception {
  Integer repositoryId = 1;
  Long pullRequestId = 2L;
  UUID buttonUuid = UUID.randomUUID();
  PrnfbButton button = createPrnfbButton(createButton());
  when(settingsService.getButton(buttonUuid))//
    .thenReturn(button);
  when(userCheckService.isAllowedUseButton(button))//
    .thenReturn(true);
  sut.press(repositoryId, pullRequestId, buttonUuid);

  verify(buttonsService)//
    .handlePressed(repositoryId, pullRequestId, buttonUuid);
 }

 private ButtonDTO createButton() {
  ButtonDTO button = new ButtonDTO();
  button.setTitle("title");
  button.setUserLevel(EVERYONE);
  button.setUuid(UUID.randomUUID());
  return button;
 }

 private PrnfbButton createPrnfbButton(ButtonDTO button) {
  PrnfbButton prnfbButton = new PrnfbButton(button.getUUID(), button.getTitle(), button.getUserLevel());
  return prnfbButton;
 }

 private void allowAll() {
  when(userCheckService.filterAllowed(anyListOf(PrnfbButton.class)))//
    .thenAnswer(new Answer<List<PrnfbButton>>() {
     @SuppressWarnings("unchecked")
     @Override
     public List<PrnfbButton> answer(InvocationOnMock invocation) throws Throwable {
      return (List<PrnfbButton>) invocation.getArguments()[0];
     }
    });
 }

}
