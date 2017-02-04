package se.bjurr.prnfb.settings;

import com.google.common.base.Optional;

public interface Restricted {

  Optional<String> getRepositorySlug();

  Optional<String> getProjectKey();
}
