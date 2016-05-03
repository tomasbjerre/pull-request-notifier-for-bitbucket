package se.bjurr.prnfb.service;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class PrnfbVariableTest {

 @Test
 public void testThatAdminAndReadmeContainsVariables() throws IOException, URISyntaxException {
  URL adminResource = Resources.getResource("admin.vm");
  String adminPageContent = Resources.toString(adminResource, UTF_8);
  File readme = findReadme(new File(adminResource.toURI()));
  String readmeContent = Files.toString(readme, UTF_8);
  for (PrnfbVariable variable : PrnfbVariable.values()) {
   assertThat(adminPageContent)//
     .as("admin.vm should include " + variable.name() + "\nWas:" + adminPageContent)//
     .contains(variable.name());
   assertThat(readmeContent)//
     .as("README.md should include " + variable.name() + "\nWas:" + readmeContent)//
     .contains(variable.name());
  }
 }

 private File findReadme(File file) {
  File candidate = new File(file.getAbsolutePath() + "/README.md");
  if (candidate.exists()) {
   return candidate;
  }
  return findReadme(file.getParentFile());
 }

}
