package tech.mistermel.easierbackup.uploader;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	private static final String USER_URL = "https://www.dropbox.com/oauth2/authorize?client_id=%%CLIENTID%%&response_type=code&code_challenge=%%CHALLENGE%%&code_challenge_method=S256&token_access_type=offline";
	private static final String TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";
	
	private final OkHttpClient httpClient = new OkHttpClient();	
	private String codeVerifier, codeChallenge;
	
	private String accessToken, refreshToken;
	private long expiryTime;
	
	private File dataFile;
	private FileConfiguration data;
	
	public DropboxUploader() {
		this.dataFile = new File(EasierBackup.instance().getDataFolder(), "dropbox.yml");
		if(!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				EasierBackup.instance().getLogger().log(Level.SEVERE, "Error occurred while attempting to create dropbox.yml file", e);
			}
		}
		this.data = YamlConfiguration.loadConfiguration(dataFile);
	}
	
	private void saveAuthenticationData() {
		data.set("accessToken", accessToken);
		data.set("refreshToken", refreshToken);
		data.set("expiryTime", expiryTime);
		
		this.saveDataFile();
	}
	
	private void saveDataFile() {
		try {
			data.save(dataFile);
		} catch (IOException e) {
			EasierBackup.instance().getLogger().log(Level.SEVERE, "Error occurred while attempting to save dropbox.yml file", e);
		}
	}
	
	public boolean authenticate(String authorizationCode) {
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
				return false;
			}
			
			JSONObject json = (JSONObject) new JSONParser().parse(response.body().string());
			response.close();
			
			this.accessToken = (String) json.get("access_token");
			this.refreshToken = (String) json.get("refresh_token");
			this.expiryTime = System.currentTimeMillis() + ((long) json.get("expires_in") * 1000);
			
			this.saveAuthenticationData();
			
			return true;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return false;
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
	
}
