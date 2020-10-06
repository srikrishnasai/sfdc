package com.tadigital.sfdc_campaign.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tadigital.sfdc_campaign.constants.StringConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Ravi.sangubotla
 *
 */
public class JWTManager {

	Logger logger = LoggerFactory.getLogger(JWTManager.class);

	/**
	 * Created a JWT based on the configurations
	 * @throws IOException 
	 */
	public String create(String clientId,String clientSecret,String orgId,String techAccountId) throws IOException {

		String jwtToken = null;
		InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("key");
		
		KeyFactory keyFactory;
		try {
			String privateKeyStr = IOUtils.toString(in);
			privateKeyStr = privateKeyStr.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "");
			keyFactory = KeyFactory.getInstance("RSA");

			logger.debug("The key factory algorithm is ::{} ",keyFactory.getAlgorithm());
			byte[] byteArray = privateKeyStr.getBytes();
			logger.debug("The array length is ::{}",byteArray.length);
			byte[] encodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(privateKeyStr);
			KeySpec ks = new PKCS8EncodedKeySpec(encodedBytes);
			String[] metascopes = new String[] { "ent_campaign_sdk" };
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);

			Long expirationTime = System.currentTimeMillis() / 1000 + 86400L;

			Claims claims = Jwts.claims();
			claims.put("iss", orgId);
			claims.put("sub", techAccountId);
			claims.put("aud",
					"https://" + StringConstants.ADOBEIOACCESSTOKENURL + "/c/"+clientId);
			claims.put("exp", expirationTime);
			for (String metascope : metascopes) {
				claims.put("https://" + StringConstants.ADOBEIOACCESSTOKENURL + "/s/" + metascope,
						java.lang.Boolean.TRUE);
			}

			jwtToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, privateKey).compact();

			logger.debug("JWTToken : %s{}" , jwtToken);
			logger.info("JWTToken : %s{}" , jwtToken);
			return verify(clientId,clientSecret, jwtToken);

		} catch (NoSuchAlgorithmException nsae) {
			logger.info("NoSuchAlgorithmException is :{}",nsae);
		} catch (InvalidKeySpecException e) {
			logger.info("InvalidKeySpecException is :{}",e);
		}

		return null;
	}

	/**
	 * 
	 */
	public String verify(String clientId,String clientSecret, String jwt) {
		String accessToken = "";
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpHost authServer = new HttpHost(StringConstants.ADOBEIOACCESSTOKENURL, 443, "https");
		HttpPost authPostRequest = new HttpPost(StringConstants.ADOBEIOJWTTOKENURL);
		authPostRequest.addHeader("Cache-Control", "no-cache");
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_secret",clientSecret ));
		params.add(new BasicNameValuePair("jwt_token", jwt));
		authPostRequest.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
		HttpResponse response;
		try {
			response = httpClient.execute(authServer, authPostRequest);
			if (200 != response.getStatusLine().getStatusCode()) {
				throw new IOException("Server returned error: " + response.getStatusLine().getReasonPhrase());
			}
			HttpEntity entity = response.getEntity();
			org.json.JSONObject jo = new org.json.JSONObject(EntityUtils.toString(entity));
			accessToken = jo.getString("access_token");
			logger.debug("Returning access_token ::{}",accessToken);
			logger.info("Returning access_token ::{}",accessToken);
			return (String) jo.get("access_token");
		} catch (IOException e) {
			logger.info("Exception Occurred ::{} ",e);
			logger.error("Exception Occurred : {}", e);
		}
		return accessToken;
	}

	}
