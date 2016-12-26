package se.bjurr.prnfb.service;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.service.PrnfbRenderer.ENCODE_FOR;

/**
 * It may be expensive to create a new {@link PrnfbRenderer} in the service for every string that
 * should be rendered. If the service instead returns an instance of this class, then it will help
 * performance.
 */
public class PrnfbRendererWrapper {

  private final ClientKeyStore clientKeyStore;
  private final PrnfbRenderer renderer;
  private final Boolean shouldAcceptAnyCertificate;

  public PrnfbRendererWrapper(
      PrnfbRenderer renderer, ClientKeyStore clientKeyStore, Boolean shouldAcceptAnyCertificate) {
    this.renderer = renderer;
    this.clientKeyStore = clientKeyStore;
    this.shouldAcceptAnyCertificate = shouldAcceptAnyCertificate;
  }

  public String render(String inputString, ENCODE_FOR encodeFor) {
    return renderer.render(inputString, encodeFor, clientKeyStore, shouldAcceptAnyCertificate);
  }
}
