package tech.mistermel.easierbackup.uploader;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.mistermel.easierbackup.EasierBackup;

public class DropboxUploader implements Uploader {
	
	private static final String CLIENT_ID = "zfcg6xjcts31s75";
	private static final String USER_URL = "https://www.dropbox.com/oauth2/authorize?client_id=%%CLIENTID%%&response_type=code&code_challenge=%%CHALLENGE%%&code_challenge_method=S256";
	private static final String TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";
	
	private final OkHttpClient httpClient = new OkHttpClient();	
	private String codeVerifier, codeChallenge;
	
	private String accessToken, refreshToken;
	
	public DropboxUploader(ConfigurationSection section) {
		this.accessToken = section.getString("accessToken");
	}
	
	public AuthenticationResult getBearerToken(String authorizationCode) {
		if(Bukkit.isPrimaryThread()) {
			EasierBackup.instance().getLogger().warning("getBearerToken() is being run on the primary thread!");
		}
		
		RequestBody body = new FormBody.Builder()
				.add("code", authorizationCode)
				.add("code_verifier", codeVerifier)
				.add("grant_type", "authorization_code")
				.add("client_id", CLIENT_ID)
				.build();
		
		Request request = new Request.Builder()
				.url(TOKEN_URL)
				.post(body)
				.build();
		
		try {
			Response response = httpClient.newCall(request).execute();
			
			if(!response.isSuccessful()) {
				return new AuthenticationResult(false, null, null, 0);
			}
			
			JSONObject json = (JSONObject) new JSONParser().parse(response.body().string());
			response.close();
			
			String accessToken = (String) json.get("access_token");
			String refreshToken = (String) json.get("refresh_token");
			long expiresIn = (long) json.get("expires_in");
			
			AuthenticationResult result = new AuthenticationResult(true, accessToken, refreshToken, System.currentTimeMillis() + expiresIn);
			return result;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String generateUserURL() {
		this.generateCode();
		return USER_URL.replace("%%CLIENTID%%", CLIENT_ID).replace("%%CHALLENGE%%", codeChallenge);
	}
	
	public boolean isAuthenticated() {
		return accessToken != null && refreshToken != null;
	}
	
	/**
	 * Generates both the code verifier and the code challenge.
	 */
	private void generateCode() {
		SecureRandom sr = new SecureRandom();
		byte[] code = new byte[32];
		sr.nextBytes(code);
		this.codeVerifier = Base64.encodeBase64URLSafeString(code);
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(codeVerifier.getBytes());
			byte[] digest = md.digest();
			this.codeChallenge = Base64.encodeBase64URLSafeString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public class AuthenticationResult {
		
		private boolean success;
		
		private String bearerToken, refreshToken;
		private long expiryTime;
		
		public AuthenticationResult(boolean success, String bearerToken, String refreshToken, long expiryTime) {
			this.success = success;
			this.bearerToken = bearerToken;
			this.refreshToken = refreshToken;
			this.expiryTime = expiryTime;
		}
		
		public boolean isSuccess() {
			return success;
		}
		
		public String getBearerToken() {
			return bearerToken;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
		
		public long getExpiryTime() {
			return expiryTime;
		}
		
	}
	
}