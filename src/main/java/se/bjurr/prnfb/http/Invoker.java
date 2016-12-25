package se.bjurr.prnfb.http;

public interface Invoker {
  HttpResponse invoke(UrlInvoker urlInvoker);
}
