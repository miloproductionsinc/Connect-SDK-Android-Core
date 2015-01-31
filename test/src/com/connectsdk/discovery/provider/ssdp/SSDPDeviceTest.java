package com.connectsdk.discovery.provider.ssdp;

import junit.framework.Assert;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.UnknownHostException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by oleksii.frolov on 1/30/2015.
 */
public class SSDPDeviceTest {
	
	String deviceDescription = 
			"<root xmlns=\"urn:schemas-upnp-org:device-1-0\" xmlns:dlna=\"urn:schemas-dlna-org:device-1-0\">\n" +
			"<specVersion>\n" +
			"<major>1</major>\n" +
			"<minor>0</minor>\n" +
			"</specVersion>\n" +
			"<device>\n" +
			"<deviceType>urn:schemas-upnp-org:device:Basic:1</deviceType>\n" +
			"<friendlyName>Adnan TV</friendlyName>\n" +
			"<manufacturer>LG Electronics</manufacturer>\n" +
			"<manufacturerURL>http://www.lge.com</manufacturerURL>\n" +
			"<modelDescription/>\n" +
			"<modelName>LG Smart TV</modelName>\n" +
			"<modelURL>http://www.lge.com</modelURL>\n" +
			"<modelNumber>WEBOS1</modelNumber>\n" +
			"<serialNumber/>\n" +
			"<UDN>uuid:86ea12c3-4ad7-2117-edbd-8177429fe21e</UDN>\n" +
			"<serviceList>\n" +
			"<service>\n" +
			"<serviceType>urn:lge-com:service:webos-second-screen:1</serviceType>\n" +
			"<serviceId>\n" +
			"urn:lge-com:serviceId:webos-second-screen-3000-3001\n" +
			"</serviceId>\n" +
			"<SCPDURL>\n" +
			"/WebOS_SecondScreen/86ea12c3-4ad7-2117-edbd-8177429fe21e/scpd.xml\n" +
			"</SCPDURL>\n" +
			"<controlURL>\n" +
			"/WebOS_SecondScreen/86ea12c3-4ad7-2117-edbd-8177429fe21e/control.xml\n" +
			"</controlURL>\n" +
			"<eventSubURL>\n" +
			"/WebOS_SecondScreen/86ea12c3-4ad7-2117-edbd-8177429fe21e/event.xml\n" +
			"</eventSubURL>\n" +
			"</service>\n" +
			"</serviceList>\n" +
			"</device>\n" +
			"</root>";

	String deviceSmallDescription =
			"<root xmlns=\"urn:schemas-upnp-org:device-1-0\" xmlns:dlna=\"urn:schemas-dlna-org:device-1-0\">\n" +
					"<specVersion>\n" +
					"<major>1</major>\n" +
					"<minor>0</minor>\n" +
					"</specVersion>\n" +
					"<device>\n" +
					"<deviceType>urn:schemas-upnp-org:device:Basic:1</deviceType>\n" +
					"</device>\n" +
					"</root>";

	@Test
	public void testCreateDeviceWithNullUrl() {
		try {
			new SSDPDevice((String)null, null);
			Assert.fail("MalformedURLException should be thrown");
		} catch (MalformedURLException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("MalformedURLException should be thrown");
		}
	}

	@Test
	public void testCreateDeviceWithWrongUrl() {
		try {
			new SSDPDevice("http://unknown.host", null);
			Assert.fail("MalformedURLException should be thrown");
		} catch (UnknownHostException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("MalformedURLException should be thrown");
		}
	}

	@Test
	public void testCreateDeviceFromPlainTextContent() {
		try {
			new SSDPDevice(getMockUrl("plain text", null), null);
			Assert.fail("SAXParseException should be thrown");
		} catch (SAXParseException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("SAXParseException should be thrown");
		}
	}

	@Test
	public void testCreateDeviceFrom() throws IOException, ParserConfigurationException, SAXException {
		SSDPDevice device = new SSDPDevice(getMockUrl(deviceDescription, "http://application_url/"), null);
		Assert.assertEquals("urn:schemas-upnp-org:device:Basic:1", device.deviceType);
		Assert.assertEquals("Adnan TV", device.friendlyName);
		Assert.assertEquals("LG Electronics", device.manufacturer);
		Assert.assertNull(device.modelDescription);
		Assert.assertEquals(deviceDescription, device.locationXML);
		Assert.assertEquals("http://application_url/", device.applicationURL);
		Assert.assertEquals("foo.bar", device.ipAddress);
		Assert.assertEquals(80, device.port);
		Assert.assertEquals("http://foo.bar", device.serviceURI);
		Assert.assertEquals("http://foo.bar:80", device.baseURL);
		Assert.assertEquals("WEBOS1", device.modelNumber);
	}

	@Test
	public void testCreateDeviceFromSmallDescription() throws IOException, ParserConfigurationException, SAXException {
		SSDPDevice device = new SSDPDevice(getMockUrl(deviceSmallDescription, "http://application_url"), null);
		Assert.assertEquals("urn:schemas-upnp-org:device:Basic:1", device.deviceType);
		Assert.assertNull(device.friendlyName);
		Assert.assertNull(device.manufacturer);
		Assert.assertNull(device.modelDescription);
		Assert.assertEquals(deviceSmallDescription, device.locationXML);
		Assert.assertEquals("http://application_url/", device.applicationURL);
		Assert.assertEquals("foo.bar", device.ipAddress);
		Assert.assertEquals(80, device.port);
		Assert.assertEquals("http://foo.bar", device.serviceURI);
		Assert.assertEquals("http://foo.bar:80", device.baseURL);
		Assert.assertNull(device.modelNumber);
	}

	private URL getMockUrl(final String content, String applicationUrl) throws IOException {
		final URLConnection mockConnection = Mockito.mock(URLConnection.class);
		Mockito.when(mockConnection.getInputStream()).thenReturn(new StringInputStream(content));
		Mockito.when(mockConnection.getHeaderField("Application-URL")).thenReturn(applicationUrl);

		final URLStreamHandler handler = new URLStreamHandler() {

			@Override
			protected URLConnection openConnection(final URL arg0)
					throws IOException {
				return mockConnection;
			}
		};
		final URL url = new URL("http", "foo.bar", 80, "", handler);
		return url;
	}
	
}