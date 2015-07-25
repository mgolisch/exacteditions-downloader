package eu.mgolisch.exacteditions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLInputElement;
import com.gargoylesoftware.htmlunit.util.Cookie;
 
public class DownloadHelper {
 
  private  CookieManager cookiemanager = new CookieManager();
  private HttpsURLConnection conn;
  private String username = "";
  private String password = "";
  private final String USER_AGENT = "Mozilla/5.0";
  private final String loginurl = "https://login.exacteditions.com/login";
  private final String browserurl = "https://www.exacteditions.com/browseEditions.do";
  private boolean loggedin = false;
  public DownloadHelper() {
	  cookiemanager = new CookieManager();
	  cookiemanager.setCookiesEnabled(true);
	  Properties props = Utils.loadProperties();
	  username = props.getProperty("username");
	  password = props.getProperty("password");
  }
  
 private void doLogin() throws FailingHttpStatusCodeException, MalformedURLException, IOException, GeneralSecurityException {
	 WebClient wc = new WebClient(BrowserVersion.FIREFOX_3_6);
	 wc.setThrowExceptionOnScriptError(false);
	 wc.setJavaScriptEnabled(false);
	 wc.setUseInsecureSSL(true);
	 wc.setCssEnabled(false);
	 wc.setRedirectEnabled(true);
	 wc.setCookieManager(cookiemanager);
	 final HtmlPage loginpage = wc.getPage(loginurl);
	 HtmlForm loginform = loginpage.getHtmlElementById("form-username");
	 HtmlTextInput login_username = loginform.getInputByName("username");
	 HtmlPasswordInput login_password = loginform.getInputByName("password");
	 HtmlElement button =  loginform.getHtmlElementsByTagName("button").get(0);
	 login_username.setAttribute("value", username);
	 login_password.setAttribute("value", password);
	 HtmlPage response = button.click();
	 wc.closeAllWindows();
	 loggedin = true;
 }
 
  
  
  
  public HtmlPage GetPageContent(String url) throws Exception {
	  return GetPageContent(url,true);
  }
  
  public HtmlPage GetPageContent(String url,boolean needslogin) throws Exception {
	if(needslogin&&!loggedin)
		doLogin();
	 WebClient wc = new WebClient();
	 wc.setThrowExceptionOnScriptError(false);
	 wc.setJavaScriptEnabled(false);
	 wc.setUseInsecureSSL(true);
	 wc.setCssEnabled(false);
	 wc.setRedirectEnabled(true);
	 wc.setCookieManager(cookiemanager);
	 return wc.getPage(url);
	
 
  }
  
  public byte[] GetPdfPage(String url,boolean needslogin) throws Exception {
	//FIXME: should use htmlunit too like the rest of http/html parsing code
	if(needslogin&&!loggedin)
		doLogin();
	WebClient wc = new WebClient(BrowserVersion.FIREFOX_3_6);
	wc.setThrowExceptionOnScriptError(false);
	wc.setJavaScriptEnabled(false);
	wc.setUseInsecureSSL(true);
	wc.setCssEnabled(false);
	wc.setRedirectEnabled(true);
	wc.setCookieManager(cookiemanager);
	System.out.println("Fetching pdf page from : " + url);
	UnexpectedPage page = wc.getPage(url);
	InputStream is = page.getInputStream();
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	int nRead;
	byte[] data = new byte[16384];

	while ((nRead = is.read(data, 0, data.length)) != -1) {
	  buffer.write(data, 0, nRead);
	}

	//buffer.flush();

	return buffer.toByteArray();
  } 
}