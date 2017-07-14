package se.bjurr.prnfb.service;

import java.util.Set;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.user.EscalatedSecurityContext;
import com.atlassian.bitbucket.util.Operation;

public class MockedEscalatedSecurityContext implements EscalatedSecurityContext {

  @Override
  public void applyToRequest() {}

  @Override
  public <T, E extends Throwable> T call(Operation<T, E> arg0) throws E {
    return arg0.perform();
  }

  @Override
  public EscalatedSecurityContext withPermission(Object arg0, Permission arg1) {
    return null;
  }

  @Override
  public EscalatedSecurityContext withPermission(Permission arg0) {
    return null;
  }

  @Override
  public EscalatedSecurityContext withPermissions(Set<Permission> arg0) {
    return null;
  }
}
