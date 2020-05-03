package com.harvi.tailor.auth;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.harvi.tailor.entities.JwtToken;
import com.harvi.tailor.entities.UserCredentials;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * https://github.com/jwtk/jjwt
 */
public class AuthUtil {

	private static final Logger LOG = Logger.getLogger(AuthUtil.class.getName());

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String AUTH_HEADER_STRING = "Authorization";

	private static final String ADMIN_USER = "Admin";
	private static final String ADMIN_PASSWORD = "Admin";
	private static final String TRY_USER = "Try";
	private static final String TRY_PASSWORD = "Try";

	// TODO The secret key. This should be in a property file NOT under source
	// control and not hard coded in real life. We're putting it here for
	// simplicity.
	private static String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";

	/**
	 * @param id
	 * @param issuer
	 * @param subject
	 * @param ttlMillis
	 * @return
	 */
	public static JwtToken createJWT(String issuer, String subject, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(UUID.randomUUID().toString()).setIssuedAt(now).setSubject(subject)
				.setIssuer(issuer).signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (ttlMillis > 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return new JwtToken(builder.compact());
	}

	/**
	 * @param jws
	 * @return
	 */
	public static Claims decodeJWT(String jwt) {
		// This line will throw an exception if it is not a signed JWS (as expected)
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY)).parseClaimsJws(jwt)
				.getBody();
		return claims;
	}

	public static boolean isValidJWS(String jws) {
		try {
			Claims claims = decodeJWT(jws);
			return claims != null;
		} catch (Exception e) {
			LOG.warning("Failed to decode jwt: " + e.getStackTrace());
			return false;
		}
	}

	public static boolean validateUserCredentials(UserCredentials userCredentials) {
		if (ADMIN_USER.equals(userCredentials.getUname()) && ADMIN_PASSWORD.equals(userCredentials.getPwd())) {
			return true;
		} else if (TRY_USER.equals(userCredentials.getUname()) && TRY_PASSWORD.equals(userCredentials.getPwd())) {
			return true;
		}
		return false;
	}

}
